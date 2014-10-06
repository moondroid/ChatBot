package it.moondroid.chatbot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import it.moondroid.chatbot.alice.Alice;

/**
 * Created by Marco on 06/10/2014.
 */
public class BrainService extends Service {

    public static final String KEY_QUESTION = "it.moondroid.chatbot.BrainService.KEY_QUESTION";
    private static boolean isBrainLoaded = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BrainService","onCreate()");
        //TODO
        (new BackgroundThread()).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent==null){
            return Service.START_STICKY;
        }

        String question = intent.getStringExtra(KEY_QUESTION);
        if(question!=null){
            Log.d("BrainService","onStartCommand() question:"+question);
            String answer = "";
            if (isBrainLoaded) {
                answer = Alice.getInstance().processInput(question);
            } else {
                answer = "My brain has not been loaded yet.";
            }

            Intent localIntent =
                    new Intent(Constants.BROADCAST_ACTION_BRAIN_ANSWER)
                            // Puts the answer into the Intent
                            .putExtra(Constants.EXTRA_BRAIN_ANSWER, answer);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent);
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    private final class BackgroundThread extends Thread {
        @Override
        public void run() {

            Alice.setup(BrainService.this);

            Intent localIntent =
                    new Intent(Constants.BROADCAST_ACTION_BRAIN_LOADING)
                            // Puts the status into the Intent
                            .putExtra(Constants.EXTENDED_BRAIN_STATUS, Constants.STATUS_BRAIN_LOADED);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(localIntent);

            isBrainLoaded = true;
        }
    }
}
