package project.msd.teenviolence;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DemoColorActivity extends Activity implements Runnable,GestureDetector.OnGestureListener, Animation.AnimationListener {

    TextView view;
    LinearLayout layout;
    Thread thread=null;
    private Animation animZoomIn = null;
    private Animation animZoomOut = null, animNormal = null;
    GestureDetector detector=null;
    boolean swipeDown=false,animStarted=false,swipeDone=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_demo_color);
        view=(TextView)findViewById(R.id.demoColor);
        layout=(LinearLayout)findViewById(R.id.layoutColor);
        detector = new GestureDetector(this, this);
        layout.setBackgroundColor((Color.parseColor(ParameterFile.positiveColor)));
        view.setText("Please swipe down if you see this color");
        loadAnimaions();
        thread=new Thread(this);
        thread.start();

    }

    public void run(){
        loadFirstAnimation();
    }

    public void loadFirstAnimation(){animStarted=true;
       view.startAnimation(animZoomIn);

    }
    public void loadAnimaions(){
        animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_hint_in);
        animZoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_hint_out);
        animZoomIn.setAnimationListener(this);
        animZoomOut.setAnimationListener(this);
    }


    @Override
    public void onAnimationStart(Animation animation) {
        Log.i("Animation", "start");
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(!swipeDone) {
            if (animStarted) {
                view.startAnimation(animZoomOut);
                animStarted = false;
            } else {
                view.startAnimation(animZoomIn);
                animStarted=true;
            }
        }else{
            createNewActivity();
        }

        }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
    @Override
    public void onLongPress(MotionEvent e) {

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

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        if (swipeDown && e1.getY() - e2.getY() > 10) {
            swipeDone=true;
        }

        if(!swipeDown && e1.getY() - e2.getY() <- 10 ){
            swipeDown=true;
            layout.setBackgroundColor((Color.parseColor(ParameterFile.negativeColor)));
            view.setText("Please swipe up if you see this color");

        }



    return false;

    }
    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    protected void onDestroy(){
        super.onDestroy();
    }

    public void onBackPressed() {

        return;
    }
    public void createNewActivity(){
        Intent intent=new Intent(DemoColorActivity.this,PlayGame.class);
        intent.putExtra("speed", 100);
        DemoColorActivity.this.startActivity(intent);
    }
}
