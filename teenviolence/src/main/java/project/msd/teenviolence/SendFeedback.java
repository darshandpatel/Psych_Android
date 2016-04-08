package project.msd.teenviolence;

import android.os.AsyncTask;

/**
 * Created by surindersokhal on 4/7/16.
 */

public class SendFeedback extends AsyncTask<String, Void, Void> {

    static String URL = "imageData/ImageDataServlet?";

    protected Void doInBackground(String... param) {

        String isAttempted = param[0];
        String time = param[1];
        String isPositive = param[2];
        String bgColor = param[3];
        String responseAccurate = param[4];
        String userID = ParameterFile.userID + "";
        String data = "param=" + isAttempted + "&param=" + time + "&param=" + isPositive + "&param=" +
                bgColor + "&param=" + responseAccurate + "&param=" + userID;
        try {
            BuildConnections.buildConnection(URL + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
