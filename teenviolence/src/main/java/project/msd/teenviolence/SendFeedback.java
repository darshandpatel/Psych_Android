package project.msd.teenviolence;

import android.os.AsyncTask;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Test;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

    public static String convertHMToString(HashMap<String, String> hashMap){

        Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();
        StringBuilder str = new StringBuilder();
        str.append("{");
        while(iterator.hasNext()){
            Map.Entry<String, String> pair = iterator.next();

            str.append("\""+pair.getKey()+"\":");
            str.append("\""+pair.getValue()+"\"");
            str.append(",");
        }
        int length = str.length();
        str.deleteCharAt(length-1);
        str.append("}");
        return str.toString();
    }

    protected Void doInBackground(Void... param) {

        ArrayList<String> imageResponses = new ArrayList<String>();

        for(TestSubjectResults result : arrayList) {

            HashMap<String, String> response = new HashMap<String, String>();
            getCorrect_IncorrectResponses(result);
            //DecimalFormat df = new DecimalFormat("0.00");
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
            response.put(Constant.PARTICIPANTID, Long.toString(ParameterFile.participantId));
            response.put(Constant.SESSION_ID,Long.toString(ParameterFile.sessionID));
            response.put(Constant.IMAGE_CATEGORY_ID,Long.toString(result.imageCategoryId));
            response.put(Constant.IMAGE_TYPE_ID,Long.toString(result.imageTypeId));
            response.put(Constant.IMAGE_ID,Long.toString(result.imageId));
            response.put(Constant.CORRECTNESS,Boolean.toString(result.correctness));
            response.put(Constant.TIME,Long.toString(result.time));
            response.put(Constant.BACKGROUND_COLOR,result.backgroundColor);
            response.put(Constant.IS_ATTEMPTED,Boolean.toString(result.isAttempted));

            imageResponses.add(convertHMToString(response));
        }
        try {
            //BuildConnections.buildConnection(URL + data);
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put(Constant.RESPONSES, imageResponses);
            hashMap.put(Constant.PARTICIPANTID, ParameterFile.participantId);
            BuildConnections.buildPostConnection(Constant.SERVER_ADDRESS+"imageData/ImageDataServlet", hashMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
