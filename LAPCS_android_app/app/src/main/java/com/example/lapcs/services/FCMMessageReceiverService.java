package com.example.lapcs.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;


import com.example.lapcs.AppConsts;
import com.example.lapcs.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.lapcs.AppConsts.LAPCS_CHANNEL_ID;

public class FCMMessageReceiverService extends FirebaseMessagingService {

    public FCMMessageReceiverService() {

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(AppConsts.TAG,"onMessageReceived: called ");
        Log.d(AppConsts.TAG,"onMessageReceived: Message received from: "+remoteMessage.getFrom());
        if(remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.classic_alarm);
            Notification notification = new NotificationCompat.Builder(this, LAPCS_CHANNEL_ID)
                    .setSmallIcon(R.drawable.lapcs_logo_2b)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSound(soundUri)
                    .setColor(Color.BLUE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) //to show content in lock screen
                    .build();

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(AppConsts.Notification_ID, notification);
        }

        if (remoteMessage.getData().size() > 0)
        {
            Log.d(AppConsts.TAG, "onMessageReceived: Data: "+remoteMessage.getData().toString());
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();

        Log.d(AppConsts.TAG, "onDeletedMessages: called");
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d(AppConsts.TAG, "onNewToken: called ");
        //upload this token on the app server
    }

}
