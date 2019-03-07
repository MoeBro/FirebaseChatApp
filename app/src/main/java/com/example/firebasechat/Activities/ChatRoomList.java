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
import com.example.firebasechat.ViewHolders.ChatRoomViewHolder;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        //Querying database to get chatrooms
        Query query = FirebaseDatabase.getInstance().getReference().child("Chatrooms").orderByChild("created");

        recyclerView = findViewById(R.id.chatRoomRecyclerView);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        //making a new chatroom for every item gotten from the query
        FirebaseRecyclerOptions<Chatroom> options = new FirebaseRecyclerOptions.Builder<Chatroom>().setQuery(query, new SnapshotParser<Chatroom>() {
            @NonNull
            @Override
            public Chatroom parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new Chatroom(snapshot.child("Name").getValue().toString(),snapshot.child("Description").getValue().toString());
            }
        }).build();
        //Making an adapter for the recyclerview using the chatroom viewholder
        adapter = new FirebaseRecyclerAdapter<Chatroom, ChatRoomViewHolder>(options){
            @NonNull
            @Override
            public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatroomlist_item,viewGroup,false);
                return new ChatRoomViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ChatRoomViewHolder holder, final int position, @NonNull final Chatroom model) {
                holder.setRoomName(model.getRoomName());
                holder.setDescription(model.getDescription());
                holder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //when clicking on chatroom go to that chatroom
                        String key = getRef(position).getKey();
                        Intent intent = new Intent(ChatRoomList.this,ChatRoomActivity.class);
                        //send the chatrooms name with the intent to the next activity
                        intent.putExtra("RoomName",model.getRoomName());
                        intent.putExtra("key",key);
                        ChatRoomList.this.startActivity(intent);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
        //refresh layout when swiping down
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }

    //floating sign out button
    //if user presses yes it signs out
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

    //Creating a new chatroom and inserting it into the firebase database
    public void newChatRoom(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new room");
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateTime = new SimpleDateFormat("dd/MM/YYYY HH:mm");
        final String createdDate = simpleDateTime.format(currentDate);
        Context context = view.getContext();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText nameBox = new EditText(context);
        nameBox.setHint("Enter Name");
        linearLayout.addView(nameBox);

        final EditText descriptionText = new EditText(context);
        descriptionText.setHint("Enter Description");
        linearLayout.addView(descriptionText);
        builder.setView(linearLayout);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap chatroomMap = new HashMap();
                chatroomMap.put("Name",nameBox.getText().toString());
                chatroomMap.put("Description",descriptionText.getText().toString());
                chatroomMap.put("created", createdDate);
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
