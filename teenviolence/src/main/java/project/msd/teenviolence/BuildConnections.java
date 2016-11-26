package project.msd.teenviolence;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import java.net.URL;
import java.util.Map;

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

    public static InputStream buildPostConnection(String urlString, HashMap<String, Object> params) throws IOException {
        try {

            if (urlString.contains(" ")) {
                urlString = urlString.replaceAll(" ", "%20");
            }
            URL url = new URL(urlString);
            System.out.println("URL :" + urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write(getPostDataString(params));
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream stream = httpURLConnection.getInputStream();
            System.out.println("strem "+stream);
            return stream;
        } catch (Exception e) {
            e.printStackTrace();
            ;
            return null;
        }

    }

    private static String getPostDataString(HashMap<String, Object> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, Object> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }

        return result.toString();
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
