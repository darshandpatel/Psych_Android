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

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.concurrent.Semaphore;

public class Register extends AppCompatActivity implements View.OnClickListener {

    Button register,cancel;
    static final String ENCODING="UTF-8";
    ProgressDialog progressDialog=null;
    static final String URL="http://10.0.2.2:8080/TeenViolenceServer/AuthenticatingUser?queryType=register";
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
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("Verifying the user");
            progressDialog.show();

            new FetchAggrement().execute("");

        }else{
            createNextActivity(Login_Activity.class);
        }

    }

    public void buildAlertDialog(String msg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("User Registration failed");

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
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


                                try {
                                    boolean success = performRegistration(semaphore);
                                    semaphore.acquire();
                                    if (success) {
                                        progressDialog.dismiss();
                                        BuildInstructions bi = new BuildInstructions(Register.this, semaphore);
                                        semaphore.acquire();
                                        createNextActivity(Questions.class);
                                    }
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



    public boolean performRegistration(Semaphore sema){
        try{
        InputStream stream=Login_Activity.buildConnection(URL);
        String result= IOUtils.toString(stream, ENCODING);
            final JSONObject object=new JSONObject(result);
            String status=object.getString("status");
            if(status.equalsIgnoreCase("1")){
                progressDialog.dismiss();
                sema.release();
                return true;
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try{
                        progressDialog.dismiss();
                    buildAlertDialog(object.getString("message"));;}
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            cleanAllEditTexts();
            sema.release();
            return false;

        }catch (Exception e){
            e.printStackTrace();
            progressDialog.dismiss();
            return false;
        }

    }

    public void cleanAllEditTexts(){

    }

    public void createNextActivity(Class clas){
        Intent intent=new Intent(Register.this,clas);
        intent.putExtra("speed",100);
        Register.this.startActivity(intent);
    }
}
