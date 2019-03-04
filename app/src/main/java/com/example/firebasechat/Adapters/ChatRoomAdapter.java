package com.example.firebasechat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.firebasechat.Entity.Chatroom;
import com.example.firebasechat.R;

import java.util.ArrayList;

public class ChatRoomAdapter extends BaseAdapter {

    ArrayList<Chatroom> listOfChatRooms;
    Context context;

    public ChatRoomAdapter(Context context, ArrayList<Chatroom> listOfChatRooms)
    {
        this.context = context;
        this.listOfChatRooms = listOfChatRooms;
    }

    @Override
    public int getCount() {
        return listOfChatRooms.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //change values in each cardview item in list of chat rooms
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.chatroomlist_item,null);
        }

        Chatroom chatroom = listOfChatRooms.get(position);

        TextView roomName = (TextView)convertView.findViewById(R.id.roomName);
        TextView roomDescription = (TextView)convertView.findViewById(R.id.roomDescription);

        roomName.setText(chatroom.getRoomName());
        roomDescription.setText(chatroom.getDescription());

        return convertView;
    }
}
