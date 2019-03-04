package com.example.firebasechat.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.firebasechat.Adapters.ChatRoomAdapter;
import com.example.firebasechat.Entity.Chatroom;
import com.example.firebasechat.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatRoomList extends AppCompatActivity {

    public static final ArrayList<Chatroom> ListOfChatRooms = new ArrayList<>();
    private ListView chatRoomListView;
    private DatabaseReference dbRootReference, dbChatRoomReference;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_chat_room_list);
        chatRoomListView = (ListView)findViewById(R.id.chatRoomList);
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        dbRootReference = FirebaseDatabase.getInstance().getReference();
        dbChatRoomReference = dbRootReference.child("Chatrooms");


        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);

        //database event listener
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ListOfChatRooms.clear();
                getDataFromDatabase(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Fail","failed to read value", databaseError.toException());
            }
        };
        dbChatRoomReference.addListenerForSingleValueEvent(valueEventListener);

        //swiping to refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                dbChatRoomReference.addListenerForSingleValueEvent(valueEventListener);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    public void getDataFromDatabase(DataSnapshot dataSnapshot)
    {
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            String name = ds.child("Name").getValue(String.class);
            String description = ds.child("Description").getValue(String.class);
            ListOfChatRooms.add(new Chatroom(name,description));
        }

        chatRoomListView.setAdapter(new ChatRoomAdapter(ChatRoomList.this,ListOfChatRooms));
        chatRoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChatRoomList.this,ChatRoomActivity.class);
                Chatroom chatroom = ListOfChatRooms.get(position);
                intent.putExtra("RoomName",chatroom.getRoomName());
                startActivity(intent);
            }
        });
    }


    //floating sign out button
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter description");

        Context context = view.getContext();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(context);
        nameBox.setHint("Name");
        linearLayout.addView(nameBox);

        final EditText descriptionText = new EditText(context);
        descriptionText.setHint("Time");
        linearLayout.addView(descriptionText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap chatroomMap = new HashMap();
                chatroomMap.put("Name",nameBox.getText().toString());
                chatroomMap.put("Description",descriptionText.getText().toString());
                dbRootReference.child("Chatrooms").push().setValue(chatroomMap);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();


    }


}
