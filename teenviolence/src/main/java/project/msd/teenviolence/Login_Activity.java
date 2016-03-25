package project.msd.teenviolence;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by surindersokhal on 1/30/16.
 */
public class Login_Activity extends Activity implements View.OnClickListener{

    ProgressDialog dialog=null;
    Button loginButton=null,signUpButton=null;
    EditText userName=null;
    EditText passowrd=null;
    final String URL="http://192.168.173.1:8080/TeenViolence/AuthenticatingUser";
    final String ENCODING="UTF-8";
    static ParameterFile paramObject=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.login_activity);
        loginButton=(Button)findViewById(R.id.loginButton);
        signUpButton=(Button)findViewById(R.id.SignupButton);

        userName=(EditText)findViewById(R.id.eidtTextbox);
        passowrd=(EditText)findViewById(R.id.passowdTextbox);

        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

       /* new RequestToBuildParameterFile().execute();*/
        //Makes the activity to go to full screen.
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          //      WindowManager.LayoutParams.FLAG_FULLSCREEN);

}

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.loginButton){
            dialog=new ProgressDialog(this);
            dialog.setMessage("Verifying the user");
            dialog.show();
            ArrayList<String> data=new ArrayList<>();
            data.add(userName.getText().toString());
            data.add(passowrd.getText().toString());
            new VerifyLogin().execute(data);

        }else{
            Intent intent=new Intent(Login_Activity.this,Register.class);
            Login_Activity.this.startActivity(intent);
        }

    }

    class VerifyLogin extends AsyncTask<ArrayList<String>,Void, Boolean>{

        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {
            String userName= params[0].get(0);
            String passowrd=params[0].get(1);
            System.out.println("Surcinder in asyn");
            return isCorrectLogin(userName,passowrd);
        }

        protected void onPostExecute(Boolean check){
            if(check){
                Toast.makeText(Login_Activity.this,"Login Successful",Toast.LENGTH_LONG);
                dialog.dismiss();
                playGame();
            }else{
                dialog.dismiss();
                buildAlertDialog();

            }

        }
    }



    public void buildAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title
        alertDialogBuilder.setTitle("User verification failed");

        // set dialog message
        alertDialogBuilder
                .setMessage("Invalid username or pasword")
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

    public boolean isCorrectLogin(String userName,String passowrd){
        try{
            InputStream stream=buildConnection(URL);
            String json=(IOUtils.toString(stream, ENCODING));
            /*JSONObject object=new JSONObject(json);
            return object.getInt("status")==0?false:true;*/
            System.out.println("Strind "+ json);
            return true;
        }catch(IOException e){
            e.printStackTrace();
        }

        return true;
    }
    public void playGame(){
        Intent intent=new Intent(Login_Activity.this,PlayGame.class);
        intent.putExtra("parameters",paramObject);
        Login_Activity.this.startActivity(intent);

    }

    public static InputStream buildConnection(String URL) throws IOException{
        HttpClient httpclient = new DefaultHttpClient();
        System.out.println("Surinder "+httpclient);
        HttpGet httpget = new HttpGet(URL);
        System.out.println("Surinder "+httpget);
        HttpResponse response= (httpclient.execute(httpget));
        InputStream stream=response.getEntity().getContent();
        System.out.println(stream);
        return stream;

    }
}
