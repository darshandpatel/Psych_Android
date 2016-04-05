package project.msd.teenviolence;

import android.os.AsyncTask;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by surindersokhal on 3/24/16.
 */
public class RequestToBuildParameterFile extends AsyncTask<Void,Void,Void> {

    final static String URL="";
    final static String ENCODING="UTF-8";
    @Override
    protected Void doInBackground(Void... params ) {

        try{
        InputStream stream=BuildConnections.buildConnection(URL);

            String json=new Gson().toJson(IOUtils.toString(stream, ENCODING));
            Login_Activity.paramObject=new Gson().fromJson(json, ParameterFile.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }



}
