package project.msd.teenviolence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

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
    static File outputFile = null;
    static Login_Activity activity = null;
    //static String ADDRESS="http://ec2-52-38-37-183.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/";
    static String ADDRESS = "http://468d06e3.ngrok.io/TeenViolenceServer/";

    static boolean isDownloadComplete = false;

    final String URL = ADDRESS + "AuthenticatingUser?queryType=login";
    final static String DEMOURL = ADDRESS + "ParameterServlet";
    final String ENCODING = "UTF-8";
    static ParameterFile paramObject = null;
    static boolean isDownloadStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_activity);
        loginButton = (Button) findViewById(R.id.loginButton);
        signUpButton = (Button) findViewById(R.id.SignupButton);

        userName = (EditText) findViewById(R.id.eidtTextbox);
        passowrd = (EditText) findViewById(R.id.password);

        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        new DownloadVideo().execute();
    }


    public void onBackPressed() {

        return;
    }

    public static void fetchImagesExecutorService() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        int counter = PlayGame.testSubjectResults.size();
        while (counter <= ParameterFile.totalGames) {
            if (PlayGame.testSubjectResults.size() <= ParameterFile.totalGames) {
                FetchImages fetchImages = new FetchImages();
                executor.execute(fetchImages);
                System.out.println("Images executed");
            }
            counter++;

        }
        executor.shutdown();

    }


    class DownloadVideo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... param) {
            downloadDemoVideo();
            fetchImagesExecutorService();
            return null;
        }
    }

    public static void downloadDemoVideo() {


        try {
            isDownloadStarted = true;
            InputStream stream = BuildConnections.buildConnection(DEMOURL + "?queryType=video");
            File outputDir = activity.getCacheDir(); // context being the Activity pointer
            outputFile = new File(outputDir + "/demo.mp4");
            if (outputFile.exists()) {
                return;
            }
            outputFile.createNewFile();
            // outputFile = File.createTempFile("demo", ".mp4", outputDir);
            outputFile.setReadable(true, false);
            OutputStream out = new FileOutputStream(outputFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = stream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            stream.close();
            isDownloadComplete = true;
            System.out.println("Done " + outputFile.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Pattern p = Pattern.compile("^[a-z0-9_-]{5,15}$");
        Matcher matcher = p.matcher(username);
        return matcher.matches();
    }

    class VerifyLogin extends AsyncTask<ArrayList<String>, Void, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {
            String userName = params[0].get(0);
            String passowrd = params[0].get(1);
            return true;
            /*
            if(isValidUsername(userName)){
                return isCorrectLogin(userName,passowrd);
            }
            return false;*/
        }

        protected void onPostExecute(Boolean check) {
            if (check) {
                dialog.dismiss();
                welcomeActivity();
            } else {
                dialog.dismiss();
                buildAlertDialog();

            }

        }
    }


    public void buildAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
         alertDialogBuilder.setTitle("User verification failed");
        alertDialogBuilder
                .setMessage("Invalid username or pasword")
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

    public boolean isCorrectLogin(String userName, String password) {
        try {
            InputStream stream = BuildConnections.buildConnection(URL + "&username=" + userName + "&passsword=" + password);
            String json = IOUtils.toString(stream, ENCODING);
            System.out.println("String " + String.valueOf(json) + " " + json.getClass());
            JSONObject object = new JSONObject(json);
            if (object.getString("success").equalsIgnoreCase("1")) {
                ParameterFile.userID = Integer.parseInt(object.getString("userID"));
                ParameterFile.sessionID = Integer.parseInt(object.getString("sessionID"));
                ParameterFile.positiveColor = object.getString("positiveColor");
                ParameterFile.negativeColor = object.getString("negativeColor");
                ParameterFile.totalGames = Integer.parseInt(object.getString("totalGames"));
                ParameterFile.time = Integer.parseInt(object.getString("time"));
                return true;
            } else
                return false;


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void welcomeActivity() {
        Intent intent = new Intent(Login_Activity.this, HomeScreen.class);
        intent.putExtra("parameters", paramObject);
        intent.putExtra("text", "Welcome: " + userName.getText().toString());
        Login_Activity.this.startActivity(intent);
    }


}
