package com.example.lapcs.Listeners;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.Utils.ServiceUtils;
import com.example.lapcs.models.CallInfo;
import com.example.lapcs.models.SMSInfo;
import com.example.lapcs.services.WatchListService;
import com.google.common.collect.HashBiMap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;

public class PhoneCallStateListener extends PhoneStateListener {

    private Context context;

    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String parentDeviceTokenID= "";

    HashMap<String,String> WatchListContactsMap=new HashMap<String,String>();
    HashMap<String,String> BlockListContactsMap=new HashMap<String,String>();

    public void setWatchListContactsMap(HashMap<String, String> watchListContactsMap) {
        WatchListContactsMap = watchListContactsMap;
        Log.d(TAG, this.getClass().getName()+": "+"WatchListContactsMap after Reading in setWatchListContactsMap"+new Gson().toJson(WatchListContactsMap));
    }

    public void setBlockListContactsMap(HashMap<String, String> blockListContactsMap) {
        BlockListContactsMap = blockListContactsMap;
        Log.d(TAG, this.getClass().getName()+": "+"BlockListContactsMap after Reading in setBlockListContactsMap"+new Gson().toJson(BlockListContactsMap));
    }

    private String eventStartTime = "";



    public PhoneCallStateListener(Context context,HashMap<String,String> WatchListContactsMap,HashMap<String,String> BlockListContactsMap){
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.WatchListContactsMap = WatchListContactsMap;
        this.BlockListContactsMap = BlockListContactsMap;
    }

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        Toast.makeText(context, "Phone Call State Listener is Active", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "WatchListContactsMap after Reading in onCallStateChanged"+new Gson().toJson(WatchListContactsMap));

        Log.d(TAG, "BlockListContactsMap after Reading in onCallStateChanged"+new Gson().toJson(BlockListContactsMap));


        switch (state) {


            /* Incoming call */
            case TelephonyManager.CALL_STATE_RINGING:
            {
                Log.d(TAG, "Incoming call from: " + incomingNumber+" at "+ SMSInfo.GetCurrentTime());

                ///////////////////////////////////////////////
                PushIncomingAndOutgoingCallDataInFireBase(incomingNumber,false);

            }
            break;
            case PhoneStateListener.LISTEN_CALL_STATE:
            {
                Log.d(TAG, "LISTEN_CALL_STATE");
            }
            break;
            /* Call ended */
            case TelephonyManager.CALL_STATE_IDLE:
            {
                Log.d(TAG, "IDLE");

            }
            break;

            /* Outgoing call */
            case TelephonyManager.CALL_STATE_OFFHOOK:
            {

                //android.os.Debug.waitForDebugger();
                Log.d(TAG, "OFFHOOK");
                eventStartTime = SMSInfo.GetCurrentTime();

                //Toast.makeText(context, "Outgoing call to: " + incomingNumber +" at "+eventStartTime, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Outgoing call to: " + incomingNumber +" at "+eventStartTime);

                ///////////////////////////////////////////////

                PushIncomingAndOutgoingCallDataInFireBase(incomingNumber,true);

                ///////////////////////////////////////////////

//                Log.d(TAG, this.getClass().getName()+": Blocking Outgoing call to: " + incomingNumber +" at "+ SMSInfo.GetCurrentTime());
//
//                ServiceUtils.BlockIncomingOrOutgoingCalls(context);
//
//                String blockCallMsg = "Outgoing call to: " + incomingNumber +" is Blocked at "+ SMSInfo.GetCurrentTime();
//                ServiceUtils.updateTriggerForBlockCallInFireBase(context,blockCallMsg);

            }
            break;

        }
        super.onCallStateChanged(state, incomingNumber);
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

