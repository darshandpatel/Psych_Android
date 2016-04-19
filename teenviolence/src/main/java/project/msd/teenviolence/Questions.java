package project.msd.teenviolence;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.SeekBar;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.Semaphore;

public class Questions extends AppCompatActivity implements View.OnClickListener {


    Button button = null;
    LinearLayout layout = null;
    SeekBar[] edits;
    TextView[] questionsViews;

    boolean isQuestion;
    final static String QUESTION_URL = "http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/questionnaire/Questionnaire";
    static Questions questions = null;
    Semaphore semaphore = new Semaphore(0, true);
    ProgressDialog progressDialog = null;
    boolean demoPlayed = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        semaphore = new Semaphore(0, true);
        layout = (LinearLayout) findViewById(R.id.scrol);
        Intent intent = new Intent();
        demoPlayed = intent.getBooleanExtra("demoNeeded", true);
        if (intent.hasExtra("isQuestion")) {
            isQuestion = intent.getBooleanExtra("isQuestion", false);
        }
        new fetchQuestions().execute();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while questions are being fetched");
        progressDialog.show();
        ;
    }

    class fetchQuestions extends AsyncTask<String, Void, String[]> {

        protected String[] doInBackground(String... parms) {
            String questions[] = null;
            try {
                InputStream stream = BuildConnections.buildConnection(QUESTION_URL + "" +
                        "?requestType=request&questionSession=" + ParameterFile.QuestionSession);

                JSONObject object = BuildConnections.getJSOnObject(stream);
                JSONArray array = object.getJSONArray("questions");
                questions = new String[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    questions[i] = array.getJSONObject(i).getString("question");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return questions;
        }

        protected void onPostExecute(String questions[]) {
            displayQuestions(questions);
            semaphore.release();
            displayButton();
            progressDialog.dismiss();
        }
    }

    public void displayButton() {
        LinearLayout lLayout = new LinearLayout(this);
        lLayout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lparams.weight = 1;

        button = new Button(this);
        button.setText("Continue");
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        lLayout.addView(button);
        button.setOnClickListener(this);
        layout.addView(lLayout);

    }

    @Override
    public void onClick(View view) {

        new SendFeedback().execute(getResults());
        try {
            semaphore.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void createNewActivity(Class activity, String text) {
        Intent intent = new Intent(Questions.this, activity);
        intent.putExtra("speed", 100);
        intent.putExtra("text", text);
        Questions.this.startActivity(intent);
    }

    public void displayQuestions(String questions[]) {
        layout.removeAllViews();
        edits = new SeekBar[questions.length];
        questionsViews = new TextView[questions.length];
        LinearLayout lLayout = null;
        for (int i = 0; i < questions.length; i++) {
            lLayout = new LinearLayout(this);
            lLayout.setOrientation(LinearLayout.VERTICAL);


            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.weight = 1;
            lparams.setMargins(0, 10, 0, 0);
            TextView tView = buildTextView(i, questions[i], lparams);

            SeekBar eView = buildEditView(lparams);


            lLayout.addView(tView);
            lLayout.addView(eView);
            edits[i] = eView;
            questionsViews[i] = tView;
            layout.addView(lLayout);
        }
    }


    public SeekBar buildEditView(ViewGroup.LayoutParams layoutParams) {
        SeekBar bar = new SeekBar(this);
        bar.setMax(5);
        bar.setBackground(getDrawable(R.drawable.edit_text));
        bar.setLayoutParams(layoutParams);
        return bar;
    }

    public TextView buildTextView(int i, String question, ViewGroup.LayoutParams lparams) {
        TextView edit_text = new TextView(this);
        edit_text.canScrollHorizontally(0);
        edit_text.setTextColor(Color.WHITE);
        edit_text.setMaxLines(100);
        edit_text.setLayoutParams(lparams);
        edit_text.setText(i + 1 + ") " + question);
        return edit_text;
    }


    class SendFeedback extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... parms) {
            JSONObject object = null;
            try {

                String feedback = parms[0];
                System.out.println("feedback " + feedback);

                InputStream stream = BuildConnections.buildConnection(QUESTION_URL + "?requestType=feedback" + feedback);
                object = BuildConnections.getJSOnObject(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;

        }

        protected void onPostExecute(JSONObject jsonObject) {

            try {

                String val = jsonObject.getString("save");
                if (val.equalsIgnoreCase("successful")) {

                    if (ParameterFile.QuestionSession == 1) {
                        createNewActivity(HomeScreen.class, "Thanks for playing\n"+ParameterFile.userName);
                    } else {


                        if (demoPlayed) {
                            startNewActivity(PlayDemo.class);
                        } else {
                            startNewActivity(PlayGame.class);
                        }
                    }
                } else
                    buildAlertDialog(jsonObject.getString("message" + "\nPlease try again."));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void buildAlertDialog(String message) {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(questions);
        alertDialogBuilder.setTitle("Error in saving feedback");
        boolean check = false;
        // set dialog message
        alertDialogBuilder
                .setMessage(message + "\nPlease try again")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void startNewActivity(Class classToBePlayed) {
        Intent intent = new Intent(Questions.this, classToBePlayed);
        intent.putExtra("speed", 100);
        Questions.this.startActivity(intent);
    }

    public String getResults() {
        String result = "{\"feedback\":[";
        String questions = "";
        String answers = "";
        for (int i = 0; i < questionsViews.length; i++) {
            String val = questionsViews[i].getText().toString().trim();
            val = Register.encodeString(val.substring(val.indexOf(")") + 1, val.length()));
            questions += "&question=" + val;
            String answer = getAnswer(edits[i].getProgress());
            answers += "&answer=" + answer;
        }


        return questions + answers + "&userID=" + ParameterFile.userID + "&sessionID=" + ParameterFile.sessionID +
                "&sessionDate=" + (new Date()).toString() + "&questionSession=" + ParameterFile.QuestionSession;

    }

    public String getAnswer(int progress) {
        String result = "";
        switch (progress) {
            case 0:
                result = Register.encodeString("Very Slightly or not at all");
                break;
            case 1:
                result = Register.encodeString("A little");
                break;
            case 2:
                result = Register.encodeString("Moderatly");
                break;
            case 3:
                result = Register.encodeString("Quite a bit");
                break;
            case 4:
                result = Register.encodeString("Extremely");
                break;
        }
        return result;
    }

    public void onBackPressed() {

        return;
    }


    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Done on Destroy");
    }


}
