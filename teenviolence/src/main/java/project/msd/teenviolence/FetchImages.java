package project.msd.teenviolence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Created by surindersokhal on 4/4/16.
 */
public class FetchImages implements Runnable {

    static RequestQueue requestQueue = null;
    static Semaphore semaphore = new Semaphore(0, true);
    final static String POSITIVE_URL = "http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/ImageFetcher";
    final static String NEGATIVE_URL = "http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/ImageFetcher";
    final static Random random = new Random();


    public void run() {
        boolean check;
        {
            double start = System.nanoTime();
            check = random.nextInt(2) == 1 ? true : false;
            fetchImage(check);

        }
    }


    protected void fetchImage(boolean isPositive) {
        String color = "";
        //get the image
        String urlToFetchImage = null;
        if (isPositive) {
            urlToFetchImage = POSITIVE_URL + "?param1=positive";
            color = ParameterFile.positiveColor;
        } else {
            urlToFetchImage = NEGATIVE_URL + "?param1=negative";
            color = ParameterFile.negativeColor;
        }
        InputStream stream = null;
        try {

            stream = BuildConnections.buildConnection(urlToFetchImage);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        synchronized (PlayGame.testSubjectResults) {
        if(stream!=null && PlayGame.testSubjectResults.size()<=ParameterFile.totalGames ) {
            Bitmap image = BitmapFactory.decodeStream(stream, null, options);
            TestSubjectResults temp = new TestSubjectResults();
            temp.backgroundColor = color;
            temp.image = image;
            temp.isPositive = isPositive;
            temp.time = ParameterFile.time;
            PlayGame.testSubjectResults.add(temp);
            }
        }

    }


}
