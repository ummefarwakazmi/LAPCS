package com.example.lapcs.InitApp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lapcs.AppConsts;
import com.example.lapcs.R;
import com.example.lapcs.Utils.FireBaseUtils;
import com.example.lapcs.models.Child;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import static com.example.lapcs.AppConsts.DEFAULT_EMERGENCY_NUMBER;
import static com.example.lapcs.AppConsts.LAPCS_CHANNEL_ID;
import static com.example.lapcs.AppConsts.LAPCS_SOS_CHANNEL_ID;
import static com.example.lapcs.AppConsts.TAG;

public class App extends Application {

    public static String DeviceToken="";

    //private static List<Child> MyChildList;
    DatabaseReference mDatabase;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        DeviceToken = "NIL";
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        editor.putString("EmergencyNumber", DEFAULT_EMERGENCY_NUMBER);
        editor.commit();

        DeviceToken = sharedPreferences.getString("DeviceToken", "NIL");

        /* Enable disk persistence  */
        FireBaseUtils.getDatabase().setPersistenceEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.classic_alarm);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationChannel fcmChannel = new NotificationChannel(LAPCS_CHANNEL_ID, "LAPCS CHANNEL", NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            fcmChannel.setSound(soundUri, audioAttributes);

            manager.createNotificationChannel(fcmChannel);

            NotificationChannel sosChannel = new NotificationChannel(LAPCS_SOS_CHANNEL_ID, "LAPCS SOS CHANNEL", NotificationManager.IMPORTANCE_HIGH);
            //uncomment if wanna no sound for this channel
           // sosChannel.setSound(null,null);

            manager.createNotificationChannel(sosChannel);

        }

    }

}

