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
    static boolean checkImagePainted=false;

    ImageView view = null;
    PlayGame that = null;
    static long startTime = 0, endTime = 0;
    static int correctResponses = 0;
    static ArrayList<TestSubjectResults> testSubjectResults = new ArrayList<TestSubjectResults>(); ;
    static Thread thread=null;
    ProgressDialog dialog = null;
    private Animation animZoomIn = null;
    private Animation animZoomOut = null, animNormal = null;
    LinearLayout linearLayout;
    final Semaphore semaphore=new Semaphore(0,true);

    public void initialiseValues(){
        totalCorrectResponse=0;
        totalwrongResponse=0;
        unattemptedQuestions=0;
        totalTimeTaken=0;
        totalQuestions=0;
        nextImageNeeded = false;
        gameOver = false;
        paintInPostExecuteNeeded = true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initialiseValues();
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


        loadAnimaions();
        startPlayingTheGame();
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

        thread =new Thread(new Runnable() {
            @Override
            public void run() {

                paintImages();

            }
        });
        thread.start();




    }


    synchronized public void paintNextImage(Semaphore sema) {
        final Semaphore semaphore=sema;
        if(!gameOver) {

            runOnUiThread(new Runnable() {
                public void run() {
                    view.startAnimation(animNormal);

                    if (testSubjectResults.get(nextCounter).isPositive) {
                        if (testSubjectResults.get(nextCounter).backgroundColor.equalsIgnoreCase("#"))
                            testSubjectResults.get(nextCounter).backgroundColor = ParameterFile.positiveColor;

                        linearLayout.setBackgroundColor(Color.parseColor(testSubjectResults.get(nextCounter).backgroundColor));
                    } else {
                        if (testSubjectResults.get(nextCounter).backgroundColor.equalsIgnoreCase("#"))
                            testSubjectResults.get(nextCounter).backgroundColor = ParameterFile.negativeColor;
                        linearLayout.setBackgroundColor(Color.parseColor(testSubjectResults.get(nextCounter).backgroundColor));
                    }
                    view.setImageBitmap(testSubjectResults.get(nextCounter).image);

                    nextCounter++;
                    nextImageNeeded = false;
                    dialog.dismiss();

                    paintInPostExecuteNeeded = false;
                    startTime = System.nanoTime();
                    checkImagePainted = true;

                }

            });


        }
    }


    public void paintImages() {

        while (!gameOver) {

            if (nextCounter>= ParameterFile.totalGames) {
                gameOver=true;

                buildReport();
                break;
            }

            if(nextImageNeeded && ((nextCounter) > (testSubjectResults.size() - 1))){

                paintInPostExecuteNeeded=true;
                dialog.dismiss();
            }
            if (((nextCounter==0 && testSubjectResults.size()>1 )|| nextImageNeeded)) {

                paintNextImage(semaphore);
                try{

                    semaphore.acquire();
                    checkImagePainted=false;

                }catch (Exception e){

                    e.printStackTrace();;
                }
            }

            if (paintInPostExecuteNeeded && !dialog.isShowing()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Please wait while next image is being fetched");
                        dialog.show();
                    }
                });


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

      //  checkForNextImage();
        return false;
    }

    public void checkForNextImage() {
        if (nextCounter < ParameterFile.totalGames) {
            if (nextCounter-1 <testSubjectResults.size() - 1) {
                nextImageNeeded = true;
            } else {
                paintInPostExecuteNeeded = true;
                dialog.dismiss();

            }
        }else{
            gameOver=true;
            buildReport();
        }
    }


    public void buildReport(){

        totalQuestions=testSubjectResults.size();

        ParameterFile.isGamePlayed=true;
        Semaphore semaphore=new Semaphore(0,true);
        new SendFeedback(semaphore).execute();
        gameOver=true;
        nextCounter = 0;
        new FetchParameter().execute();
        testSubjectResults.clear();
        testSubjectResults = new ArrayList<TestSubjectResults>();
        ParameterFile.QuestionSession=1;
        Intent intent=new Intent(PlayGame.this,Questions.class);
        intent.putExtra("isQuestion",true);
        PlayGame.this.startActivity(intent);

    }


    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {


        TestSubjectResults results = testSubjectResults.get(nextCounter-1);

        double time=(System.nanoTime()-startTime)/(Math.pow(10,9));


        try{
            if (e1.getY() - e2.getY() <- 10) {
                fingerSwipedUp();
                if((time)>ParameterFile.time && nextCounter>0 && ((nextCounter-1) < (testSubjectResults.size() - 1))){

                    unattemptedQuestions++;
                    semaphore.release();
                    return false;
                }



                results.responseAccurate = results.isPositive == true ? true : false;
                endTime = System.nanoTime();
                results.time = (endTime - startTime);

            }
            if (e1.getY() - e2.getY() > 10) {
                fingerSwipeDown();
                if((time)>ParameterFile.time && nextCounter>0 && ((nextCounter-1) < (testSubjectResults.size() - 1))){

                    unattemptedQuestions++;
                    semaphore.release();

                    return false;

                }

                results.responseAccurate = results.isPositive == false ? true : false;

                endTime = System.nanoTime();
                results.time = (endTime - startTime);
            }}catch (Exception e){
            e.printStackTrace();
        }
        results.isAttempted = true;
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

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

        checkForNextImage();

        semaphore.release();

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    protected void onDestroy(){
        super.onDestroy();
        System.out.println("Done on Destroy");

    }

    public void onBackPressed() {

        return;
    }

}
