package com.example.lapcs.services;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.AppConsts;
import com.example.lapcs.Listeners.PhoneCallStateListener;
import com.example.lapcs.Observers.OutgoingSMSObserver;
import com.example.lapcs.Receivers.IncomingSMSReceiver;
import com.example.lapcs.Utils.ServiceUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.lapcs.AppConsts.INITIAL_DELAY_IN_SECONDS;
import static com.example.lapcs.AppConsts.NOTIFY_INTERVAL;
import static com.example.lapcs.AppConsts.TAG;

public class WatchListService extends Service {

    IntentFilter filter;

    Context mContext;
    DatabaseReference mDatabase;

    Calendar calendar;

    public static ServiceUtils serviceUtilsObj = new ServiceUtils();

    SharedPreferences sharedPreferences;

    HashMap<String,String> WatchListContactsMap=new HashMap<String,String>();
    HashMap<String,String> BlockListContactsMap=new HashMap<String,String>();

    PhoneCallStateListener customPhoneListener;


    // run on another Thread to avoid crash
    private Handler ServiceRunningCheckTaskHandler = new Handler();
    Timer timer = null;


    // Listener defined by anonymous inner class.
    public SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Log.d(TAG, "A preference has been changed in OnSharedPreferenceChangeListener: WatchListService");

            WatchListContactsMap =  new HashMap<String, String> (LoadHashMapFromSharedPref("WatchListContactsMap"));
            Log.d(TAG, "WatchListContactsMap after Reading in Service"+new Gson().toJson(WatchListContactsMap));

            BlockListContactsMap =  new HashMap<String, String> (LoadHashMapFromSharedPref("BlockListContactsMap"));
            Log.d(TAG, "BlockListContactsMap after Reading in Service"+new Gson().toJson(BlockListContactsMap));

            Intent watchListDataIntent = new Intent("lapcs.watchlist.DATA");
            watchListDataIntent.putExtra("WatchListContactsMap", WatchListContactsMap);
            watchListDataIntent.putExtra("BlockListContactsMap", BlockListContactsMap);
            sendBroadcast(watchListDataIntent);

            if(outgoingSMSObserver != null)
            {
                outgoingSMSObserver.setWatchListContactsMap(WatchListContactsMap);
                outgoingSMSObserver.setBlockListContactsMap(BlockListContactsMap);
            }

            if(customPhoneListener!=null)
            {
                customPhoneListener.setBlockListContactsMap(BlockListContactsMap);
                customPhoneListener.setWatchListContactsMap(WatchListContactsMap);
            }


        }
    };


    IncomingSMSReceiver serviceReceiver = new IncomingSMSReceiver();

    OutgoingSMSObserver outgoingSMSObserver;
    ContentResolver contentResolver;

    public WatchListService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG,"On Task Removed: WatchListService Restarted");

        Intent restartService = new Intent(getApplicationContext(), this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG,"onStartCommand: WatchListService");

        WatchListContactsMap =  new HashMap<String, String> (LoadHashMapFromSharedPref("WatchListContactsMap"));
        Log.d(TAG, "WatchListContactsMap after Reading in Service"+new Gson().toJson(WatchListContactsMap));

        BlockListContactsMap =  new HashMap<String, String> (LoadHashMapFromSharedPref("BlockListContactsMap"));
        Log.d(TAG, "BlockListContactsMap after Reading in Service"+new Gson().toJson(BlockListContactsMap));


        filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction("android.provider.Telephony.SMS_SENT");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        filter.addAction("android.provider.Telephony.SMS_DELIVER_ACTION");
        filter.addAction("lapcs.watchlist.DATA");
        filter.setPriority(2147483647);

        registerReceiver(serviceReceiver, filter);

        Intent watchListDataIntent = new Intent("lapcs.watchlist.DATA");
        watchListDataIntent.putExtra("WatchListContactsMap", WatchListContactsMap);
        watchListDataIntent.putExtra("BlockListContactsMap", BlockListContactsMap);

        sendBroadcast(watchListDataIntent);

        contentResolver = this.getContentResolver();

        outgoingSMSObserver.contentResolver = contentResolver;
        outgoingSMSObserver.setWatchListContactsMap(WatchListContactsMap);
        outgoingSMSObserver.setBlockListContactsMap(BlockListContactsMap);
        outgoingSMSObserver.setInitialPos();
        outgoingSMSObserver.setmContext(this);

        contentResolver.registerContentObserver(Uri.parse("content://sms"),true, outgoingSMSObserver);
        Log.d(TAG,"outgoingSMSObserver is also started");

        TelephonyManager telephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        customPhoneListener = new PhoneCallStateListener(getApplicationContext(),WatchListContactsMap,BlockListContactsMap);
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);



//        /////////////////////////////////////////////
//        calendar = Calendar.getInstance();
//        // cancel if already existed
//        if(timer != null) {
//            timer.cancel();
//        }
//        //Fixed-rate timers (scheduleAtFixedRate()) are based on the starting time (so each iteration will execute at startTime + iterationNumber * delayTime).
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new ServiceRunningCheckTask(), INITIAL_DELAY_IN_SECONDS,NOTIFY_INTERVAL);
//        /////////////////////////////////////////////



        return Service.START_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        mContext = getApplicationContext();

        Log.d(TAG,"onCreate: WatchListService");
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
        outgoingSMSObserver = new OutgoingSMSObserver(new Handler(),getApplicationContext());
        calendar = Calendar.getInstance();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"WatchListService Destroyed");
        unregisterReceiver(serviceReceiver);

        //contentResolver.unregisterContentObserver(outgoingSMSObserver);

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(mListener);
        super.onDestroy();
    }

    private Map<String,String> LoadHashMapFromSharedPref(String key)
    {
        Map<String,String> outputMap = new HashMap<String,String>();
        sharedPreferences =  getApplicationContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        try{
            if (sharedPreferences != null){
                String jsonString = sharedPreferences.getString(key, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k,v);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }


    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void runWatchListService()
    {
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {
            if(!isServiceRunning(WatchListService.class))
            {
                startService(new Intent(this, WatchListService.class));
                Log.d(AppConsts.TAG,"PublicHomeActivity:    WatchListService is Started !");
            }
            else
            {
                Log.d(AppConsts.TAG,"PublicHomeActivity:    WatchListService is Already Running !");
            }

        }
        else
        {
            Log.d(TAG,"Imei and userID is empty !!");
        }

    }


    private class ServiceRunningCheckTask extends TimerTask
    {
        @Override
        public void run() {
            // run on another thread
            ServiceRunningCheckTaskHandler.post(new Runnable() {

                @Override
                public void run() {

                    Log.d(TAG,"Timer"+calendar.getTime());
                    Log.d(TAG,"Checking WatchListService Timer at: "+"Day_"+calendar.get(Calendar.YEAR)+"_"+(calendar.get(Calendar.MONTH)+1)+"_"+calendar.get(Calendar.DAY_OF_MONTH));

                    runWatchListService();
                }

            });
        }
    }

}
