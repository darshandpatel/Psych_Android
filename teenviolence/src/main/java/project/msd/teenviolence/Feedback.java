package project.msd.teenviolence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Feedback extends AppCompatActivity implements View.OnClickListener{


    TextView username,sessionID,responseTime,correct,wrong,total,totalAsked;
    Button b1;
    String user="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        user=intent.getStringExtra("username");
        if(ParameterFile.isGamePlayed){
            setContentView(R.layout.activity_feedback);
            initialiseTextViews();
            setTextValues();
        }
        else{
            setContentView(R.layout.no_game_played_feedback);
        }
        b1=(Button)(findViewById(R.id.button));
        b1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public void setTextValues(){
        DecimalFormat df = new DecimalFormat("0.00");
        username.setText(ParameterFile.userName);
        sessionID.setText(ParameterFile.sessionID+"");
        responseTime.setText(df.format(PlayGame.totalTimeTaken/(PlayGame.totalQuestions))+" millisecond");
        correct.setText(PlayGame.totalCorrectResponse+"");
        wrong.setText(PlayGame.totalwrongResponse+"");
        total.setText(PlayGame.unattemptedQuestions+"");
        totalAsked.setText(PlayGame.totalQuestions + "");

    }

    public void initialiseTextViews(){
        username=(TextView)findViewById(R.id.username);
        sessionID=(TextView)findViewById(R.id.session);
        responseTime=(TextView)findViewById(R.id.response);
        correct=(TextView)findViewById(R.id.correct);
        wrong=(TextView)findViewById(R.id.wrong);
        total=(TextView)findViewById(R.id.total);
        totalAsked=(TextView)findViewById(R.id.questions);

    }
    public void onBackPressed() {

        return;
    }
    protected void onDestroy(){
        super.onDestroy();
        System.out.println("Done on Destroy");


    }
}
