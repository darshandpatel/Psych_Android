package project.msd.teenviolence;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by surindersokhal on 4/7/16.
 */
public class FetchParameter extends AsyncTask<Void,Void,JSONObject> {

    final String URL="";
    @Override
    protected JSONObject doInBackground(Void... param){
        try{
            InputStream stream=BuildConnections.buildConnection(URL);
            JSONObject object=BuildConnections.getJSOnObject(stream);
            return object;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject object){
        if(object==null){
            Login_Activity.buildAlertDialog("Error fetching Parameter", "Unable to fetch the parameters.\nPlease retry.");
            return;
        }
        try {
            ParameterFile.userID = Integer.parseInt(object.getString("userID"));
            ParameterFile.sessionID = Integer.parseInt(object.getString("sessionID"));
            ParameterFile.positiveColor = object.getString("positiveColor");
            ParameterFile.negativeColor = object.getString("negativeColor");
            ParameterFile.totalGames = Integer.parseInt(object.getString("totalGames"));
            ParameterFile.time = Integer.parseInt(object.getString("time"));
        }catch (Exception e){
            Login_Activity.buildAlertDialog("Error fetching Parameter", e.getMessage()+"\nPlease retry.");
        }
    }
}
