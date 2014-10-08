package it.moondroid.chatbot;

import android.app.Application;
import android.content.Intent;

/**
 * Created by marco.granatiero on 03/10/2014.
 */
public class ChatBotApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        BrainLogger.setup(this);

        Intent brainIntent = new Intent(this, BrainService.class);
        brainIntent.setAction(BrainService.ACTION_START);
        startService(brainIntent);
    }
}
