package project.msd.teenviolence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by surindersokhal on 1/30/16.
 */
public class PlayGame extends Activity implements GestureDetector.OnGestureListener, Animation.AnimationListener {

    static int nextCounter = 0;

    static int totalCorrectResponse=0;
    static int totalwrongResponse=0;
    static int unattemptedQuestions=0;
    static long totalTimeTaken=0;
    static int totalQuestions=0;
    static boolean nextImageNeeded = false;
    static boolean gameOver = false;
    static boolean paintInPostExecuteNeeded = true;
    GestureDetector detector = null;

    ImageView view = null;
    PlayGame that = null;
    long startTime = 0, endTime = 0;
    int correctResponses = 0;
    static ArrayList<TestSubjectResults> testSubjectResults = new ArrayList<TestSubjectResults>(); ;

    ProgressDialog dialog = null;
    private Animation animZoomIn = null;
    private Animation animZoomOut = null, animNormal = null;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.play_game);
        dialog = new ProgressDialog(this);
        that = this;

        linearLayout=(LinearLayout)findViewById(R.id.layoutID);
        linearLayout.setBackgroundColor(Color.rgb(12, 12, 12));
        view = (ImageView) findViewById(R.id.imageView);
        detector = new GestureDetector(this, this);

        startPlayingTheGame();
        loadAnimaions();
    }

    public void loadAnimaions(){
        animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_in);
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_out);
        animNormal = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_normal);
        animZoomIn.setAnimationListener(this);
        animZoomOut.setAnimationListener(this);
    }

    public void fingerSwipedUp() {
        view.startAnimation(animZoomIn);
    }

    public void fingerSwipeDown() {
        view.startAnimation(animZoomOut);
    }

    public void startPlayingTheGame() {

        new Thread(){public  void run() {
            if (testSubjectResults.size() < ParameterFile.totalGames) {

                Login_Activity.fetchImagesExecutorService();
            }

            paintImages();
        }}.start();;


    }


    public void paintNextImage() {

        runOnUiThread(new Runnable() {
            public void run() {
                view.startAnimation(animNormal);
                if(testSubjectResults.get(nextCounter).isPositive){
                    linearLayout.setBackgroundColor(Color.GREEN);
                }else{
                    linearLayout.setBackgroundColor(Color.RED);
                }
                view.setImageBitmap(testSubjectResults.get(nextCounter).image);
            }
        });

        nextCounter++;
        nextImageNeeded = false;
        dialog.dismiss();
        paintInPostExecuteNeeded=false;
        startTime = System.nanoTime();
    }


    public void paintImages() {
        while (!gameOver) {
            if (nextCounter>= ParameterFile.totalGames) {
                gameOver=true;
                
                buildReport();
                break;
            }



            if((System.nanoTime()-startTime)>ParameterFile.time && nextCounter>0 && ((nextCounter-1) < (testSubjectResults.size() - 1))){
                System.out.println("Time is greater");
                unattemptedQuestions++;
                testSubjectResults.get(nextCounter-1).isAttempted=false;
                nextImageNeeded=true;
            }

            if(nextImageNeeded && ((nextCounter) > (testSubjectResults.size() - 1))){
                paintInPostExecuteNeeded=true;
                dialog.dismiss();
            }
            if ((nextCounter==0 && testSubjectResults.size()>1 )|| nextImageNeeded) {
                paintNextImage();
            }

            if(paintInPostExecuteNeeded && !dialog.isShowing()){
                dialog.setMessage("Please wait while next image is being fetched");
                dialog.show();

            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent me) {
        this.detector.onTouchEvent(me);
        return super.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d("---onDown----", e.toString());
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        Log.d("---onFling---", e1.toString() + e2.toString());
        TestSubjectResults results = testSubjectResults.get(nextCounter-1);
        results.isAttempted = true;
        try{
        if (e1.getY() - e2.getY() > 10) {
            fingerSwipedUp();
            results.responseAccurate = results.isPositive == true ? true : false;
        }
        if (e1.getY() - e2.getY() < -10) {
            fingerSwipeDown();

            results.responseAccurate = results.isPositive == false ? true : false;
        }}catch (Exception e){
                e.printStackTrace();
        }
        endTime = System.nanoTime();
        results.time = (endTime - startTime);
      //  checkForNextImage();
        return false;
    }

    public void checkForNextImage() {
        if (nextCounter <= ParameterFile.totalGames) {
            if (nextCounter-1 < testSubjectResults.size() - 1) {
                nextImageNeeded = true;
            } else {
                paintInPostExecuteNeeded = true;
                dialog.dismiss();

            }
        }else{
            gameOver=true;
            runOnUiThread(new Runnable() {
                public void run() {
                    view.startAnimation(animNormal);
                }});
            buildReport();
        }
    }


    public void buildReport(){
        totalQuestions=testSubjectResults.size();
        ParameterFile.isGamePlayed=true;
        SendFeedback feedback=new SendFeedback();
        for(TestSubjectResults result:testSubjectResults){
            getCorrect_IncorrectResponses(result);
            totalTimeTaken+=result.time;
            feedback.execute(result.isAttempted+"",result.time+"",result.isPositive+"",result.backgroundColor,result.responseAccurate+"");
            System.out.println("Surinder feedback: " + result.isAttempted + " " + result.time + " " + result.isPositive + " " + result.imageName + " " + result.backgroundColor);
        }

        testSubjectResults = new ArrayList<TestSubjectResults>(); ;
        new FetchParameter().execute();
        new Thread(){public  void run() {
            if (testSubjectResults.size() < ParameterFile.totalGames) {

                Login_Activity.fetchImagesExecutorService();
            }}}.start();
        Intent intent=new Intent(PlayGame.this,HomeScreen.class);
        intent.putExtra("text","Thank you for playing.");
        PlayGame.this.startActivity(intent);

    }

    public void getCorrect_IncorrectResponses(TestSubjectResults result){
        if(result.isAttempted){
            if(result.responseAccurate)
                correctResponses++;
            if(!result.responseAccurate)
                totalwrongResponse++;
        }
    }
    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("---onLongPress---", e.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        Log.d("---onScroll---", e1.toString() + e2.toString());
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d("---onShowPress---", e.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("---onSingleTapUp---", e.toString());
        return false;
    }


    //gets the current orientation of the phone.
    public int getScreenOrientation() {
        final int rotation = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 1;
            case Surface.ROTATION_180:
                return 2;
            default:
                return 3;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        Log.i("Animation", "start");
     }

    @Override
    public void onAnimationEnd(Animation animation) {
        Log.i("Animation", "end");
        try{
            Thread.sleep(400);}catch (Exception e){
            e.printStackTrace();
        }
        checkForNextImage();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void buildAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("User verification failed");

        // set dialog message
        alertDialogBuilder
                .setMessage("Invalid username or pasword")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    protected void onDestroy(){
        super.onDestroy();
        System.out.println("Done on Destroy");
        Login_Activity.outputFile.delete();

    }

    public void onBackPressed() {

        return;
    }

}
