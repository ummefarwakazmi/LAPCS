package com.example.lapcs.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.lapcs.AppConsts;
import com.example.lapcs.R;
import com.example.lapcs.services.SendMessageFromLockScreenService;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.lapcs.AppConsts.SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID;

public class ScreenStateUtils {

    public static void CreateLockScreenSOSNotification(Context context) {

        Intent MsgIntent = new Intent(AppConsts.LOCKSCREEN_SOS_MSG_INTENT);
        PendingIntent MsgPendingIntent = PendingIntent.getBroadcast(context, SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID, MsgIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent AlertIntent =  new Intent(AppConsts.LOCKSCREEN_SOS_ALERT_INTENT);
        PendingIntent AlertPendingIntent = PendingIntent.getBroadcast(context, SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID, AlertIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.layout_notification);
        view.setOnClickPendingIntent(R.id.notification_icon_iv, MsgPendingIntent);
        view.setOnClickPendingIntent(R.id.send_notification_btn_ib, AlertPendingIntent);

        Notification notification = new NotificationCompat.Builder(context, AppConsts.LAPCS_SOS_CHANNEL_ID)
                .setSmallIcon(R.drawable.lapcs_logo2)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setSmallIcon(R.drawable.abc_ic_menu_share_mtrl_alpha)
                .setContentTitle("SOS Notification")
                .setContentText("SOS")
                .setContent(view)
                .setSound(null)
                .build();

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID, notification);

    }
}
