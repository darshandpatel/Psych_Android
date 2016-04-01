package project.msd.teenviolence;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.Semaphore;

public class Questions extends AppCompatActivity implements View.OnClickListener {

    Semaphore semaphore=null;
    Button button=null;
    LinearLayout layout=null;
    EditText[] edits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        semaphore=new Semaphore(0,true);
        layout=(LinearLayout)findViewById(R.id.scrol);
        new Thread(){
            public void run(){
                try{
                    new fetchQuestions().execute("");
                    semaphore.acquire();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
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

    @Override
    public void onClick(View view){
        Intent intent=new Intent(Questions.this,PlayDemo.class);
        intent.putExtra("speed",100);
        Questions.this.startActivity(intent);
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
