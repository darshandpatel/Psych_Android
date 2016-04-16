package project.msd.teenviolence;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DemoColorActivity extends AppCompatActivity {

    TextView view;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_color);
        view=(TextView)findViewById(R.id.demoColor);
        layout=(LinearLayout)findViewById(R.id.layoutColor);

        paintDemoColor();
    }

    public void paintDemoColor(){
        try{
            layout.setBackgroundColor((Color.parseColor(ParameterFile.positiveColor)));
            view.setText("Please swipe down if you see this color");
            Thread.sleep(2000);
            layout.setBackgroundColor((Color.parseColor(ParameterFile.negativeColor)));
            view.setText("Please swipe up if you see this color");
            Thread.sleep(2000);
            createNewActivity();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void createNewActivity(){
        Intent intent=new Intent(DemoColorActivity.this,PlayGame.class);
        intent.putExtra("speed", 100);
        DemoColorActivity.this.startActivity(intent);
    }
}
