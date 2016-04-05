package project.msd.teenviolence;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.concurrent.Semaphore;

/**
 * Created by surindersokhal on 2/5/16.
 */
public class BuildInstructions {

    Context context=null;
    Semaphore semaphore=null;
    String[] instructions=null;
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
            String address="";
            //fetch address
            String[] array={"1","2","3","4","5"};
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
                // if this button is clicked, close
                // current activity
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
