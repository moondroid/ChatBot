package it.moondroid.chatbot;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by marco.granatiero on 03/10/2014.
 */
public class ChatBotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BrainLogger.setup(this);

//        Intent loadBrainIntent = new Intent(this, LoadBrainService.class);
//        startService(loadBrainIntent);

        Intent brainIntent = new Intent(this, BrainService.class);
        brainIntent.putExtra("KEY1", "Value to be used by the service");
        startService(brainIntent);
    }
}
