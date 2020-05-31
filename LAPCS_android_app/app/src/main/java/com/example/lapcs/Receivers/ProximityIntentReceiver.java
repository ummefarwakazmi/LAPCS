package com.example.lapcs.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;

public class ProximityIntentReceiver extends BroadcastReceiver {

    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        Boolean entering = intent.getBooleanExtra(key, false);

        if (entering) {
            String msg = "Your Child  " + Build.MODEL + " is Entering Proximity Area";
            Log.d(AppConsts.TAG, msg);
            String parentDeviceTokenID = "";
            sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
            parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

            if (!parentDeviceTokenID.equals("")) {
                // Sending Notification to Parent Device
                PushNotificationHelper.SendPushNotification(context, parentDeviceTokenID, Server_API_key, ContentType, msg, "LAPCS Proximity Alert!");
                Log.d(TAG, "Proximity Alert Message sent to parent");
            } else {
                Log.d(TAG, "Parent Device is Not Linked. Notification To Parent Sending Failed! ");
                Toast.makeText(context, "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_LONG).show();
            }

        }
        else {
            String msg = "Your Child  " + Build.MODEL + " is Leaving Proximity Area";
            Log.d(AppConsts.TAG, msg);
            String parentDeviceTokenID = "";
            sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
            parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

            if (!parentDeviceTokenID.equals("")) {
                // Sending Notification to Parent Device
                PushNotificationHelper.SendPushNotification(context, parentDeviceTokenID, Server_API_key, ContentType, msg, "LAPCS Proximity Alert!");
                Log.d(TAG, "Proximity Alert Message sent to parent");
            } else {
                Log.d(TAG, "Parent Device is Not Linked. Notification To Parent Sending Failed! ");
                Toast.makeText(context, "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_LONG).show();
            }
        }

    }
}
