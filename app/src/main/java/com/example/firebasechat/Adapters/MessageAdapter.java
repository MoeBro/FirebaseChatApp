package com.example.firebasechat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.firebasechat.Entity.TextMessage;
import com.example.firebasechat.R;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {

    ArrayList<TextMessage> MessageList;
    Context context;
    private int lastposition = -1;

    public MessageAdapter(Context context,ArrayList<TextMessage> messageList)
    {
        this.context = context;
        this.MessageList = messageList;
    }

    @Override
    public int getCount() {
        return MessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class Viewholder
    {
        TextView name;
        TextView date;
        TextView message;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;
        Viewholder viewholder;
        TextMessage message = MessageList.get(position);
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.messagelist_item, null);

            viewholder = new Viewholder();
            viewholder.message = (TextView) convertView.findViewById(R.id.messageText);
            viewholder.date = (TextView) convertView.findViewById(R.id.messageTime);
            viewholder.name = (TextView) convertView.findViewById(R.id.senderName);


            result = convertView;

            convertView.setTag(viewholder);
        } else{
            viewholder = (Viewholder) convertView.getTag();
            result = convertView;
        }

        viewholder.name.setText(message.getSenderName());
        viewholder.date.setText(message.getDateTime());
        viewholder.message.setText(message.getText());

        return convertView;
    }
}
