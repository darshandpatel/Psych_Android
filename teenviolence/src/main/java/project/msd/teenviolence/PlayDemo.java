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

public class PlayDemo extends Activity implements View.OnClickListener {

    VideoView videoView=null;
    Button skipButton=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_demo);
        videoView=(VideoView)(findViewById(R.id.videoView));
        skipButton=(Button)(findViewById(R.id.skipButton));
        skipButton.setOnClickListener(this);
        playVideo();
    }

    public void playVideo(){
        try{
            ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Verifying the user");
            progressDialog.show();

            progressDialog.dismiss();
            System.out.println(Login_Activity.outputFile.getPath());
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);

            Uri video = Uri.parse(Login_Activity.outputFile.getPath());
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.start();
            /*
            videoView.setVideoPath(Login_Activity.outputFile.getPath());
            videoView.start();*/

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                        Intent intent=new Intent(PlayDemo.this,PlayGame.class);
                        intent.putExtra("speed",100);
                        PlayDemo.this.startActivity(intent);

                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onClick(View v) {

    }
}
