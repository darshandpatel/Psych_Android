package project.msd.teenviolence;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.concurrent.Semaphore;

public class Register extends AppCompatActivity implements View.OnClickListener {

    Button register,cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register=(Button)findViewById(R.id.register);
        cancel=(Button)findViewById(R.id.Cancel);
        register.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.register){
            ProgressDialog dialog=new ProgressDialog(this);
            dialog.setMessage("Verifying the user");
            dialog.show();

            new FetchAggrement().execute("");

        }else{

        }

    }

    class FetchAggrement extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... parms){

            String address="";
            //fetch Aggrement;

            return "Please agree to all terms and conditions";
        }

        protected void onPostExecute(String aggrement){
            buildAgreement(aggrement);

        }
    }
    public void buildAgreement(String aggrement){
        final Semaphore semaphore=new Semaphore(0,true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
      //  Dialog d = alertDialogBuilder.setView(new View(this)).create();
       // WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //lp.copyFrom(d.getWindow().getAttributes());
        //lp.width =(int)(WindowManager.LayoutParams.MATCH_PARENT*0.9);
        //lp.height = (int)(WindowManager.LayoutParams.MATCH_PARENT*0.9);
        //d.getWindow().setAttributes(lp);
        // set title
        alertDialogBuilder.setTitle("Aggrement");
         boolean check=false;
        // set dialog message
        alertDialogBuilder
                .setMessage(aggrement)
                .setCancelable(false)
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                        new Thread() {
                            public void run() {
                                BuildInstructions bi = new BuildInstructions(Register.this, semaphore);
                                try {
                                    semaphore.acquire();
                                    createNextActivity();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();


                    }
                }).setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, close
                // current activity
                dialog.cancel();
                System.exit(0);

            }
        });



        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }



    public void createNextActivity(){
        Intent intent=new Intent(Register.this,Questions.class);
        intent.putExtra("speed",100);
        Register.this.startActivity(intent);
    }
}
