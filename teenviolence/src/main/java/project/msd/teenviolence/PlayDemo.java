package project.msd.teenviolence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Semaphore;

public class PlayDemo extends Activity implements View.OnClickListener {

    VideoView videoView=null;
    Button skipButton=null;
    boolean isSkipEnable=true;

    public void onBackPressed() {

        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_demo);
        videoView=(VideoView)(findViewById(R.id.videoView));
        skipButton=(Button)(findViewById(R.id.skipButton));
        Intent intent = getIntent();
        isSkipEnable=intent.getBooleanExtra("skipEnabled",true);
        skipButton.setOnClickListener(this);

        if(!isSkipEnable){
            skipButton.setVisibility(View.INVISIBLE);
            skipButton.setClickable(false);
        }


        startVideo();
    }






    public void startVideo(){

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        Uri video = Uri.parse("http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/ParameterServlet?queryType=video");
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(!isSkipEnable){
                    finish();
                }else{
                createNewActivity();}

            }
        });
    }

    public void createNewActivity(){
        Intent intent=new Intent(PlayDemo.this,DemoColorActivity.class);
        intent.putExtra("speed", 100);
        PlayDemo.this.startActivity(intent);
    }
    @Override
    public void onClick(View v) {
        videoView.stopPlayback();

        createNewActivity();

    }
    protected void onDestroy(){
        super.onDestroy();
        System.out.println("Done on Destroy");
    }
}
