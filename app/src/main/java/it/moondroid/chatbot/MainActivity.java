package it.moondroid.chatbot;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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


public class MainActivity extends Activity {

    private static final String FRAGMENT_DIALOG_LOG_TAG = "BrainLoggerDialog";

    private ListView chatListView;
    private static ChatArrayAdapter adapter;
    private EditText chatEditText;
    private BrainLoggerDialog dialog;
    private ResponseReceiver mMessageReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getFragmentManager();

        if (savedInstanceState == null) {
            Log.d("MainActivity", "onCreate savedInstanceState null");
            adapter = new ChatArrayAdapter(getApplicationContext());

            dialog = new BrainLoggerDialog();
            if (!ChatBotApplication.isBrainLoaded()) {
                dialog.show(fm, FRAGMENT_DIALOG_LOG_TAG);
            } else {
                dialog.setPositiveButtonEnabled(true);
            }

        } else {
            Log.d("MainActivity", "onCreate savedInstanceState NOT null");
            dialog = (BrainLoggerDialog) fm.findFragmentByTag(FRAGMENT_DIALOG_LOG_TAG);
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

                    Intent brainIntent = new Intent(MainActivity.this, BrainService.class);
                    brainIntent.setAction(BrainService.ACTION_QUESTION);
                    brainIntent.putExtra(BrainService.EXTRA_QUESTION, question);
                    startService(brainIntent);

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
                Constants.BROADCAST_ACTION_BRAIN_STATUS);
        intentFilter.addAction(Constants.BROADCAST_ACTION_BRAIN_ANSWER);
        intentFilter.addAction(Constants.BROADCAST_ACTION_LOGGER);

        mMessageReceiver = new ResponseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, intentFilter);

        if (dialog != null && ChatBotApplication.isBrainLoaded()) {

            dialog.loadLog();
            dialog.setPositiveButtonEnabled(true);
        }
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

        if (id == R.id.action_log) {
            FragmentManager fm = getFragmentManager();
            dialog = new BrainLoggerDialog();
            dialog.show(fm, FRAGMENT_DIALOG_LOG_TAG);
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

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_STATUS)) {

                int status = intent.getIntExtra(Constants.EXTRA_BRAIN_STATUS, 0);
                switch (status) {

                    case Constants.STATUS_BRAIN_LOADING:
                        Toast.makeText(MainActivity.this, "brain loading", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.show(getFragmentManager(), FRAGMENT_DIALOG_LOG_TAG);
                        }
                        break;

                    case Constants.STATUS_BRAIN_LOADED:
                        Toast.makeText(MainActivity.this, "brain loaded", Toast.LENGTH_SHORT).show();
                        if (dialog != null) {
                            dialog.setPositiveButtonEnabled(true);
                        }
                        break;

                }
            }

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_BRAIN_ANSWER)) {
                String answer = intent.getStringExtra(Constants.EXTRA_BRAIN_ANSWER);
                adapter.add(new ChatMessage(true, answer));
                adapter.notifyDataSetChanged();
            }

            if (intent.getAction().equalsIgnoreCase(Constants.BROADCAST_ACTION_LOGGER)) {

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
