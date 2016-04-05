package project.msd.teenviolence;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.view.ViewGroup.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.concurrent.Semaphore;

public class Questions extends AppCompatActivity implements View.OnClickListener {


    Button button=null;
    LinearLayout layout=null;
    EditText[] edits;
    TextView[] questionsViews;
    TextView[] expertViews;
    SeekBar[] bars;
    final static String QUESTION_URL="http://ec2-52-38-37-183.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/Question";
    static Questions questions=null;
    Semaphore semaphore=new Semaphore(0,true);

    boolean demoPlayed=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionsViews=new TextView[10];
        expertViews=new TextView[5];
        bars=new SeekBar[5];
        edits=new EditText[5];
        setContentView(R.layout.activity_questions);
        Intent intent=new Intent();
        demoPlayed=intent.getBooleanExtra("demoNeeded",true);
        // layout=(LinearLayout)findViewById(R.id.scrol);
        MenuPouplateItems.questions=this;
        initialiseViews();
        button=(Button)findViewById(R.id.submit);
        button.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.help:
                MenuPouplateItems.showHelp();
                return true;
            case R.id.logout:
                MenuPouplateItems.logout();
                return true;
            case R.id.feedback:
                MenuPouplateItems.showFeedback();
                return true;
            case R.id.demo:
                MenuPouplateItems.showDemo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void initialiseViews(){
        for(int i=1;i<=5;i++){
            intialisQuesitons(i);
            initialiseBars(i);
        }
        for(int i=6;i<=10;i++){
            intialisQuesitons(i);
            int tID2=getResources().getIdentifier("edit"+(i-5),"id","project.msd.teenviolence");
            EditText edit=(EditText)findViewById(tID2);
            edits[i-6]=edit;
        }
    }

    public void intialisQuesitons(int i){
        int tID=getResources().getIdentifier("q"+i,"id","project.msd.teenviolence");
        TextView view=(TextView)findViewById(tID);
        questionsViews[i-1]=view;
    }

    public void initialiseBars(int i){
        int seekID=getResources().getIdentifier("seekBar"+i,"id","project.msd.teenviolence");
        SeekBar seekBar=(SeekBar)findViewById(seekID);
        int tID2=getResources().getIdentifier("seekBar"+i+"TextView","id","project.msd.teenviolence");
        TextView view2=(TextView)findViewById(tID2);
        setSeekBarChangedListeners(seekBar,view2);
        expertViews[i-1]=view2;
        bars[i-1]=seekBar;
    }

    public void setSeekBarChangedListeners(SeekBar seekBar,final TextView textView){
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

    class fetchQuestions extends AsyncTask<String,Void,String>{

        protected String doInBackground(String... parms){
            String array="Enter name\nEnter age\nEnter gender\nEnter bale bale";
            return array;
        }
        protected void onPostExecute(String questions){
            String questionArray[]=questions.split("\n");
            displayQuestions(questionArray);
            semaphore.release();
            displayButton();
        }
    }


    public void populateQuestions(String[] questions){
        for(int i=0;i<10;i++){
            questionsViews[i].setText(i+") "+questions[i]);
        }
    }
    public void displayButton(){
        LinearLayout lLayout=new LinearLayout(this);
        lLayout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lparams.weight=1;

        button=new Button(this);
        button.setText("Continue");
        button.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        lLayout.addView(button);
        button.setOnClickListener(this);
        layout.addView(lLayout);

    }
    class SendFeedback extends AsyncTask<String,Void,InputStream>{

        protected InputStream doInBackground(String... parms){
            InputStream stream=null;
            try {
                String feedback = parms[0];
                stream = BuildConnections.buildConnection(QUESTION_URL + "?feedback=" + feedback);
            }catch(Exception e){
                e.printStackTrace();
            }
            return stream;

        }

        protected void onPostExecute(InputStream stream){
            JSONObject jsonObject=BuildConnections.getJSOnObject(stream);
            semaphore.release();

           /* try{

                String val=jsonObject.getString("result");
                if(val.equalsIgnoreCase("success"))
                    startNewActivity();
                else
                    buildAlertDialog(jsonObject.getString("message"));
            }catch (JSONException e){
                e.printStackTrace();
            }*/
        }
    }

    public void buildAlertDialog(String message){

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(questions);
        alertDialogBuilder.setTitle("Error in saving feedback");
        boolean check=false;
        // set dialog message
        alertDialogBuilder
                .setMessage(message+"\nPlease try again")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void startNewActivity(Class classToBePlayed){
        Intent intent=new Intent(Questions.this,classToBePlayed);
        intent.putExtra("speed",100);
        Questions.this.startActivity(intent);
    }

    public String getResults(){
        String result="{\"feedback\":[";

        for(int i=0;i<5;i++){
            String jsonPart="{\"question\":\""+questionsViews[i].getText().toString()+"\",\"answer\":\""+bars[i].getProgress()+"\"},";
            result+=jsonPart;
        }
        for(int i=5;i<9;i++){
            String jsonPart="{\"question\":\""+questionsViews[i].getText().toString()+"\",\"answer\":\""+edits[i-5].getText()+"\"},";
            result+=jsonPart;
        }
        String jsonPart="{\"question\":\""+questionsViews[9].getText().toString()+"\",\"answer\":\""+edits[4].getText()+"\"}]";
        result+=jsonPart;
        return result;

    }
    public void onBackPressed() {

        return;
    }
    @Override
    public void onClick(View view){
        if(demoPlayed){
        startNewActivity(PlayDemo.class);
        }else{
            startNewActivity(PlayGame.class);
        }
        /*
        new SendFeedback().execute(getResults());
        try{
        semaphore.acquire();
        }catch (Exception e){
            e.printStackTrace();
        }*/

    }
    public void displayQuestions(String questions[]){
        layout.removeAllViews();
        edits=new EditText[questions.length];
        LinearLayout lLayout=null;
        for(int i=0;i<questions.length;i++){
            lLayout=new LinearLayout(this);
            lLayout.setOrientation(LinearLayout.VERTICAL);


            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            lparams.weight=1;
            lparams.setMargins(0,10,0,0);
            TextView tView=buildTextView(i,questions[i],lparams);

            EditText eView=buildEditView(lparams);


            lLayout.addView(tView);
            lLayout.addView(eView);
            edits[i]=eView;
            layout.addView(lLayout);
        }



    }

    public EditText buildEditView(LayoutParams layoutParams){
        EditText edit_text = new EditText(this);
        edit_text.canScrollHorizontally(0);
        edit_text.setMaxLines(100);
        edit_text.setLayoutParams(layoutParams);

        edit_text.setHint("Please enter your response");
        return edit_text;
    }
    public TextView buildTextView(int i,String question,LayoutParams lparams){
        TextView edit_text = new TextView(this);
        edit_text.canScrollHorizontally(0);
        edit_text.setMaxLines(100);
        edit_text.setLayoutParams(lparams);
        edit_text.setText(i+") " + question);
        return edit_text;
    }
}
