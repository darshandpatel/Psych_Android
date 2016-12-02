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
    //final static String POSITIVE_URL = "http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/ImageFetcher";
    final static String POSITIVE_URL = "http://10.0.2.2:8080/TeenViolenceServer2/ImageFetcher";
    //final static String NEGATIVE_URL = "http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/ImageFetcher";
    final static String NEGATIVE_URL = "http://10.0.2.2:8080/TeenViolenceServer2/ImageFetcher";
    final static Random random = new Random();

    Long imageCategoryId;
    Long imageTypeId;
    String imageType;
    Long displayTime;
    String imagePath;
    String imageURL;
    Long imageId;

    public FetchImages(Long imageCategoryId, Long imageTypeId, String imageType, Long displayTime,
                       String imagePath, String imageURL, Long imageId){
        this.imageCategoryId = imageCategoryId;
        this.imageTypeId = imageTypeId;
        this.imageType = imageType;
        this.displayTime = displayTime;
        this.imagePath = imagePath;
        this.imageURL = imageURL;
        this.imageId = imageId;
    }
    public void run() {
        fetchImage();
        //boolean check;
        //{
            //double start = System.nanoTime();
            //check = random.nextInt(2) == 1 ? true : false;
            //fetchImage(check);


        //}
    }


    protected void fetchImage() {
        String color = "";
        InputStream stream = null;
        try {
            //get the image
            stream = BuildConnections.buildConnection(this.imageURL);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        synchronized (PlayGame.testSubjectResults) {
            //if(stream!=null && PlayGame.testSubjectResults.size()<=ParameterFile.totalGames ) {
            if(stream!=null){
                Bitmap image = BitmapFactory.decodeStream(stream, null, options);
                TestSubjectResults temp = new TestSubjectResults();
                if(this.imageType.equals(Constant.POSITIVE)){
                    temp.backgroundColor = ParameterFile.positiveColor;
                    temp.isPositive = true;
                }else{
                    temp.backgroundColor = ParameterFile.negativeColor;
                    temp.isPositive = false;
                }
                temp.image = image;
                temp.time = this.displayTime;
                temp.imageCategoryId= this.imageCategoryId;
                temp.imageId = this.imageId;
                temp.imageTypeId = this.imageTypeId;
                PlayGame.testSubjectResults.add(temp);
            }
        }

    }


}
