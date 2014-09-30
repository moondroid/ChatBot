package it.moondroid.chatbot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;


import it.moondroid.chatbot.eliza.Eliza;


public class MainActivity extends Activity {

    private ListView chatListView;
    private ChatArrayAdapter adapter;
    private EditText chatEditText;

    Eliza eliza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eliza = new Eliza(this);

        chatListView = (ListView) findViewById(R.id.chat_listView);

        adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.chat_listitem);
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
                            String response = eliza.processInput(question);
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
}
