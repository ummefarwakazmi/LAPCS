package com.example.lapcs.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.AppConsts;
import com.example.lapcs.models.CallInfo;
import com.example.lapcs.models.SMSInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.lapcs.AppConsts.TAG;

public class ServiceUtils {

    public Calendar calendar;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public ServiceUtils()
    {
        calendar = GetCurrentTime();
    }

    private Calendar GetCurrentTime()
    {
        return Calendar.getInstance();
    }


    public void PushCallDataInFireBase(Context context, String callInfoObj_JSON )
    {
        Log.d(TAG,"WatchListService: PushCallDataInFireBase ");
        Log.d(TAG,"PushCallDataInFireBase :"+callInfoObj_JSON);

        sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {


            Log.d(TAG,"Imei: "+Imei+"userID: "+userID);



            Gson gson = new Gson();
            CallInfo callInfoObj = gson.fromJson(callInfoObj_JSON, CallInfo.class);
            String contactNameToQuery = "";

            if("Unknown".equals(callInfoObj.Name))
            {
                contactNameToQuery = "Unknown";
            }
            else
            {
                contactNameToQuery = callInfoObj.Name.trim();
            }


            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String finalContactNameToQuery = contactNameToQuery;
            mDatabase.child("Users").child(userID).child(Imei).child("watchListDetails").child(contactNameToQuery).child("calls").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    List<CallInfo> callInfoList = new ArrayList<CallInfo>();

                    Log.d(TAG,dataSnapshot.toString());

                    if (dataSnapshot.getValue() != null) //Contact Number Already Exists in Database, so load its details first
                    {
                        if(!("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                        {
                            Log.d(TAG, "WatchListSMSIntentService: Contact Number Already Exists in Database.");

                            String callsStringFromDB = dataSnapshot.getValue().toString();
                            Log.d(TAG, "WatchListSMSIntentService: callsStringFromDB="+callsStringFromDB);

                            callInfoList = (List<CallInfo>) new Gson().fromJson(callsStringFromDB,callInfoList.getClass());
                            Log.d(TAG, "WatchListSMSIntentService: callInfoList="+new Gson().toJson(callInfoList));

                        }

                    }

                    callInfoList.add(callInfoObj);
                    Log.d(TAG, "WatchListSMSIntentService: callInfoList after updation="+new Gson().toJson(callInfoList));

                    mDatabase.child("Users").child(userID).child(Imei).child("watchListDetails").child(finalContactNameToQuery).child("calls").setValue(new Gson().toJson(callInfoList));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Log.d(TAG,"Imei and userID is empty !!");
        }

    }

    public static boolean isServiceRunning(Activity context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void updateMobileDataInFireBase(Context context, String childIMEI, String mobileData )
    {
       // Toast.makeText(context, "Data Change Showing from Service Utils: childIMEI: "+childIMEI+" Device Name: "+mobileData , Toast.LENGTH_SHORT).show();

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(AppConsts.Mobiles_Node_Ref+"/"+childIMEI).setValue(mobileData);

    }

    public static void updateTriggerInFireBase(Context context, String childIMEI, String mobileData )
    {
        //Toast.makeText(context, "Data Change Showing from Service Utils: childIMEI: "+childIMEI+" Device Name: "+mobileData , Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);

        final String userID = sharedPreferences.getString("UserID", "");

        if(
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {

            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("Users").child(userID).child(childIMEI).child("trigger").setValue("NA");
            mDatabase.child("Users").child(userID).child(childIMEI).child("trigger").setValue(childIMEI + "/remove");



        }
        else
        {
            Log.d(TAG,"userID is empty !!");
            //Toast.makeText(context, " Device "+mobileData + " Can't be Removed because User ID is empty !!" , Toast.LENGTH_SHORT).show();

        }

    }

//    public static void BlockIncomingOrOutgoingCalls(Context context) {
//        try
//        {
//
//            boolean isMuteAlreadyON = false;
//            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//
//            int rignerModeState = audioManager.getRingerMode();
//
//            if( rignerModeState == AudioManager.RINGER_MODE_SILENT || rignerModeState == AudioManager.RINGER_MODE_VIBRATE )
//            {
//                Log.d(TAG, ServiceUtils.class.getName()+": Mute is Already Turned ON . . . ");
//                isMuteAlreadyON = true;
//            }
//            else
//            {
//                Log.d(TAG, ServiceUtils.class.getName()+": Turning ON the Mute . . . ");
//                isMuteAlreadyON = false;
//                //Turn ON the mute
//                audioManager.setStreamMute(AudioManager.STREAM_RING, true);
//            }
//
//            Log.d(TAG, ServiceUtils.class.getName()+": Call Ending Started . . . ");
//            // Java reflection to gain access to TelephonyManager's
//            // ITelephony getter
//            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            Class c = Class.forName(tm.getClass().getName());
//            Method m = c.getDeclaredMethod("getITelephony");
//            m.setAccessible(true);
//            com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m.invoke(tm);
//            telephonyService = (ITelephony) m.invoke(tm);
//            //
//            telephonyService.silenceRinger();
//            telephonyService.endCall();
//            Log.d(TAG, ServiceUtils.class.getName()+": Call Ending Done . . . ");
//
//            if(isMuteAlreadyON == false)
//            {
//                Log.d(TAG, ServiceUtils.class.getName()+": Turning OFF the Mute . . . ");
//                //Turn OFF the mute
//                audioManager.setStreamMute(AudioManager.STREAM_RING, false);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d(TAG, ServiceUtils.class.getName()+": Call Ending Exception Thrown . . . ");
//        }
//    }

    public static void updateTriggerForBlockCallInFireBase(Context context, String blockCallMSg )
    {
//        Toast.makeText(context, ServiceUtils.class.getName()+": blockCallMSg: "+blockCallMSg , Toast.LENGTH_SHORT).show();
        Log.d(TAG, ServiceUtils.class.getName()+": blockCallMSg: "+blockCallMSg);

        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);

        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {
            Log.d(TAG,"Imei: "+Imei+"userID: "+userID);

            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("Users").child(userID).child(Imei).child("trigger").setValue("NA");
            mDatabase.child("Users").child(userID).child(Imei).child("trigger").setValue(Imei + "/block/"+blockCallMSg);

        }
        else
        {
            Log.d(TAG,ServiceUtils.class.getName()+": Imei and userID is empty !!");

        }

    }


    public static void declinePhone(Context context) {
        try
        {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";
            Class<?> telephonyClass;
            Class<?> telephonyStubClass;
            Class<?> serviceManagerClass;
            Class<?> serviceManagerNativeClass;
            Method telephonyEndCall;
            Object telephonyObject;
            Object serviceManagerObject;
            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);
            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);
            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);
            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");
            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);
            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("unable", "msg cant dissconect call....");
        }
    }

}