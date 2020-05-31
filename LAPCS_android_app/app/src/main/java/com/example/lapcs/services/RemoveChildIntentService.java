package com.example.lapcs.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.Activities.SettingActivity;
import com.example.lapcs.AppConsts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.lapcs.AppConsts.TAG;

public class RemoveChildIntentService extends IntentService {

    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public RemoveChildIntentService() {
        super("RemoveChildIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

//        mDatabase.child(AppConsts.Users_Node_Ref).child(userID).child(Imei).setValue(null);
//        mDatabase.child(AppConsts.Mobiles_Node_Ref).child(Imei).setValue(null);
//        mDatabase.child(AppConsts.Child_Meta_Data_Node_Ref).child(userID).child(Imei).setValue(null);

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {
            Map updateMultipleDataHashMap = new HashMap();

            updateMultipleDataHashMap.put(AppConsts.Users_Node_Ref+"/"+userID+"/"+ Imei, null);
            updateMultipleDataHashMap.put(AppConsts.Mobiles_Node_Ref+"/"+Imei, null);
            updateMultipleDataHashMap.put(AppConsts.Child_Meta_Data_Node_Ref+"/"+userID+"/"+ Imei, null);

            mDatabase.updateChildren(updateMultipleDataHashMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be saved. " + databaseError.getMessage());
                        Log.d(TAG,this.getClass().getName()+": Child Node Removing Error!");
                    } else {
                        Log.d(TAG,this.getClass().getName()+": Child Node Removed Successfully!");

                        editor.putString("PIN","");
                        editor.putString("Mode","");
                        editor.putString("Imei","");
                        editor.putString("Email","");
                        editor.putString("UserID","");
                        editor.putString("userAdded","");
                        editor.putString("DeviceToken","");
                        editor.putString("SmsCommands","");
                        editor.putString("mobileAdded","");
                        editor.putString("ParentDeviceToken","");
                        editor.putString("userAddedWithMobile","");
                        editor.putString("LoginActivityVisited","");
                        editor.putString("BlockListContactsMap","");
                        editor.putString("WatchListContactsMap","");
                        editor.putString("EmergencyNumber","");

                        editor.commit();

                        Intent stopLocationServiceIntent = new Intent("stop");
                        sendBroadcast(stopLocationServiceIntent);

                        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancelAll();
                        notificationManager.cancel(AppConsts.Location_Notification_ID);
                        notificationManager.cancel(AppConsts.Notification_ID);
                        notificationManager.cancel(AppConsts.SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID);

                        stopService(new Intent(getApplicationContext(), TriggerEvents.class));
                        stopService(new Intent(getApplicationContext(), LocationService.class));
                        stopService(new Intent(getApplicationContext(), WatchListService.class));
                        stopService(new Intent(getApplicationContext(), LockScreenSOS_Service.class));
                        stopService(new Intent(getApplicationContext(), SendMessageFromLockScreenService.class));

                        //if getCurrentUser does not return null
                        if(FirebaseAuth.getInstance().getCurrentUser() != null){
                            //that means user is already logged in

                            //logging out the user
                            FirebaseAuth.getInstance().signOut();
                        }

                        Intent MainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                        MainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(MainActivityIntent);

                    }
                }
            });

        }
        else
        {
            Log.d(TAG,"Child Not Removed Because Imei and userID is empty !!");
        }
    }

}
