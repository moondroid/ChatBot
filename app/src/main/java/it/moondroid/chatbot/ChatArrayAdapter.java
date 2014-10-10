package it.moondroid.chatbot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marco.granatiero on 30/09/2014.
 */
public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView messageTextView;
    private List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    private LinearLayout wrapper;

    @Override
    public void add(ChatMessage object) {
        chatMessages.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.chatMessages.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessages.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.chat_listitem, parent, false);
        }

        wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

        ChatMessage comment = getItem(position);

        messageTextView = (TextView) row.findViewById(R.id.text);

        messageTextView.setText(comment.text);

        messageTextView.setBackgroundResource(comment.left ? R.drawable.msg_in : R.drawable.msg_out);
        wrapper.setGravity(comment.left ? Gravity.LEFT : Gravity.RIGHT);

        return row;
    }

    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
