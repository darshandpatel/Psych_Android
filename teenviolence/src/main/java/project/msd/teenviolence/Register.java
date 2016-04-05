package project.msd.teenviolence;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Register extends AppCompatActivity implements View.OnClickListener {

    Button register,cancel;
    static final String ENCODING="UTF-8";
    ProgressDialog progressDialog=null;
    static final String URL="http://1742aefa.ngrok.io/TeenViolenceServer/AuthenticatingUser?queryType=register";
    Spinner age,gender;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void onBackPressed() {

        return;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        register=(Button)findViewById(R.id.register);
        cancel=(Button)findViewById(R.id.Cancel);
        age =(Spinner)findViewById(R.id.age);
        gender=(Spinner)findViewById(R.id.gender);
        ArrayList<String> list=new ArrayList<>();
        list.add("select your age");

        for(int i=0;i<53;i++){
            list.add((i+18)+"");
        }
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.spinner_design,list);
        ArrayAdapter adapter1=ArrayAdapter.createFromResource(this,R.array.gender_array,R.layout.spinner_design);
        ArrayAdapter adapter2=ArrayAdapter.createFromResource(this,R.array.ethnicity_array,R.layout.spinner_design);
        ArrayAdapter adapter3=ArrayAdapter.createFromResource(this,R.array.mobile_experience,R.layout.spinner_design);
        ArrayAdapter adapter4=ArrayAdapter.createFromResource(this,R.array.education,R.layout.spinner_design);
        gender.setAdapter(adapter1);
        ((Spinner)findViewById(R.id.ethnicity)).setAdapter(adapter2);
        ((Spinner)findViewById(R.id.mobile_exp)).setAdapter(adapter3);
        ((Spinner)findViewById(R.id.education)).setAdapter(adapter4);

        age.setAdapter(adapter);
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
                                    boolean success = true;//performRegistration(semaphore);
                                    //semaphore.acquire();
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
        InputStream stream=BuildConnections.buildConnection(URL);
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
