package project.msd.teenviolence;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

public class Register extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {

    Button register, cancel;
    static final String ENCODING = "UTF-8";
    ProgressDialog progressDialog = null;
    Object[] datatype;
    int temp[]=new int[5];
    static final String URL = "http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/registration/Register";
    Spinner age, gender, ethnicity, mobile_exp, education;
    EditText username, password, psycoMeds;
    CheckBox disabiltiy, color;
    boolean isActivityStarted=false;


    public void onBackPressed() {

        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        register = (Button) findViewById(R.id.register);
        cancel = (Button) findViewById(R.id.Cancel);
        age = (Spinner) findViewById(R.id.age);
        gender = (Spinner) findViewById(R.id.gender);

        ArrayList<String> list = new ArrayList<>();
        list.add("select your age");

        for (int i = 0; i < 53; i++) {
            list.add((i + 18) + "");
        }
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_design, list);
        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this, R.array.gender_array, R.layout.spinner_design);
        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this, R.array.ethnicity_array, R.layout.spinner_design);
        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this, R.array.mobile_experience, R.layout.spinner_design);
        ArrayAdapter adapter4 = ArrayAdapter.createFromResource(this, R.array.education, R.layout.spinner_design);

        ethnicity = ((Spinner) findViewById(R.id.ethnicity));
        mobile_exp = ((Spinner) findViewById(R.id.mobile_exp));
        education = ((Spinner) findViewById(R.id.education));
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        color = (CheckBox) findViewById(R.id.colorblindness);
        disabiltiy = (CheckBox) findViewById(R.id.disability);
        psycoMeds = (EditText) findViewById(R.id.psycoMed);


        age.setAdapter(adapter);
        gender.setAdapter(adapter1);
        ethnicity.setAdapter(adapter2);
        mobile_exp.setAdapter(adapter3);
        education.setAdapter(adapter4);

        age.setOnItemSelectedListener(this);
        gender.setOnItemSelectedListener(this);
        ethnicity.setOnItemSelectedListener(this);
        mobile_exp.setOnItemSelectedListener(this);
        education.setOnItemSelectedListener(this);



        register.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
        System.out.println("Done "+position);
        boolean check=false;
        if(parentView.getId()==R.id.age){
            if(position==0 && temp[0]>0)
                check=true;
            else
                temp[0]=1;
        }
        if(parentView.getId()==R.id.gender){
            if(position==0 && temp[1]>0)
                check=true;
            else
                temp[1]=1;
        }
        if(parentView.getId()==R.id.ethnicity){
            if(position==0 && temp[2]>0)
                check=true;
            else
                temp[2]=1;
        }
        if(parentView.getId()==R.id.mobile_exp){
            if(position==0 && temp[3]>0)
                check=true;
            else
                temp[3]=1;
        }
        if(parentView.getId()==R.id.education){
            if(position==0 && temp[4]>0)
                check=true;
            else
                temp[4]=1;
        }

        System.out.println("Done "+check);
        if(check){
            buildAlertDialog("Wrong selection","You cannot select the first element.Please make the valid selection.");
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
        isActivityStarted=false;
    }


    public void buildAlertDialog(String title,String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register) {

            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Verifying the user");
            progressDialog.show();
            new FetchAggrement().execute();

        } else {
            createNextActivity(Login_Activity.class,"");
        }

    }

    public void buildAlertDialog(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("User Registration failed");

        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
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

    class FetchAggrement extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... parms) {
            InputStream stream = null;
            String agreement = null;

            try {
                stream = BuildConnections.buildConnection(URL + "?queryType=getAgreement");
                agreement = IOUtils.toString(stream,"UTF-8");

                return agreement;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String aggrement) {
            if (aggrement == null) {
                ProgressDialog dialog = new ProgressDialog(Register.this);
                dialog.setMessage("Unable to fetch agreement. Please try again");
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            } else {
                buildAgreement(aggrement);
            }

        }
    }

    public void buildAgreement(String aggrement) {
        final Semaphore semaphore = new Semaphore(0, true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Aggrement");
        boolean check = false;
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
                                        String user="Welcome: " + username.getText().toString();
                                        createNextActivity(Login_Activity.class,user);
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


    public static String encodeString(String rawString){
        byte array[]=rawString.getBytes();
        String encodedString=Base64.encodeToString(array,Base64.URL_SAFE|Base64.NO_WRAP);
        return encodedString;
    }

    public boolean performRegistration(Semaphore sema) {
        try {
        String url = URL + "?queryType=register&param=" + encodeString(username.getText().toString()) + "&param=" +
                    encodeString(password.getText().toString()) + "&param=" + (age.getSelectedItem().toString()) + "&param=" +
                    encodeString(ethnicity.getSelectedItem().toString()) + "&param=" + encodeString(gender.getSelectedItem().toString()) +
                    "&param=" + encodeString(disabiltiy.isChecked()+"") + "&param=" + encodeString(mobile_exp.getSelectedItem().toString()) +
                    "&param=" + encodeString(psycoMeds.getText().toString()) + "&param=" + encodeString(color.isChecked()+"")+ "&param=" + encodeString(education.getSelectedItem().toString());

            System.out.println("Done "+url);


            InputStream stream = BuildConnections.buildConnection(url);
            final JSONObject object = BuildConnections.getJSOnObject(stream);

            String status = object.getString("status");
            if (status.equalsIgnoreCase("1")) {
                progressDialog.dismiss();
                sema.release();
                return true;
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        progressDialog.dismiss();
                        buildAlertDialog(object.getString("message")+"\nPlease try again.");
                        ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            sema.release();
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            progressDialog.dismiss();
            return false;
        }

    }



    public void createNextActivity(Class clas,String sendData) {
        Intent intent = new Intent(Register.this, clas);
        intent.putExtra("speed", 100);
        intent.putExtra("text", sendData);
        Register.this.startActivity(intent);
    }
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Done on Destroy");


    }
}
