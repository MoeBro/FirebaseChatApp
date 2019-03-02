package com.example.firebasechat.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.firebasechat.Models.Chatroom;
import com.example.firebasechat.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatRoomList extends AppCompatActivity {

    public ArrayList<Chatroom> ListOfChatRooms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);
        getSupportActionBar().hide();
    }

    //floating signout button
    public void signOutButton(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Sign out?")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        Intent intent = new Intent(ChatRoomList.this,LogInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("no",null).show();
    }

    public void newChatRoom(View view) {
    }
}
