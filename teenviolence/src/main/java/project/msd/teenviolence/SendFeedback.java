package project.msd.teenviolence;

import android.os.AsyncTask;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Test;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 * Created by surindersokhal on 4/7/16.
 */

public class SendFeedback extends AsyncTask<Void, Void, Void> {

    Semaphore semaphore=null;
    ArrayList<TestSubjectResults> arrayList=null;
    public SendFeedback(Semaphore sem){
        arrayList=new ArrayList<TestSubjectResults>();
        arrayList.addAll(PlayGame.testSubjectResults);
        semaphore=sem;

    }
    public void getCorrect_IncorrectResponses(TestSubjectResults result){
        if(result.isAttempted){
            if(result.correctness)
                PlayGame.totalCorrectResponse++;
           else
                PlayGame.totalwrongResponse++;
        }
    }
    protected Void doInBackground(Void... param) {

        ArrayList<HashMap<String, Object>> imageResponses = new ArrayList<HashMap<String, Object>>();

        for(TestSubjectResults result : arrayList) {

            HashMap<String, Object> response = new HashMap<String, Object>();
            getCorrect_IncorrectResponses(result);
            DecimalFormat df = new DecimalFormat("0.00");
            PlayGame.totalTimeTaken += ((result.time) / Math.pow(10, 6));
            //String isAttempted = result.isAttempted + "";
            //String time = df.format((result.time) / Math.pow(10, 6)) + " secs";
            //String isPositive = result.isPositive + "";
            //String bgColor = result.backgroundColor;
            //String correctness = result.correctness + "";
            //String userID = ParameterFile.userID + "";
            //String data = "param=" + Register.encodeString(isAttempted) + "&param=" + Register.encodeString(time) + "&param=" +
            //        Register.encodeString(isPositive) + "&param=" +
            //        Register.encodeString(bgColor) + "&param=" +
            //        Register.encodeString(correctness) + "&param=" + userID;

            response.put(Constant.PARTICIPANTID,ParameterFile.participantId);
            response.put(Constant.SESSION_ID,ParameterFile.sessionID);
            response.put(Constant.IMAGE_CATEGORY_ID,result.imageCategoryId);
            response.put(Constant.IMAGE_TYPE_ID,result.imageTypeId);
            response.put(Constant.IMAGE_ID,result.imageId);
            response.put(Constant.CORRECTNESS,result.correctness);
            response.put(Constant.TIME,result.time);
            response.put(Constant.BACKGROUND_COLOR,result.backgroundColor);
            response.put(Constant.IS_ATTEMPTED,result.isAttempted);
            imageResponses.add(response);
        }
        try {
            //BuildConnections.buildConnection(URL + data);
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put(Constant.RESPONSES, imageResponses);
            BuildConnections.buildPostConnection(Constant.SERVER_ADDRESS+"imageData/ImageDataServlet", hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
