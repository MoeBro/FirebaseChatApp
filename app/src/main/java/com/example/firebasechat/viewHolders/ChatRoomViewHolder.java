package com.example.firebasechat.viewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firebasechat.R;

public class ChatRoomViewHolder extends RecyclerView.ViewHolder {
    public TextView roomNameView;
    public TextView descriptionView;
    public LinearLayout root;

    public ChatRoomViewHolder(@NonNull View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.list_root);
        roomNameView = itemView.findViewById(R.id.nameOfRoom);
        descriptionView = itemView.findViewById(R.id.roomDescription);
    }

    public void setRoomName (String nameOfRoom){
        roomNameView.setText(nameOfRoom);
    }

    public void setDescription(String description){
        descriptionView.setText(description);
    }

}
