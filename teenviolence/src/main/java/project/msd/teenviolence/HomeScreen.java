package project.msd.teenviolence;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.annotations.SerializedName;

import java.util.concurrent.Semaphore;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener{

    TextView textView;
    Button demo,instruction,game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        textView=(TextView)findViewById(R.id.homeID);
        Intent intent = getIntent();
        String text=intent.getStringExtra("text");
        textView.setText(text);
        demo=(Button)findViewById(R.id.demo);
        instruction=(Button)findViewById(R.id.instructions);
        game=(Button)findViewById(R.id.playGame);
        demo.setOnClickListener(this);
        instruction.setOnClickListener(this);
        game.setOnClickListener(this);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.instructions) {

            Semaphore semaphore=new Semaphore(0,true);
            BuildInstructions build=new BuildInstructions(this,semaphore);

        }
        if (view.getId() == R.id.demo) {
            Intent intent=new Intent(HomeScreen.this,PlayDemo.class);
            intent.putExtra("skipEnabled",false);
            HomeScreen.this.startActivity(intent);
        }
        if (view.getId() == R.id.playGame) {
            Intent intent=new Intent(HomeScreen.this,Questions.class);
            intent.putExtra("demoNeeded",false);
            HomeScreen.this.startActivity(intent);
        }
    }

    public void onBackPressed() {

        return;
    }

}