    public String getContactNameFromWatchOrBlockList(String ContactNumber)
    {
        String ContactNameToReturn = null;

        if(!(   "".equals(ContactNumber.toString())) &&
                !(TextUtils.isEmpty(ContactNumber.toString())) &&
                !("Unknown".equals(ContactNumber.toString()))
        )
        {
            String phoneToCompare  = ContactNumber.trim();

            if(phoneToCompare.startsWith("+"))
            {
                phoneToCompare = phoneToCompare.substring(phoneToCompare.length() - 10);
                phoneToCompare = "0"+phoneToCompare;
            }

            if(WatchListContactsMap.containsValue(phoneToCompare.trim()))
            {
                HashBiMap<String, String> WatchListContactsBiMap = HashBiMap.create();
                WatchListContactsBiMap.putAll(WatchListContactsMap);
                ContactNameToReturn = WatchListContactsBiMap.inverse().get(phoneToCompare.trim());

            }

            if(BlockListContactsMap.containsValue(phoneToCompare.trim()))
            {
                HashBiMap<String, String> BlockListContactsBiMap = HashBiMap.create();
                BlockListContactsBiMap.putAll(BlockListContactsMap);
                ContactNameToReturn = BlockListContactsBiMap.inverse().get(phoneToCompare.trim());
            }
        }

        return ContactNameToReturn;
    }
    public void PushIncomingAndOutgoingCallDataInFireBase(String incomingNumber, boolean isOutgoing)
    {
        final String[] phoneNumber = {incomingNumber};

        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                String callInfoObj_JSON = "";
                CallInfo callInfoObj;
                String contactName = "";
                String contactNameToStore = "";
                String CallType = "";
                boolean outgoing = isOutgoing;
                long Stime =0, Etime = 0;

                if(phoneNumber[0].length()>0)
                {
                    contactName=getContactName(context, phoneNumber[0].toString());
                    if(contactName == null)
                    {
                        contactName = getContactNameFromWatchOrBlockList(phoneNumber[0].toString());
                    }
                }
                else
                {
                    phoneNumber[0] = "Unknown";
                }


                if(contactName != null)
                {
                    contactNameToStore = contactName;
                }
                else
                {
                    contactNameToStore = "Unknown";
                }

                Log.d(TAG,"New Call From : "+contactNameToStore+" Number: " + phoneNumber[0] + "  outgoing: " + outgoing + "  Call Length: 0 at "+CallInfo.GetCurrentTime());

                if (outgoing)   //outgoing true means -> outgoing
                {
                    CallType = "Outgoing";

                }
                else    //else call is incoming
                {
                    CallType = "Incoming";
                }

                //callInfoObj = new CallInfo(phoneNumber[0], outgoing,(int) ((Etime - Stime) / 1000), CallInfo.GetCurrentTime(),contactName,CallType);
                callInfoObj = new CallInfo(phoneNumber[0], outgoing, CallInfo.GetCurrentTime(),contactName,CallType);

                callInfoObj_JSON = gson.toJson(callInfoObj);

                if(contactName != null)
                {
                    if (WatchListContactsMap.size()>0)
                    {
                        Log.d(TAG, "WatchListContactsMap checking containsValue: "+new Gson().toJson(WatchListContactsMap));

                        if(WatchListContactsMap.containsKey(contactName.trim()))
                        {
                            WatchListService.serviceUtilsObj.PushCallDataInFireBase(context,callInfoObj_JSON);
                            sharedPreferences =context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
                            parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                            if(!parentDeviceTokenID.equals(""))
                            {
                                // Sending Notification to Parent Device
                                PushNotificationHelper.SendPushNotification(context,parentDeviceTokenID,Server_API_key,ContentType,CallType+" Call: "+contactName,"Watch List Alert!");
                            }
                    }
                    }
                    else
                    {
                        Log.d(TAG,"WatchListContactsMap is empty");
                    }

                    /////////////////////////////////////////////////////////

                    if (BlockListContactsMap.size()>0)
                    {
                        Log.d(TAG, "BlockListContactsMap checking containsValue: "+new Gson().toJson(BlockListContactsMap));

                        if(BlockListContactsMap.containsKey(contactName.trim()))
                        {
                            //ServiceUtils.BlockIncomingOrOutgoingCalls(context);

                            String blockCallMsg="";

                            if (outgoing)   //outgoing true means -> outgoing
                            {
                                blockCallMsg = "Outgoing call to: " + contactName +" is Blocked at "+ CallInfo.GetCurrentTime();
                            }
                            else
                            {
                                blockCallMsg = "Incoming call from: " + contactName +" is Blocked at "+ CallInfo.GetCurrentTime();
                            }

                            Log.d(TAG, this.getClass().getName()+": "+blockCallMsg);

                            ServiceUtils.updateTriggerForBlockCallInFireBase(context,blockCallMsg);
                            sharedPreferences =context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
                            parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                            if(!parentDeviceTokenID.equals(""))
                            {
                                // Sending Notification to Parent Device
                                PushNotificationHelper.SendPushNotification(context,parentDeviceTokenID,Server_API_key,ContentType,blockCallMsg,"Block List Alert!");
                            }

                        }
                    }
                    else
                    {
                        Log.d(TAG,"BlockListContactsMap is empty");
                    }

                    /////////////////////////////////////////////////////////


                }
                else
                {
                    if (WatchListContactsMap.size()>0)
                    {
                        Log.d(TAG, "WatchListContactsMap checking containsValue: "+new Gson().toJson(WatchListContactsMap));

                        String phoneToCompare           = phoneNumber[0].trim();

                        if(phoneToCompare.startsWith("+"))
                        {
                            phoneToCompare = phoneToCompare.substring(phoneToCompare.length() - 10);
                            phoneToCompare = "0"+phoneToCompare;
                        }

                        Log.d(TAG,this.getClass().getName()+": Comparing: "+phoneToCompare);

                        if(WatchListContactsMap.containsValue(phoneToCompare.trim()))
                        {
                            WatchListService.serviceUtilsObj.PushCallDataInFireBase(context,callInfoObj_JSON);
                            sharedPreferences =context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
                            parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                            if(!parentDeviceTokenID.equals(""))
                            {
                                // Sending Notification to Parent Device
                                PushNotificationHelper.SendPushNotification(context,parentDeviceTokenID,Server_API_key,ContentType,CallType+" Call: "+phoneToCompare,"Watch List Alert!");
                            }
                        }
                    }
                    else
                    {
                        Log.d(TAG,"WatchListContactsMap is empty");
                    }

                    /////////////////////////////////////////////////////////

                    if (BlockListContactsMap.size()>0)
                    {
                        Log.d(TAG, "BlockListContactsMap checking containsValue: "+new Gson().toJson(BlockListContactsMap));

                        String phoneToCompare           = phoneNumber[0].trim();

                        if(phoneToCompare.startsWith("+"))
                        {
                            phoneToCompare = phoneToCompare.substring(phoneToCompare.length() - 10);
                            phoneToCompare = "0"+phoneToCompare;
                        }


                        Log.d(TAG,this.getClass().getName()+": Comparing: "+phoneToCompare);


                        if(BlockListContactsMap.containsKey(phoneToCompare.trim()))
                        {
                           // ServiceUtils.BlockIncomingOrOutgoingCalls(context);

                            String blockCallMsg="";

                            if (outgoing)   //outgoing true means -> outgoing
                            {
                                blockCallMsg = "Outgoing call to: " + phoneToCompare +" is Blocked at "+ CallInfo.GetCurrentTime();
                            }
                            else
                            {
                                blockCallMsg = "Incoming call from: " + phoneToCompare +" is Blocked at "+ CallInfo.GetCurrentTime();
                            }

                            Log.d(TAG, this.getClass().getName()+": "+blockCallMsg);

                            ServiceUtils.updateTriggerForBlockCallInFireBase(context,blockCallMsg);
                            sharedPreferences =context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
                            parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                            if(!parentDeviceTokenID.equals(""))
                            {
                                // Sending Notification to Parent Device
                                PushNotificationHelper.SendPushNotification(context,parentDeviceTokenID,Server_API_key,ContentType,blockCallMsg,"Block List Alert!");
                            }

                        }
                    }
                    else
                    {
                        Log.d(TAG,"BlockListContactsMap is empty");
                    }

                    /////////////////////////////////////////////////////////


                }



            }
        }).start();


    }
}
