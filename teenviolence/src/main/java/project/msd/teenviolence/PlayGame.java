package project.msd.teenviolence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.Display;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by surindersokhal on 1/30/16.
 */
public class PlayGame extends Activity implements GestureDetector.OnGestureListener,Animation.AnimationListener {

    private Animation animZoomIn=null;
    private Animation animZoomOut=null;
    public Drawable image=null;
    private double speed=0;
    GestureDetector detector=null;
    Semaphore semaphore =new Semaphore(0,true);
    ImageView view =null;
    PlayGame that=null;
    int correctResponse=0;
    int errorResponse=0;
    double responseTime=0;
    double startTime=0;
    final static String POSITIVE_URL="http://10.0.2.2:8080/TeenViolenceServer/ImageFetcher";
    final static String NEGATIVE_URL="http://10.0.2.2:8080/TeenViolenceServer/ImageFetcher";
    double endTime=0;
    ParameterFile paramObject=null;
    HashMap<Bitmap,Boolean> imagesStream=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagesStream=new HashMap<Bitmap,Boolean>();


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        that=this;
        setContentView(R.layout.play_game);
        //Makes the activity to go to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view=(ImageView)findViewById(R.id.imageView);
       // image=view.getDrawable();
        detector = new GestureDetector(this,this);
        Intent intent = getIntent();

        paramObject=(ParameterFile)intent.getSerializableExtra("parameter");
        startPlayingTheGame();
        animZoomIn= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_zoom_in);
        animZoomOut= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_zoom_out);
    }



    public void fingerSwipedUp(){
        view.startAnimation(animZoomIn);

    }
    public void fingerSwipeDown(){
        view.startAnimation(animZoomOut);
    }
    public void startPlayingTheGame(){
        final FetchImages fetchImages=null;
        int count=0;
        final Random random=new Random();
        Thread thread=new Thread(){
            public void run(){
               boolean check=false;
                int counter=0;
               while(true) {
                   double start=System.nanoTime();
                   check=random.nextInt(3)==1?true:false;
                   new FetchImages(check).execute();
                   if((System.nanoTime()-start)/1000000000>5);{
                       semaphore.release();
                   }try{

                   semaphore.acquire();
                   Thread.sleep(3000);
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                   if(counter>10)
                       break;
                   counter++;




                }
            }
        };
        thread.start();

    }


    @Override
    public boolean onTouchEvent(MotionEvent me){
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
        Log.d("---onFling---",e1.toString()+e2.toString());

        endTime=System.nanoTime();
        responseTime+=(endTime-startTime);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d("---onLongPress---",e.toString());
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
        Log.d("---onSingleTapUp---",e.toString());
        return false;
    }


    //gets the current orientation of the phone.
    public int getScreenOrientation()
    {
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

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        try{
        Thread.sleep(100);}
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }


    private class FetchImages extends AsyncTask<Void,Void,Bitmap>{
        boolean isPositive=false;
        private ProgressDialog dialog;

        public FetchImages(boolean check){
            isPositive=check;
            //dialog=new ProgressDialog(that);

        }


        @Override
        protected Bitmap doInBackground(Void... urls){
            //get the image
            String urlToFetchImage=null;
            if(isPositive)
                urlToFetchImage=POSITIVE_URL+"?param1=positive";
            else
                urlToFetchImage=NEGATIVE_URL+"?param1=negative";
            InputStream stream=null;
            try{

                stream=Login_Activity.buildConnection(urlToFetchImage);
            //stream=new java.net.URL("http://www.funnydam.com/uploads/hello_sunshine_6894646119.jpg").openStream();
             //stream=new BufferedInputStream(new FileInputStream(new File()));


            }/*catch(MalformedURLException malformedException){

                malformedException.printStackTrace();
            }*/catch(IOException ioException){
                ioException.printStackTrace();
            }
           // InputStream s=that.getResources().openRawResource(R.drawable.background);
           // = Bitmap.

//            buildAlertDialog();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap image= BitmapFactory.decodeStream(stream,null,options);
            return image;
        }

        protected void onPostExecute(Bitmap image)
        {
            imagesStream.put(image,isPositive);
            view.setImageBitmap(image);

            startTime=System.nanoTime();
           // dialog.dismiss();
            semaphore.release();;
        }



    }

    public void buildAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("User verification failed");

        // set dialog message
        alertDialogBuilder
                .setMessage("Invalid username or pasword")
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });



        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
