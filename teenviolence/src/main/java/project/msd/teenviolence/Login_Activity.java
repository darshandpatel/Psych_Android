package project.msd.teenviolence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;


import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
;

/**
 * Created by surindersokhal on 1/30/16.
 */
public class Login_Activity extends Activity implements View.OnClickListener {

    ProgressDialog dialog = null;
    Button loginButton = null, signUpButton = null;
    EditText userName = null;
    EditText passowrd = null;
    static Login_Activity activity = null;
    static String ADDRESS = "http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/";


    static boolean isDownloadComplete = false;

    final String URL = ADDRESS + "AuthenticatingUser?queryType=login";
    final static String DEMOURL = ADDRESS + "ParameterServlet";
    final String ENCODING = "UTF-8";
    static ParameterFile paramObject = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;

        setContentView(R.layout.login_activity);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.SignupButton);

        userName = (EditText) findViewById(R.id.eidtTextbox);
        passowrd = (EditText) findViewById(R.id.password);

        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);




    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        System.out.println("Done on Destroy");
    }
    public void onBackPressed() {

        return;
    }








    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginButton) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Verifying the user");
            dialog.show();
            ArrayList<String> data = new ArrayList<>();
            data.add(userName.getText().toString());
            data.add(passowrd.getText().toString());
            new VerifyLogin().execute(data);

        } else {
            Intent intent = new Intent(Login_Activity.this, Register.class);
            Login_Activity.this.startActivity(intent);
        }

    }

    public static boolean isValidUsername(String username) {
        Pattern p = Pattern.compile("^[a-z0-9_-]{1,15}$");
        Matcher matcher = p.matcher(username);
        return matcher.matches();
    }

    class VerifyLogin extends AsyncTask<ArrayList<String>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {
            String userName = params[0].get(0);
            String passowrd = params[0].get(1);
            if(isValidUsername(userName)){
                return isCorrectLogin(Register.encodeString(userName),Register.encodeString(passowrd));
            }
            return false;
        }

        protected void onPostExecute(Boolean check) {
            if (check) {
                dialog.dismiss();
                welcomeActivity();
            } else {
                dialog.dismiss();
                buildAlertDialog("Error validating User","Invalid username/password");

            }

        }
    }


    public static void buildAlertDialog(String title,String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
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

    public boolean isCorrectLogin(String username, String password) {
        try {

            System.out.println(username+" "+password);
            InputStream stream = BuildConnections.buildConnection(URL + "&username=" + username + "&password=" + password);
            String json = IOUtils.toString(stream, ENCODING);
            System.out.println("String " + String.valueOf(json) + " " + json.getClass());
            JSONObject object = new JSONObject(json);
            if (object.getString("success").equalsIgnoreCase("1")) {
                ParameterFile.userName=userName.getText().toString();
                ParameterFile.QuestionSession=0;
                ParameterFile.userID = Integer.parseInt(object.getString("userId"));
                new FetchParameter().execute();
                return true;
            } else
                return false;


        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }


    }

    public void welcomeActivity() {
        Intent intent = new Intent(Login_Activity.this, HomeScreen.class);
        intent.putExtra("parameters", paramObject);
        intent.putExtra("text", "Welcome: " + userName.getText().toString());
        Login_Activity.this.startActivity(intent);
    }


}
