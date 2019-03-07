package com.example.firebasechat.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firebasechat.Entity.TextMessage;
import com.example.firebasechat.Notifications.NotificationHandler;
import com.example.firebasechat.R;
import com.example.firebasechat.ViewHolders.MessageViewHolder;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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


    ArrayList subscriberList = new ArrayList();
    private String roomName;
    private DatabaseReference dbrootReference,dbMessageReference;
    EditText editText;
    FirebaseAuth mFirebaseAuth;
    TextView roomNameTitle;
    String roomKey;
    private NotificationHandler handler;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        final Uri googleAvatar = account.getPhotoUrl();

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        editText = findViewById(R.id.messageInput);
        roomNameTitle = findViewById(R.id.roomNameHeader);
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
                Glide.with(ChatRoomActivity.this).load(googleAvatar).into(holder.messageImage);

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
                dbMessageReference.child("Subscribers").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            System.out.println(dataSnapshot.toString());
                            if(!dataSnapshot.hasChild(getSenderName())){
                                System.out.println("DOES NOT EXIST");
                                AlertDialog dialog = new AlertDialog.Builder(ChatRoomActivity.this)
                                        .setTitle("Subscribe")
                                        .setMessage("Do you want to subscribe?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dbMessageReference.child("Subscribers").child(getSenderName()).setValue(getSenderName());
                                            }
                                        }).setNegativeButton("No",null)
                                        .show();
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                sendMessage();
                return true;
            }
        });
        fetchSubscribers();
        dbMessageReference.child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (subscriberList.contains(getSenderName())){
                    sendNotification("New Message!",roomName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fetchSubscribers(){
        dbMessageReference.child("Subscribers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    subscriberList.add(ds.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        //Update the rooms latest message received date
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


    public void sendNotification(String title,String roomName){
        android.support.v4.app.NotificationCompat.Builder nfb = handler.getC1Notification(title,roomName);
        handler.myManager().notify(1,nfb.build());
    }

    public void returnButtonClick(View view) {
        finish();
    }

}
