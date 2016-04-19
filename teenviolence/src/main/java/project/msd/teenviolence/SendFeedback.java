package project.msd.teenviolence;

import android.os.AsyncTask;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Test;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by surindersokhal on 4/7/16.
 */

public class SendFeedback extends AsyncTask<Void, Void, Void> {

    static String URL = "http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/imageData/ImageDataServlet?";
    Semaphore semaphore=null;
    ArrayList<TestSubjectResults> arrayList=null;
    public SendFeedback(Semaphore sem){
        arrayList=new ArrayList<TestSubjectResults>();
        arrayList.addAll(PlayGame.testSubjectResults);
        semaphore=sem;

    }
    public void getCorrect_IncorrectResponses(TestSubjectResults result){
        if(result.isAttempted){
            if(result.responseAccurate)
                PlayGame.totalCorrectResponse++;
           else
                PlayGame.totalwrongResponse++;
        }
    }
    protected Void doInBackground(Void... param) {

        for(TestSubjectResults result:arrayList) {


            getCorrect_IncorrectResponses(result);
            DecimalFormat df = new DecimalFormat("0.00");
            PlayGame.totalTimeTaken += ((result.time)/Math.pow(10,6));
            String isAttempted = result.isAttempted + "";
            String time = df.format((result.time)/Math.pow(10,6)) + " secs";
            String isPositive = result.isPositive + "";
            String bgColor = result.backgroundColor;
            String responseAccurate = result.responseAccurate + "";
            String userID = ParameterFile.userID + "";
            String data = "param=" + Register.encodeString(isAttempted) + "&param=" + Register.encodeString(time) + "&param=" +
                    Register.encodeString(isPositive) + "&param=" +
                    Register.encodeString(bgColor) + "&param=" +
                    Register.encodeString(responseAccurate) + "&param=" + userID;
            try {
                BuildConnections.buildConnection(URL + data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
