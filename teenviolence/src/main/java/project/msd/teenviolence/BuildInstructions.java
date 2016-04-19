package project.msd.teenviolence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.concurrent.Semaphore;

/**
 * Created by surindersokhal on 2/5/16.
 */
public class BuildInstructions {

    static final String URL="http://ec2-52-37-136-210.us-west-2.compute.amazonaws.com:8080/TeenViolence_Server/parameter/FetchInstruction";
    Context context=null;
    Semaphore semaphore=null;
    public BuildInstructions(Object object,Semaphore sem){
        if(object instanceof Register){
        context= (Register)object;}
        if(object instanceof HomeScreen){
            context= (HomeScreen)object;
        }
        semaphore=sem;
        new FetchInstructions().execute("");

    }

    class FetchInstructions extends AsyncTask<String,Void,String[]>{

        protected String[] doInBackground(String... parm){
            String instructions=null;
            try {
                InputStream stream = BuildConnections.buildConnection(URL);
                instructions= IOUtils.toString(stream,"UTF-8");
            }catch (Exception e){
                e.printStackTrace();
            }

            String[] array=instructions.split("\n");
            return array;
        }
        protected void onPostExecute(String array[]){
            displayLists(array);
        }
    }

    public void displayLists(String arg[]){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater =(LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View convertView = (View) inflater.inflate(R.layout.dialog_custom_list, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("List");
        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                semaphore.release();

            }
        });
        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,arg);
        lv.setAdapter(adapter);

        alertDialog.show();
    }
}
