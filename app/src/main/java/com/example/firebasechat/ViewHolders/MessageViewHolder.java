package com.example.firebasechat.ViewHolders;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.firebasechat.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageName;
    public TextView messageDate;
    public TextView messageText;
    public LinearLayout messageRoot;
    public ImageView messageImage;


    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageRoot = itemView.findViewById(R.id.message_root);
        messageName = itemView.findViewById(R.id.messageName);
        messageDate = itemView.findViewById(R.id.messageDate);
        messageText = itemView.findViewById(R.id.messageText);
        messageImage = itemView.findViewById(R.id.messageImage);

    }

   public void setMessageName(String messagename){
        messageName.setText(messagename);
   }

    public void setMessageDate(String messagedate){
        messageDate.setText(messagedate);
    }

    public void setMessageText(String messagetext){
        messageText.setText(messagetext);
    }


}
