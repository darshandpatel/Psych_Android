package project.msd.teenviolence;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by surindersokhal on 4/4/16.
 */
public class BuildConnections {


    public static InputStream buildConnection(String URL) throws IOException {
        try {

            if (URL.contains(" ")) {
                URL = URL.replaceAll(" ", "%20");
            }

            System.out.println("URL :" + URL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(URL);
            HttpResponse response = (httpclient.execute(httpget));
            InputStream stream = response.getEntity().getContent();
            System.out.println("strem "+stream);
            return stream;
        } catch (Exception e) {
            e.printStackTrace();
            ;
            return null;
        }

    }

    public static JSONObject getJSOnObject(InputStream stream) {
        try {
            String json = IOUtils.toString(stream, "UTF-8");
            System.out.println("Parameter " + json);
            System.out.println("String " + String.valueOf(json) + " " + json.getClass());
            JSONObject object = new JSONObject(json);
            System.out.println("Parameter object " + object);
            return object;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
