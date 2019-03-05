package com.example.firebasechat.Entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.nio.ByteBuffer;

//model for individual messages
public class TextMessage {

    private String senderName;
    private String dateTime;
    private String messageText;


    public TextMessage(String senderName, String dateTime, String messageText) {
        this.senderName = senderName;
        this.dateTime = dateTime;
        this.messageText = messageText;
    }

    public TextMessage(){

    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getText() {
        return messageText;
    }

    public void setText(String messageText) {
        this.messageText = messageText;
    }


}
