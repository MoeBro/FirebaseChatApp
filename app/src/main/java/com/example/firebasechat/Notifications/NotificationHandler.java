package com.example.firebasechat.Notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.firebasechat.Activities.ChatRoomActivity;
import com.example.firebasechat.R;

public class NotificationHandler extends ContextWrapper {

      public static final String channelID = "channel1ID";
      public static final String channelName= "channel 1";
      private NotificationManager mManager;

      public NotificationHandler(Context base)
      {
          super(base);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O);{
              createChannels();
      }
      }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel channel1 = new NotificationChannel(channelID,channelName, NotificationManager.IMPORTANCE_HIGH);
        channel1.enableLights(true);

        myManager().createNotificationChannel(channel1);
    }

    public NotificationManager myManager()
    {
        if(mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getC1Notification(String title,String message,String roomName){
          Intent resultIntent = new Intent(this, ChatRoomActivity.class);
          resultIntent.putExtra("RoomName",roomName);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(getApplicationContext(),channelID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notificationiconsmall)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
    }

}
