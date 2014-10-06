package it.moondroid.chatbot;

import android.app.Activity;
import android.app.FragmentManager;
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


public class MainActivity extends Activity {

    private ListView chatListView;
    private static ChatArrayAdapter adapter;
    private EditText chatEditText;
    private static boolean isBrainLoaded = false;
    private BrainLoggerDialog dialog;
    private ResponseReceiver mMessageReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_listitem);

            FragmentManager fm = getFragmentManager();
            dialog = new BrainLoggerDialog();
            dialog.show(fm, "BrainLoggerDialog");
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
                            if (isBrainLoaded) {
                                response = Alice.getInstance().processInput(question);
                            } else {
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
    protected void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        IntentFilter intentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION_BRAIN_LOADING);
        intentFilter.addAction(Constants.BROADCAST_ACTION_LOGGER);

        mMessageReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        super.onPause();
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
    private class ResponseReceiver extends BroadcastReceiver {

        private ResponseReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == Constants.BROADCAST_ACTION_BRAIN_LOADING) {

                int status = intent.getIntExtra(Constants.EXTENDED_BRAIN_STATUS, 0);
                switch (status) {
                    case Constants.STATUS_BRAIN_LOADED:
                        Toast.makeText(MainActivity.this, "brain loaded", Toast.LENGTH_SHORT).show();
                        isBrainLoaded = true;
                        break;

                }
            }

            if (intent.getAction() == Constants.BROADCAST_ACTION_LOGGER) {

                String info = intent.getStringExtra(Constants.EXTENDED_LOGGER_INFO);
                if (info != null) {
                    Log.i("EXTENDED_LOGGER_INFO", info);
                    if (dialog != null) {
                        dialog.addLine(info);
                    }
                }

            }


        }
    }




}
