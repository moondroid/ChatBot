package it.moondroid.chatbot;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import it.moondroid.chatbot.alice.Alice;
import it.moondroid.chatbot.eliza.Eliza;


public class MainActivity extends Activity {

    private ListView chatListView;
    private static ChatArrayAdapter adapter;
    private EditText chatEditText;
    private static boolean isBrainLoaded = false;
    //Eliza eliza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //eliza = new Eliza(this);

        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        // Instantiates a new DownloadStateReceiver
        ResponseReceiver mResponseReceiver =
                new ResponseReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mResponseReceiver, mStatusIntentFilter);

        if (savedInstanceState==null){
            adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_listitem);
        }

        chatListView = (ListView) findViewById(R.id.chat_listView);
        chatListView.setAdapter(adapter);

        chatEditText = (EditText) findViewById(R.id.chat_editText);
        chatEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    final String question = chatEditText.getText().toString();
                    adapter.add(new ChatMessage(false, question));
                    chatEditText.setText("");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            String response = "";
                            if(isBrainLoaded){
                                response = Alice.getInstance().processInput(question);
                            }else {
                                response = "My brain has not been loaded yet.";
                            }

                            adapter.add(new ChatMessage(true, response));
                        }
                    }, 1000);

                    return true;
                }

                return false;
            }
        });

        //hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Broadcast receiver for receiving status updates from the IntentService
    private class ResponseReceiver extends BroadcastReceiver
    {
        // Prevents instantiation
        private ResponseReceiver() {
        }
        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

        /*
         * Handle Intents here.
         */
          if (intent.getAction()==Constants.BROADCAST_ACTION){
              int status = intent.getIntExtra(Constants.EXTENDED_DATA_STATUS, 0);
              switch (status){
                  case Constants.STATUS_BRAIN_LOADED:
                      Toast.makeText(MainActivity.this, "brain loaded", Toast.LENGTH_SHORT).show();
                      isBrainLoaded = true;
                      break;

              }
          }

        }
    }
}
