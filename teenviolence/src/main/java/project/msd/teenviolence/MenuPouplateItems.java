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

    static HomeScreen questions=null;


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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                questions);
        alertDialogBuilder.setTitle("Help");
        alertDialogBuilder

                .setCancelable(false)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }
                );
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
}

    public static void showFeedback(String username){
        Intent intent=new Intent(questions,Feedback.class);
        intent.putExtra("username",username);
        questions.startActivity(intent);
    }
}
