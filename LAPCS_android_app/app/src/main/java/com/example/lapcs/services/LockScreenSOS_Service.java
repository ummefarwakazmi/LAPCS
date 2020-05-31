package com.example.lapcs.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.lapcs.AppConsts;
import com.example.lapcs.R;
import com.example.lapcs.Receivers.ScreenStateReceiver;

import static com.example.lapcs.AppConsts.TAG;

public class LockScreenSOS_Service extends Service {

    public ScreenStateReceiver mScreenStateReceiver;

    public LockScreenSOS_Service() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate: LockScreenSOS_Service ");
        mScreenStateReceiver = new ScreenStateReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // do some extra things
        Log.d(TAG,this.getClass().getName()+"onStartCommand: LockScreenSOS_Service Started");
        Toast.makeText(getApplicationContext(),"LockScreenSOS_Service Started",Toast.LENGTH_SHORT);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.setPriority(2147483647);

        registerReceiver(mScreenStateReceiver, filter);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG,"On Task Removed: LockScreenSOS_Service Restarted");
        Toast.makeText(getApplicationContext(),"LockScreenSOS_Service Restarted",Toast.LENGTH_SHORT);

        Intent restartServiceSOS_Service = new Intent(getApplicationContext(), this.getClass());
        restartServiceSOS_Service.setPackage(getPackageName());
        PendingIntent restartServiceSOS_ServicePI = PendingIntent.getService(
                getApplicationContext(), 5, restartServiceSOS_Service,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServiceSOS_ServicePI);
    }

    @Override
    public void onDestroy() {

        Log.d(AppConsts.TAG, this.getClass().getName()+": onDestroy(): SOS Service Destroyed");
        Toast.makeText(getApplicationContext(),"SOS Service Destroyed",Toast.LENGTH_SHORT);

        // release receiver
        if (mScreenStateReceiver != null)
        {
            unregisterReceiver(mScreenStateReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
