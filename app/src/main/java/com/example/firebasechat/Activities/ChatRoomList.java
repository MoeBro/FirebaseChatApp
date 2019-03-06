package com.example.firebasechat.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.firebasechat.Entity.Chatroom;
import com.example.firebasechat.R;
import com.example.firebasechat.viewHolders.ChatRoomViewHolder;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChatRoomList extends AppCompatActivity {

    private DatabaseReference dbRootReference, dbChatRoomReference;
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        getSupportActionBar().hide();
        dbRootReference = FirebaseDatabase.getInstance().getReference();
        dbChatRoomReference = FirebaseDatabase.getInstance().getReference().child("Chatrooms");

        recyclerView = findViewById(R.id.chatRoomRecyclerView);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<Chatroom> options = new FirebaseRecyclerOptions.Builder<Chatroom>().setQuery(dbChatRoomReference, new SnapshotParser<Chatroom>() {
            @NonNull
            @Override
            public Chatroom parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Chatroom(snapshot.child("Name").getValue().toString(),snapshot.child("Description").getValue().toString());
            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<Chatroom, ChatRoomViewHolder>(options){

            @Override
            protected void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position, @NonNull Chatroom model) {
                holder.setRoomName(model.getRoomName());
                holder.setDescription(model.getDescription());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @NonNull
            @Override
            public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatroomlist_item,viewGroup,false);
                return new ChatRoomViewHolder(view);
            }

        };
        recyclerView.setAdapter(adapter);

        //database event listener
    }


    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
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
        builder.setTitle("Add a new room");

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
