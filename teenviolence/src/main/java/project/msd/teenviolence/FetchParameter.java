package project.msd.teenviolence;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by surindersokhal on 4/7/16.
 */
public class FetchParameter extends AsyncTask<Void,Void,JSONObject> {

    final String URL="http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/parameter/InitialParameter";
    @Override
    protected JSONObject doInBackground(Void... param){
        try{
            InputStream stream=BuildConnections.buildConnection(URL+"?userID="+ParameterFile.userID);
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


            ParameterFile.sessionID = Integer.parseInt(object.getString("sessionID"));
            ParameterFile.positiveColor = object.getString("positiveColor");
            ParameterFile.negativeColor = object.getString("negativeColor");
            ParameterFile.totalGames = Integer.parseInt(object.getString("totalGames"));
            ParameterFile.time = (object.getInt("timeInterval"));

            new DownloadVideo();
        }catch (Exception e){
            e.printStackTrace();

            Login_Activity.buildAlertDialog("Error fetching Parameter", e.getMessage() + "\nPlease retry.");
        }
    }

    class DownloadVideo {

        DownloadVideo(){
            doInBackground();
        }
        protected Void doInBackground(Void... param) {

            fetchImagesExecutorService();
            return null;
        }
    }

    public static void fetchImagesExecutorService() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        int counter = PlayGame.testSubjectResults.size();
        System.out.println("Size "+PlayGame.testSubjectResults.size());
        int size=0;
        while (counter < ParameterFile.totalGames) {
            if (size < ParameterFile.totalGames) {
                FetchImages fetchImages = new FetchImages();
                executor.execute(fetchImages);
                size++;
            }
            counter++;
        }

        executor.shutdown();

    }
}
