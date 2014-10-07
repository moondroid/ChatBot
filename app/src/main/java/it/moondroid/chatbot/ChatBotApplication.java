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

        Intent brainIntent = new Intent(this, BrainService.class);
        brainIntent.putExtra(BrainService.COMMAND_ACTION, BrainService.ACTION_START);
        startService(brainIntent);
    }
}
