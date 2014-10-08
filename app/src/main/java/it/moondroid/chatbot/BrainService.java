package it.moondroid.chatbot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import it.moondroid.chatbot.alice.Alice;

/**
 * Created by Marco on 06/10/2014.
 */
public class BrainService extends Service {

    public static final String ACTION_QUESTION = "it.moondroid.chatbot.BrainService.ACTION_QUESTION";
    public static final String ACTION_STOP = "it.moondroid.chatbot.BrainService.ACTION_STOP";
    public static final String ACTION_START = "it.moondroid.chatbot.BrainService.ACTION_START";

    public static final String EXTRA_QUESTION = "it.moondroid.chatbot.BrainService.EXTRA_QUESTION";

    private static final int NOTIFICATION_ID = 1337;

    private static Alice alice;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BrainService","onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent==null){
            Log.d("BrainService","onStartCommand() null");
            return Service.START_STICKY;
        }

        String action = intent.getAction();

        if(action.equalsIgnoreCase(ACTION_QUESTION)){

            String question = intent.getStringExtra(EXTRA_QUESTION);
            if(question!=null){
                Log.d("BrainService","onStartCommand() question:"+question);
                String answer = "";
                if (alice!=null) {
                    answer = alice.processInput(question);
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


        if(action.equalsIgnoreCase(ACTION_STOP)){
            Log.d("BrainService","onStartCommand() ACTION_STOP");
            stopForeground(true);
            stopSelf();
            return Service.START_NOT_STICKY;
        }

        if(action.equalsIgnoreCase(ACTION_START) && alice==null){
            Log.d("BrainService","onStartCommand() ACTION_START");
            (new LoadBrainThread()).start();
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        //return null;
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("BrainService", "onDestroy()");
    }

    /** method for clients */
    public boolean isBrainLoaded() {
        return alice!=null;
    }

    private final class LoadBrainThread extends Thread {
        @Override
        public void run() {
            Log.d("BrainService","LoadBrainThread run()");

            Intent loadingIntent =
                    new Intent(Constants.BROADCAST_ACTION_BRAIN_STATUS)
                            // Puts the status into the Intent
                            .putExtra(Constants.EXTRA_BRAIN_STATUS, Constants.STATUS_BRAIN_LOADING);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(loadingIntent);
            showLoadingNotification();

            alice = Alice.setup(BrainService.this);

            Intent loadedIntent =
                    new Intent(Constants.BROADCAST_ACTION_BRAIN_STATUS)
                            // Puts the status into the Intent
                            .putExtra(Constants.EXTRA_BRAIN_STATUS, Constants.STATUS_BRAIN_LOADED);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(BrainService.this).sendBroadcast(loadedIntent);

            showLoadedNotification();

        }
    }

    private void showLoadedNotification(){
        Intent openIntent = new Intent(BrainService.this, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openPIntent = PendingIntent.getActivity(BrainService.this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopIntent = new Intent(BrainService.this, BrainService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPIntent = PendingIntent.getService(BrainService.this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent restartIntent = new Intent(BrainService.this, BrainService.class);
        restartIntent.setAction(ACTION_START);
        PendingIntent restartPIntent = PendingIntent.getService(BrainService.this, 0, restartIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification note =
                new NotificationCompat.Builder(BrainService.this)
                        .setSmallIcon(R.drawable.ic_stat_notify_chat)
                        .setTicker("Brain Loaded")
                        .setContentTitle("Alice Brain")
                        .setContentText("You can talk to me")
                        .setSound(Uri.parse("android.resource://"
                                + getPackageName() + "/" + R.raw.notification_loaded))
                        .setAutoCancel(false)
                        .setContentIntent(openPIntent)
                        .setWhen(System.currentTimeMillis())
                        //.addAction(R.drawable.ic_stat_reload, "Restart", restartPIntent)
                        .addAction(R.drawable.ic_stat_stop, "Stop", stopPIntent)
                        .build();

        startForeground(NOTIFICATION_ID, note);
    }

    private void showLoadingNotification(){
        Intent openIntent = new Intent(BrainService.this, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent openPIntent = PendingIntent.getActivity(BrainService.this, 0, openIntent, 0);

        Intent stopIntent = new Intent(BrainService.this, BrainService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent stopPIntent = PendingIntent.getService(BrainService.this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification note =
                new NotificationCompat.Builder(BrainService.this)
                        .setSmallIcon(R.drawable.ic_stat_loading)
                        .setTicker("Loading Brain")
                        .setContentTitle("Alice Brain")
                        .setContentText("Loading...")
                        .setAutoCancel(false)
                        .setContentIntent(openPIntent)
                        .setWhen(System.currentTimeMillis())
                        .setProgress(0, 0, true)
//                        .addAction(R.drawable.ic_stat_reload, "Restart", restartPIntent)
                        .addAction(R.drawable.ic_stat_stop, "Stop", stopPIntent)
                        .build();

        startForeground(NOTIFICATION_ID, note);
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        BrainService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BrainService.this;
        }
    }



}
