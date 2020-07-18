package com.example.lapcs.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.lapcs.Activities.SOSMessageActivity;
import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.Helpers.SmsHelper;
import com.example.lapcs.R;
import com.example.lapcs.Receivers.ScreenStateReceiver;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.LOCKSCREEN_SOS_ALERT_INTENT;
import static com.example.lapcs.AppConsts.LOCKSCREEN_SOS_MSG_INTENT;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;

public class SendMessageFromLockScreenService extends Service {

    SharedPreferences sharedPreferences;
    String parentDeviceTokenID= "";

    protected BroadcastReceiver mLockScreenSOSAlertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(LOCKSCREEN_SOS_MSG_INTENT))
            {
                Log.d(AppConsts.TAG,"Pressed SEND_MESSAGE_ACTION");

                String EmergencyNumber  = sharedPreferences.getString("EmergencyNumber", "");

                if (!EmergencyNumber.equalsIgnoreCase("")) {

                    if (SmsHelper.hasValidPreConditions(getApplicationContext(),EmergencyNumber))
                    {
                        SmsHelper.sendDebugSms(String.valueOf(EmergencyNumber), "SOS Message From Your ChildActivity! He is in Danger.");
                        Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                    }
                }

            }
            else if(intent.getAction().equals(LOCKSCREEN_SOS_ALERT_INTENT))
            {
                Log.d(AppConsts.TAG,"Pressed SEND_ALERT_ACTION");
                //Toast.makeText(getApplicationContext(),"Sending Panic Alert...", Toast.LENGTH_SHORT).show();

                parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                if(!parentDeviceTokenID.equals(""))
                {
                    PushNotificationHelper.SendPushNotification(getApplicationContext(), parentDeviceTokenID,Server_API_key,ContentType,"Your Child is in Danger","LAPCS Alert!");
                }
                else
                {
                    //Toast.makeText(getApplicationContext(), "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };



    public SendMessageFromLockScreenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate: SendMessageFromLockScreenService ");
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG,"On Task Removed: SendMessageFromLockScreenService Restarted");
        //Toast.makeText(getApplicationContext(),"SendMessageFromLockScreenService Restarted",Toast.LENGTH_SHORT);

        Intent restartLockScreenServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartLockScreenServiceIntent.setPackage(getPackageName());
        PendingIntent restartLockScreenServicePI = PendingIntent.getService(
                getApplicationContext(), 5, restartLockScreenServiceIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartLockScreenServicePI);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,this.getClass().getName()+": onStartCommand: SendMessageFromLockScreenService Started");

        IntentFilter filter = new IntentFilter();
        filter.addAction(LOCKSCREEN_SOS_MSG_INTENT);
        filter.addAction(LOCKSCREEN_SOS_ALERT_INTENT);

        registerReceiver(mLockScreenSOSAlertReceiver, filter);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        Log.d(AppConsts.TAG, this.getClass().getName()+": onDestroy(): SendMessageFromLockScreenService Destroyed");
        //Toast.makeText(getApplicationContext(),"SendMessageFromLockScreenService Destroyed",Toast.LENGTH_SHORT);

        // release receiver
        if (mLockScreenSOSAlertReceiver != null)
        {
            unregisterReceiver(mLockScreenSOSAlertReceiver);
        }
    }

}
