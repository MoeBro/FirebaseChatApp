package com.example.firebasechat.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.firebasechat.Entity.TextMessage;
import com.example.firebasechat.Notifications.NotificationHandler;
import com.example.firebasechat.R;
import com.example.firebasechat.ViewHolders.MessageViewHolder;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class ChatRoomActivity extends AppCompatActivity {



    private String roomName;
    private DatabaseReference dbrootReference,dbMessageReference;
    EditText editText;
    ValueEventListener valueEventListener;
    FirebaseAuth mFirebaseAuth;
    TextView roomNameTitle;
    String roomKey;
    private NotificationHandler handler;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        editText = (EditText)findViewById(R.id.messageInput);
        roomNameTitle = (TextView)findViewById(R.id.roomNameHeader);
        //Getting the name of the chatroom from the previous activity
        Intent intent = getIntent();
        roomName = (String) intent.getSerializableExtra("RoomName");
        roomKey = (String) intent.getSerializableExtra("key");
        roomNameTitle.setText(roomName);
        dbrootReference = FirebaseDatabase.getInstance().getReference();
        dbMessageReference = dbrootReference.child("Chatrooms").child(roomKey);

        Query query = FirebaseDatabase.getInstance().getReference().child("Chatrooms").child(roomKey).child("Messages").orderByChild("Date");

        recyclerView = findViewById(R.id.messageRecyclerView);

        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        FirebaseRecyclerOptions<TextMessage> options = new FirebaseRecyclerOptions.Builder<TextMessage>().setQuery(query, new SnapshotParser<TextMessage>() {
            @NonNull
            @Override
            public TextMessage parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new TextMessage(snapshot.child("Name").getValue(String.class),
                        snapshot.child("Date").getValue(String.class),snapshot.child("Text").getValue(String.class));
            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<TextMessage, MessageViewHolder>(options){
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull TextMessage model) {
                holder.setMessageName(model.getSenderName());
                holder.setMessageDate(model.getDateTime());
                holder.setMessageText(model.getText());
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messagelist_item,viewGroup,false);
                return new MessageViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        handler = new NotificationHandler(this);

        //sending messages in the chatroom when pressing enter
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();

    }

    private void sendMessage() {

        //getting time and date of message
        Date currentDate = Calendar.getInstance().getTime();
        String messageText = editText.getText().toString();
        SimpleDateFormat simpleDateTime = new SimpleDateFormat("dd/MM/YYYY HH:mm");
        String messageDate = simpleDateTime.format(currentDate);
        //get all message fields and create new message
        HashMap messageMap = new HashMap();
        messageMap.put("Name",getSenderName());
        messageMap.put("Date",messageDate);
        messageMap.put("Text",messageText);
        dbMessageReference.child("Messages").push().setValue(messageMap);
        HashMap dateMap = new HashMap();
        dateMap.put("created",messageDate);
        dbMessageReference.updateChildren(dateMap);
        editText.setText("");

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    public String getSenderName()
    {
        //set senders name from either google or facebook login
        String senderName = "";
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (AccessToken.getCurrentAccessToken() != null)
        {
            senderName = Profile.getCurrentProfile().getName();
        }
        if (mFirebaseAuth.getCurrentUser() != null){
            senderName = mFirebaseAuth.getCurrentUser().getDisplayName();
        }
        return senderName;
    }



    public void sendNotification(String title,String message,String roomName){
        android.support.v4.app.NotificationCompat.Builder nfb = handler.getC1Notification(title,message,roomName);
        handler.myManager().notify(1,nfb.build());
    }

    public void returnButtonClick(View view) {
        finish();
    }

}
