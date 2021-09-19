package com.parbathprojectmaps.Models;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.parbathprojectmaps.AlternativaMapa;
import com.parbathprojectmaps.MapsActivity;
import com.parbathprojectmaps.R;

public class DialogModule extends DialogFragment {
    String titleDialog = "No hay titulo!?";
    String messageDialog = "No hay mensaje!?";
    boolean answerDialog = false;

    public DialogModule(String titleDialog, String messageDialog){
        this.titleDialog = titleDialog;
        this.messageDialog = messageDialog;
    }

    public DialogModule(){
    }

    public AlertDialog.Builder showDialogModule(final FragmentActivity fragmentActivity){
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity);
        builder.setTitle(R.string.title_dialog);
        builder.setMessage(R.string.message_dialog)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Dialog LOG ","Respuesta si de dialogo");
                        answerDialog = true;

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Dialog LOG ","Respuesta no de dialogo");
                        answerDialog = false;
                    }
                });
        return builder;
    }

    public boolean isAnswerDialog() {
        return answerDialog;
    }
}
