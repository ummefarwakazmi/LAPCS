package com.example.lapcs.Observers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.models.SMSInfo;
import com.example.lapcs.services.WatchListSMSIntentService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;

public class OutgoingSMSObserver  extends ContentObserver {

    public ContentResolver contentResolver;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String parentDeviceTokenID= "";

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public Context mContext;

    public void setInitialPos() {
        this.initialPos = getLastMsgId();
    }

    private int initialPos;

    HashMap<String,String> WatchListContactsMap;

    public void setWatchListContactsMap(HashMap<String, String> watchListContactsMap) {
        WatchListContactsMap = watchListContactsMap;
        Log.d(TAG, this.getClass().getName()+": "+"WatchListContactsMap after Reading in setWatchListContactsMap"+new Gson().toJson(WatchListContactsMap));
    }

    public void setBlockListContactsMap(HashMap<String, String> blockListContactsMap) {
        BlockListContactsMap = blockListContactsMap;
        Log.d(TAG, this.getClass().getName()+": "+"BlockListContactsMap after Reading in setBlockListContactsMap"+new Gson().toJson(BlockListContactsMap));
    }

    HashMap<String,String> BlockListContactsMap;

    public OutgoingSMSObserver(Handler handler) {
        super(handler);
        Log.d(TAG,"OutgoingSMSObserver: Constructor called!");
    }

    public OutgoingSMSObserver(Handler handler, Context context) {
        super(handler);
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();

        Log.d(TAG,"OutgoingSMSObserver: Constructor called!");
    }

    public OutgoingSMSObserver(Handler handler, HashMap<String,String> watchListContactsMap, HashMap<String,String> blockListContactsMap) {
        super(handler);
        this.WatchListContactsMap = watchListContactsMap;
        this.BlockListContactsMap = blockListContactsMap;
        Log.d(TAG,"OutgoingSMSObserver: Constructor called!");
    }

    //100   !=    100

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        //android.os.Debug.waitForDebugger();
        Log.d(TAG,"OutgoingSMSObserver: OnChange called!");
        Log.d(TAG,"initialPos= "+initialPos+"and  getLastMsgId()= "+getLastMsgId());

