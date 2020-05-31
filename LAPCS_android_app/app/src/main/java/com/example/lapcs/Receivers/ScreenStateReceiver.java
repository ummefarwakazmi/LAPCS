package com.example.lapcs.Receivers;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.lapcs.AppConsts;
import com.example.lapcs.R;
import com.example.lapcs.Utils.ScreenStateUtils;
import com.example.lapcs.services.SendMessageFromLockScreenService;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.lapcs.AppConsts.LOCKSCREEN_SOS_ALERT_INTENT;
import static com.example.lapcs.AppConsts.LOCKSCREEN_SOS_MSG_INTENT;
import static com.example.lapcs.AppConsts.SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID;

public class ScreenStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_ON:
                // do your stuff
                Log.d(AppConsts.TAG, "In Method:  ACTION_SCREEN_ON");

                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if( myKM.inKeyguardRestrictedInputMode() ) {
                    Log.d(AppConsts.TAG, "In Method:  ACTION_SCREEN_ON but Screen is Still Locked");
                } else {
                    Log.d(AppConsts.TAG, "In Method:  ACTION_SCREEN_ON but Screen is UnLocked");
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(AppConsts.SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID);
                }

                break;
            case Intent.ACTION_SCREEN_OFF:
                // do your stuff
                Log.d(AppConsts.TAG, "In Method:  ACTION_SCREEN_OFF");
                ScreenStateUtils.CreateLockScreenSOSNotification(context);

                break;
            case Intent.ACTION_USER_PRESENT:
                // do your stuff
                Log.d(AppConsts.TAG, "In Method:  ACTION_USER_PRESENT");

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(AppConsts.SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID);

                break;
        }
    }

}
