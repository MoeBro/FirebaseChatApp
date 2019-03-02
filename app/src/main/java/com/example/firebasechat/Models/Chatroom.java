package com.example.firebasechat.Models;

public class Chatroom {

    private String roomName;
    private String description;

    public Chatroom(String name, String description){
        this.roomName = name;
        this.description = description;
    }
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
