package project.msd.teenviolence;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

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
    EditText[] edits;
    TextView[] questionsViews;
    TextView[] expertViews;
    SeekBar[] bars;
    final static String QUESTION_URL = "http://ec2-52-38-37-183.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/questionnaire/Questionnaire";
    static Questions questions = null;
    Semaphore semaphore = new Semaphore(0, true);

    boolean demoPlayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionsViews = new TextView[10];
        expertViews = new TextView[5];
        bars = new SeekBar[5];
        edits = new EditText[5];
        setContentView(R.layout.activity_questions);
        Intent intent = new Intent();
        demoPlayed = intent.getBooleanExtra("demoNeeded", true);
        // layout=(LinearLayout)findViewById(R.id.scrol);


        new fetchQuestions().execute();
        initialiseViews();
        button = (Button) findViewById(R.id.submit);
        button.setOnClickListener(this);

    }

    public void initialiseViews() {
        for (int i = 1; i <= 5; i++) {
            intialisQuesitons(i);
            initialiseBars(i);
        }
        for (int i = 6; i <= 10; i++) {
            intialisQuesitons(i);
            int tID2 = getResources().getIdentifier("edit" + (i - 5), "id", "project.msd.teenviolence");
            EditText edit = (EditText) findViewById(tID2);
            edits[i - 6] = edit;
        }
    }

    public void intialisQuesitons(int i) {
        int tID = getResources().getIdentifier("q" + i, "id", "project.msd.teenviolence");
        TextView view = (TextView) findViewById(tID);
        questionsViews[i - 1] = view;
    }

    public void initialiseBars(int i) {
        int seekID = getResources().getIdentifier("seekBar" + i, "id", "project.msd.teenviolence");
        SeekBar seekBar = (SeekBar) findViewById(seekID);
        int tID2 = getResources().getIdentifier("seekBar" + i + "TextView", "id", "project.msd.teenviolence");
        TextView view2 = (TextView) findViewById(tID2);
        setSeekBarChangedListeners(seekBar, view2);
        expertViews[i - 1] = view2;
        bars[i - 1] = seekBar;
    }

    public void setSeekBarChangedListeners(SeekBar seekBar, final TextView textView) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progress < 2) {
                    textView.setText("Denied");
                }
                if (progress >= 2 && progress < 4) {
                    textView.setText("Not sure");
                }
                if (progress >= 4) {
                    textView.setText("Extremely confident");
                }

            }
        });

    }

    class fetchQuestions extends AsyncTask<Void, Void, String[]> {

        protected String[] doInBackground(Void... parms) {
            String questions[] = null;
            try {
                InputStream stream = BuildConnections.buildConnection(QUESTION_URL + "" +
                        "?requestType=request&questionSession="+ParameterFile.QuestionSession);

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

        protected void onPostExecute(String[] questions) {
            populateQuestions(questions);
            semaphore.release();

        }
    }


    public void populateQuestions(String[] questions) {
        for (int i = 0; i < 10; i++) {
            questionsViews[i].setText(i + ") " + questions[i]);
        }
    }

    class SendFeedback extends AsyncTask<String, Void, InputStream> {

        protected InputStream doInBackground(String... parms) {
            InputStream stream = null;
            try {
                String feedback = parms[0];
                stream = BuildConnections.buildConnection(QUESTION_URL + "?requestType=feedback" + feedback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stream;

        }

        protected void onPostExecute(InputStream stream) {
            JSONObject jsonObject = BuildConnections.getJSOnObject(stream);
            try {

                String val = jsonObject.getString("sucess");
                if (val.equalsIgnoreCase("1")) {
                    if (demoPlayed) {
                        startNewActivity(PlayDemo.class);
                    } else {
                        startNewActivity(PlayGame.class);
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
        String questions="";
        String answers="";
        for (int i = 0; i < 12; i++) {
            questions+="&question="+questionsViews[i].getText().toString();
            answers+="&answer="+bars[i].getProgress();

        }


        return questions+answers+"&userID="+ParameterFile.userID+"&sessionID="+ParameterFile.sessionID+
                "sessionDate"+(new Date()).toString();

    }

    public void onBackPressed() {

        return;
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

    protected void onDestroy(){
        super.onDestroy();
        System.out.println("Done on Destroy");
        Login_Activity.outputFile.delete();

    }



}
