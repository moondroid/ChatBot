package it.moondroid.chatbot;

import android.app.IntentService;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;

import it.moondroid.chatbot.alice.Alice;

/**
 * Created by marco.granatiero on 03/10/2014.
 */

public class LoadBrainService extends IntentService {

    public LoadBrainService() {
        super("LoadBrainService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        //...
        // Do work here, based on the contents of dataString
        //...

        Alice alice = new Alice(this, new String[0]);

        Intent localIntent =
                new Intent(Constants.BROADCAST_ACTION)
                        // Puts the status into the Intent
                        .putExtra(Constants.EXTENDED_DATA_STATUS, 1);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}