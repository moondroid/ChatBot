package it.moondroid.chatbot;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;

/**
 * Created by Marco on 04/10/2014.
 */
public class BrainLogger {

    private Context context;
    private static BrainLogger instance;
    private ArrayList<String> logs;

    public static void setup(Context context){
        instance = new BrainLogger(context);
    }

    public static BrainLogger getInstance(){
        if (instance==null){
            throw new UnsupportedOperationException("You forgot to call setup method to inizialize BrainLogger");
        }
        return instance;
    }

    private BrainLogger(Context context){
        this.context = context;
        logs = new ArrayList<String>();
    }

    public void info(String line){
        logs.add(line);
        notify(line);
    }

    public ArrayList<String> getLogs(){
        return logs;
    }

    private void notify(String line){
        Intent localIntent =
                new Intent(Constants.BROADCAST_ACTION_LOGGER)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTENDED_LOGGER_INFO, line);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }
}
