package com.example.firebasechat.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasechat.Adapters.MessageAdapter;
import com.example.firebasechat.Entity.Chatroom;
import com.example.firebasechat.Entity.TextMessage;
import com.example.firebasechat.Notifications.NotificationHandler;
import com.example.firebasechat.R;
import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.share.Share;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class ChatRoomActivity extends AppCompatActivity {

    private ListView messageListView;
    public static final ArrayList<TextMessage> TextMessageList = new ArrayList<>();
    private String roomName;
    private DatabaseReference dbrootReference,dbMessageReference,dbSubscriberReference;
    EditText editText;
    ValueEventListener valueEventListener;
    FirebaseAuth mFirebaseAuth;
    TextView roomNameTitle;
    String senderName;
    private NotificationHandler handler;

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
        roomNameTitle.setText(roomName);
        dbrootReference = FirebaseDatabase.getInstance().getReference();
        dbMessageReference = dbrootReference.child("ChatroomMessage").child(roomName);
        dbSubscriberReference = dbrootReference.child("Subscribers").child(roomName);


        handler = new NotificationHandler(this);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                TextMessageList.clear();
                getDataFromDatabase(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbMessageReference.addListenerForSingleValueEvent(valueEventListener);

        //sending messages in the chatroom when pressing enter
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return false;
            }
        });



    }



    private void sendMessage() {

        //getting time and date of message
        Date currentDate = Calendar.getInstance().getTime();
        Long timeMs = Calendar.getInstance().getTimeInMillis();
        String messageText = editText.getText().toString();
        SimpleDateFormat simpleDateTime = new SimpleDateFormat("dd/MM/YYYY HH:mm");
        String messageDate = simpleDateTime.format(currentDate);
        //get all message fields and create new message
        TextMessage message = new TextMessage(senderName,messageDate,messageText);
        dbMessageReference.child(timeMs.toString()).setValue(message);
        editText.setText("");

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);


        messageListView.smoothScrollToPosition(messageListView.getCount());
        dbMessageReference.addListenerForSingleValueEvent(valueEventListener);

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

    private void getDataFromDatabase(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds: dataSnapshot.getChildren())
        {
            String name = ds.child("senderName").getValue(String.class);
            String date = ds.child("dateTime").getValue(String.class);
            String messageText = ds.child("text").getValue(String.class);
            TextMessageList.add(new TextMessage(name,date,messageText));
        }
        messageListView.setAdapter(new MessageAdapter(ChatRoomActivity.this, TextMessageList));
        messageListView.setSelection(messageListView.getCount()-1);
        messageListView.requestFocus();
    }

    public void sendNotification(String title,String message,String roomName){
        android.support.v4.app.NotificationCompat.Builder nfb = handler.getC1Notification(title,message,roomName);
        handler.myManager().notify(1,nfb.build());
    }

    public void returnButtonClick(View view) {
        finish();
    }

    //subscribe button, adds listener for new messages and notifies users of them.
    //on bug is that the user that sent the message also gets a notification
    public void subButtonClick(View view) {
        dbSubscriberReference.push().child("Name").setValue(getSenderName());
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("ChatroomMessage").child(roomName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child: children)
                {
                    TextMessage messageValue = child.getValue(TextMessage.class);
                    sendNotification("New Message:"+roomName,messageValue.getText(),roomName);
                    TextMessageList.clear();
                    getDataFromDatabase(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