        try {

            Uri uriSMSURI = Uri.parse("content://sms/sent");
            Cursor cur = contentResolver.query(uriSMSURI, null, null, null, null);
            if (cur.moveToNext())
            {
                if (initialPos != getLastMsgId()) {

                    String content = cur.getString(cur.getColumnIndex("body"));
                    String smsNumber = cur.getString(cur.getColumnIndex("address"));
                    String contactName = "";

                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTimeInMillis(Long.parseLong(cur.getString(cur.getColumnIndex("date"))));
                    } catch (Exception ex) {
                        Log.w(TAG, "" + ex);
                    }

                    Log.d(TAG, "" + calendar.getTime());

                    if (smsNumber == null || smsNumber.length() <= 0)
                    {
                        smsNumber = "Unknown";
                    }

                    contactName=getContactName(mContext, smsNumber.toString());

                    //Saving Sent SMS in Firebase via Intent Service
                    Gson gson = new Gson();
                    String outgoingSMS_JSON = "";
                    SMSInfo smsInfoObj;

                    if(contactName != null)
                    {
                        Log.d(TAG, "OutgoingSMS to  "+contactName +" : " + smsNumber + ": " + content);

                        smsInfoObj = new SMSInfo(content,smsNumber, contactName, SMSInfo.GetCurrentTime(),"sent");
                        outgoingSMS_JSON = gson.toJson(smsInfoObj);

                        if (WatchListContactsMap.size()>0) {
                            Log.d(TAG, "WatchListContactsMap checking containsValue: " + new Gson().toJson(WatchListContactsMap));

                            if (WatchListContactsMap.containsKey(contactName.trim())) {
                                PushOutgoingSMSDataInFireBase(smsInfoObj);
                                parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                                if(!parentDeviceTokenID.equals(""))
                                {
                                    // Sending Notification to Parent Device
                                    PushNotificationHelper.SendPushNotification(mContext,parentDeviceTokenID,Server_API_key,ContentType," SMS Sent to : "+contactName,"Watch List Alert!");
                                }
                            }
                        }
                        else
                        {
                            Log.d(TAG,"WatchListContactsMap is empty");
                        }

                    }
                    else
                    {
                        Log.d(TAG, "OutgoingSMS to " + smsNumber + ": " + content);
                        smsInfoObj = new SMSInfo(content,smsNumber, "Unknown", SMSInfo.GetCurrentTime(),"sent");
                        outgoingSMS_JSON = gson.toJson(smsInfoObj);

                        if (WatchListContactsMap.size()>0)
                        {
                            Log.d(TAG, "WatchListContactsMap checking containsValue: "+new Gson().toJson(WatchListContactsMap));

                            String phoneToCompare           = smsNumber.trim();

                            if(phoneToCompare.startsWith("+"))
                            {
                                phoneToCompare = phoneToCompare.substring(phoneToCompare.length() - 10);
                                phoneToCompare = "0"+phoneToCompare;
                            }

                            Log.d(TAG,this.getClass().getName()+": Comparing: "+phoneToCompare);

                            if(WatchListContactsMap.containsValue(phoneToCompare.trim()))
                            {
                                PushOutgoingSMSDataInFireBase(smsInfoObj);
                                parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                                if(!parentDeviceTokenID.equals(""))
                                {
                                    // Sending Notification to Parent Device
                                    PushNotificationHelper.SendPushNotification(mContext,parentDeviceTokenID,Server_API_key,ContentType," SMS Sent to : "+phoneToCompare,"Watch List Alert!");
                                }
                            }
                        }
                        else
                        {
                            Log.d(TAG,"WatchListContactsMap is empty");
                        }

                    }

                    Log.d(TAG,"Now Deleting SMS! of ID Number="+getLastMsgId());
                    Log.d(TAG,"Result after Deleting SMS ="+deleteSms(getLastMsgId()));
                    Log.d(TAG,"After Deleting SMS! ==> ID Number="+getLastMsgId());

                    initialPos = getLastMsgId();

                }
            }
            cur.close();

        }catch (Exception ex)
        {
            Log.d(TAG,""+ex);
        }

    }

    public int getLastMsgId() {


        Cursor cur = contentResolver.query(Uri.parse("content://sms/sent"), null, null, null, null);
        cur.moveToFirst();
        int lastMsgId = cur.getInt(cur.getColumnIndex("_id"));
        Log.i(TAG, "Last sent message id: " + String.valueOf(lastMsgId));
        return lastMsgId;
    }

    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public void PushOutgoingSMSDataInFireBase(SMSInfo smsInfoObj)
    {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        ) {
            Log.d(TAG,"Imei: "+Imei+"userID: "+userID);

        }
        else
        {
            Log.d(TAG,"Imei and userID is empty !!");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String contactNameToQuery = "";

                if("Unknown".equals(smsInfoObj.ContactName))
                {
                    contactNameToQuery = "Unknown";
                }
                else
                {
                    contactNameToQuery = smsInfoObj.ContactName.trim();
                }

                String finalContactNameToQuery = contactNameToQuery;
                mDatabase.child("Users").child(userID).child(Imei).child("watchListDetails").child(contactNameToQuery).child("sms").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        List<SMSInfo> SMSInfoList = new ArrayList<SMSInfo>();

                        Log.d(TAG,dataSnapshot.toString());

                        if (dataSnapshot.getValue() != null) //Contact Number Already Exists in Database, so load its details first
                        {
                            if(!("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                            {
                                Log.d(TAG, "WatchListSMSIntentService: Contact Number Already Exists in Database.");

                                String smsStringFromDB = dataSnapshot.getValue().toString();
                                Log.d(TAG, "WatchListSMSIntentService: smsStringFromDB="+smsStringFromDB);

                                SMSInfoList = (List<SMSInfo>) new Gson().fromJson(smsStringFromDB,SMSInfoList.getClass());
                                Log.d(TAG, "WatchListSMSIntentService: SMSInfoList="+new Gson().toJson(SMSInfoList));

                            }

                        }

                        SMSInfoList.add(smsInfoObj);
                        Log.d(TAG, "WatchListSMSIntentService: SMSInfoList after updation="+new Gson().toJson(SMSInfoList));

                        mDatabase.child("Users").child(userID).child(Imei).child("watchListDetails").child(finalContactNameToQuery).child("sms").setValue(new Gson().toJson(SMSInfoList));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        }).start();


    }

    public boolean deleteSms(long paramLong) {

        Uri localUri = ContentUris.withAppendedId(Uri.parse("content://sms"),paramLong);
        boolean bool = false;
        if (localUri != null) {
            try {

                //int j =  contentResolver.delete(localUri,null, null);
                int j =  contentResolver.delete(Uri.parse("content://sms"),"_id=?",new String[] { String.valueOf(paramLong)});
                if (j == 1)
                    bool = true;
                else
                    bool = false;
            } catch (Exception localException) {
                localException.printStackTrace();
                bool = false;
            }
        }
        return bool;
    }



}
