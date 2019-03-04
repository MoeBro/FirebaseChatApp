package com.example.firebasechat.Entity;
//model for individual messages
public class Message {

    private String senderName;
    private String dateTime;
    private String messageText;

    public Message(String senderName, String dateTime, String messageText) {
        this.senderName = messageText;
        this.dateTime = dateTime;
        this.messageText = messageText;
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
