package project.msd.teenviolence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by surindersokhal on 4/4/16.
 */
public class MenuPouplateItems {

    static Questions questions=null;
    public static void showDemo(){
        Intent intent=new Intent(questions,PlayDemo.class);
        intent.putExtra("speed",100);
        questions.startActivity(intent);

    }

    public static void logout(){
        Intent intent=new Intent(questions,Login_Activity.class);
        intent.putExtra("speed",100);
        questions.startActivity(intent);

    }
    public static void showHelp(){

        LayoutInflater inflater= LayoutInflater.from(questions);
        View view=inflater.inflate(R.layout.dialog_layout, null);

        final TextView textview=(TextView)view.findViewById(R.id.textmsg);
        textview.setText("Your really long message.");
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                questions);
        //  Dialog d = alertDialogBuilder.setView(new View(this)).create();
        // WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //lp.copyFrom(d.getWindow().getAttributes());
        //lp.width =(int)(WindowManager.LayoutParams.MATCH_PARENT*0.9);
        //lp.height = (int)(WindowManager.LayoutParams.MATCH_PARENT*0.9);
        //d.getWindow().setAttributes(lp);
        // set title
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle("Aggrement");
        boolean check=false;
        // set dialog message
        alertDialogBuilder

                .setCancelable(false)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }
                )
                .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textview.setText("I know it more");
            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alertDialog.dismiss();
            }
        });
    }

    public static void showFeedback(){
        Intent intent=new Intent(questions,Feedback.class);
        questions.startActivity(intent);
    }
}
