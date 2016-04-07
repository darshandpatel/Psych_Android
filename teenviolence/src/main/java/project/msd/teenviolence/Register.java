package project.msd.teenviolence;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Register extends AppCompatActivity implements View.OnClickListener {

    Button register, cancel;
    static final String ENCODING = "UTF-8";
    ProgressDialog progressDialog = null;
    Object[] datatype;
    static final String URL = "http://1742aefa.ngrok.io/TeenViolenceServer/AuthenticatingUser";
    Spinner age, gender, ethnicity, mobile_exp, education;
    EditText username, password, psycoMeds;
    CheckBox disabiltiy, color;



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

        register.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Verifying the user");
            progressDialog.show();
            new FetchAggrement().execute();

        } else {
            createNextActivity(Login_Activity.class);
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
            /*
            try {
                stream = BuildConnections.buildConnection(URL + "?queryType=getAgreement");
                JSONObject object = BuildConnections.getJSOnObject(stream);
                agreement = object.getString("agreement");
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            return "";
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
                                    boolean success = true;
                                    //performRegistration(semaphore);
                                    //semaphore.acquire();
                                    if (success) {
                                        progressDialog.dismiss();
                                        createNextActivity(HomeScreen.class);
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


    public boolean performRegistration(Semaphore sema) {
        try {
            String url = URL + "?queryType=register&param=" + username.getText().toString() + "&param=" +
                    password.getText().toString() + "&param=" + age.getSelectedItem().toString() + "&param=" +
                    ethnicity.getSelectedItem().toString() + "&param=" + gender.getSelectedItem().toString() +
                    "&param=" + disabiltiy.isChecked() + "&param=" + mobile_exp.getSelectedItem().toString() +
                    "&param=" + psycoMeds.getText().toString() + "&param=" + color.isChecked();


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



    public void createNextActivity(Class clas) {
        Intent intent = new Intent(Register.this, clas);
        intent.putExtra("speed", 100);
        Register.this.startActivity(intent);
    }
}
