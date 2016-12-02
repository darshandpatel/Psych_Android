package project.msd.teenviolence;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by surindersokhal on 4/7/16.
 */
public class FetchImageParameter extends AsyncTask<Void,Void,JSONObject> {

    //final String URL="http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/parameter/InitialParameter";
    final String URL="http://10.0.2.2:8080/TeenViolenceServer2/parameter/InitialParameter";
    @Override
    protected JSONObject doInBackground(Void... param){
        try{
            PlayGame.testSubjectResults = new ArrayList<TestSubjectResults>();
            //InputStream stream=BuildConnections.buildConnection(URL+"?userID="+ParameterFile.userID);
            InputStream stream=BuildConnections.buildConnection(Constant.SERVER_ADDRESS+"imageData/ImageFetcher"+"?"+Constant.TG_ID+"="+ParameterFile.tgId);
            JSONObject object=BuildConnections.getJSOnObject(stream);
            return object;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject object){

        if(object==null){
            Login_Activity.buildAlertDialog("Error fetching Image Parameter", "Unable to fetch the image parameters.\nPlease retry.");
            return;
        }
        try {
            JSONArray images = object.getJSONArray(Constant.IMAGES);
            System.out.println("images : "+ images);
            //int length = images.length();
            //for(int i= 0; i<length; i++){
            //    JSONObject jsonObject = (JSONObject) images.get(i);
            //}
            //ParameterFile.sessionID = Integer.parseInt(object.getString("sessionID"));
            //ParameterFile.positiveColor = object.getString("positiveColor");
            //ParameterFile.negativeColor = object.getString("negativeColor");
            //ParameterFile.totalGames = Integer.parseInt(object.getString("totalGames"));
            //ParameterFile.time = (object.getInt("timeInterval"));
            new DownloadVideo(images);
        }catch (Exception e){
            e.printStackTrace();

            Login_Activity.buildAlertDialog("Error fetching Parameter", e.getMessage() + "\nPlease retry.");
        }
    }

    class DownloadVideo {

        DownloadVideo(JSONArray images){
            doInBackground(images);
        }
        protected Void doInBackground(JSONArray... images) {

            fetchImagesExecutorService(images[0]);
            return null;
        }
    }

    public static void fetchImagesExecutorService(JSONArray images) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        int counter = images.length();
        //int counter = PlayGame.testSubjectResults.size();
        //System.out.println("Size "+PlayGame.testSubjectResults.size());
        int index=0;
        try {
            while (index < counter) {

                JSONObject jsonObject = (JSONObject) images.get(index);

                //if (size < ParameterFile.totalGames) {
                Long imageCategoryId = jsonObject.getLong(Constant.IMAGE_CATEGORY_ID);
                Long imageTypeId = jsonObject.getLong(Constant.IMAGE_TYPE_ID);
                String imageType = jsonObject.getString(Constant.IMAGE_TYPE);
                Long displayTime = jsonObject.getLong(Constant.IMAGE_DISPLAY_DURATION);
                String imagePath = jsonObject.getString(Constant.IMAGE_PATH);
                Long imageId = jsonObject.getLong(Constant.IMAGE_ID);
                String imageURL = Constant.SERVER_ADDRESS+"/imageUpload?imagePath="+imagePath+"&"+
                        Constant.SOURCE+"="+Constant.ANDROID;
                FetchImages fetchImages = new FetchImages(imageCategoryId, imageTypeId, imageType,
                        displayTime, imagePath, imageURL, imageId);
                executor.execute(fetchImages);
                index++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        executor.shutdown();

    }
}
