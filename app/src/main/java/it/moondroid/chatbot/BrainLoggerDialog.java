package it.moondroid.chatbot;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import it.moondroid.chatbot.R;

/**
 * Created by Marco on 04/10/2014.
 */
public class BrainLoggerDialog extends DialogFragment {

    private ScrollView myScrollView;
    private TextView tv;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        myScrollView = (ScrollView)inflater.inflate(R.layout.dialog_brain_log, null, false);

        tv = (TextView) myScrollView
                .findViewById(R.id.textViewWithScroll);
        // Initializing a blank textview so that we can just append a text later
        //tv.setText("");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(myScrollView)
                .setTitle("Loading Brain")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @TargetApi(11)
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }

                });

        // Create the AlertDialog object and return it
        return builder.create();

    }


    public void addLine(String text) {
        if (tv != null) {
            tv.append(text+"\n");
            myScrollView.fullScroll(View.FOCUS_DOWN);
        }
    }

}
