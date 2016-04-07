package project.msd.teenviolence;

import android.app.Activity;
import android.app.ProgressDialog;
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
    Semaphore semaphore=new Semaphore(0,true);
    ProgressDialog progressDialog=null;
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
            skipButton.setClickable(false);
        }
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Downloading demo");
        progressDialog.show();
        playVideo();
    }

    public void playVideo(){
        try{
            int tries=0;

            while(!Login_Activity.isDownloadComplete && tries<100000){
                if(!Login_Activity.isDownloadStarted){
                    Login_Activity.downloadDemoVideo();
                }
             tries++;
            }
            if(Login_Activity.isDownloadComplete){
                progressDialog.dismiss();
                startVideo();
            }
            if(tries>=100000){
                progressDialog.setMessage("Unable to download video");
                Thread.sleep(1000);
                progressDialog.dismiss();
                return;
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startVideo(){
        System.out.println(Login_Activity.outputFile.getPath());
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        Uri video = Uri.parse(Login_Activity.outputFile.getPath());
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
        Intent intent=new Intent(PlayDemo.this,PlayGame.class);
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
        Login_Activity.outputFile.delete();

    }
}
