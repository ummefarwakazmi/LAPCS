package com.example.lapcs.Receivers;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.Utils.ServiceUtils;
import com.example.lapcs.models.CallInfo;
import com.example.lapcs.models.SMSInfo;
import com.example.lapcs.services.SMSHarassmentCheckerIntentService;
import com.example.lapcs.services.WatchListSMSIntentService;
import com.example.lapcs.services.WatchListService;
import com.google.common.collect.HashBiMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;

public class IncomingSMSReceiver extends BroadcastReceiver {

    private String eventStartTime = "";

    private boolean ringing = true,start = false, outgoing = false;
    private long Stime = 0,Etime;
    private String number = "";
    String parentDeviceTokenID= "";
    HashMap<String,String> WatchListContactsMap=new HashMap<String,String>();
    HashMap<String,String> BlockListContactsMap=new HashMap<String,String>();

    SharedPreferences sharedPreferences;
    DatabaseReference mDatabase;

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if(intent.getAction().equals("lapcs.watchlist.DATA")){

            WatchListContactsMap = (HashMap<String, String>)intent.getSerializableExtra("WatchListContactsMap");
            Log.d(TAG, "WatchListContactsMap after Reading in BroadcastReceiver"+new Gson().toJson(WatchListContactsMap));

            BlockListContactsMap = (HashMap<String, String>)intent.getSerializableExtra("BlockListContactsMap");
            Log.d(TAG, "BlockListContactsMap after Reading in BroadcastReceiver"+new Gson().toJson(BlockListContactsMap));

        }

        mDatabase = FirebaseDatabase.getInstance().getReference();




        try {

            Log.d("Service", "Broadcast received something: " + intent.getAction());
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

                Log.d(TAG, "SMS Received");

                Bundle intentExtras = intent.getExtras();

                if (intentExtras != null) {

                    Object[] sms = (Object[]) intentExtras.get("pdus");

                    for (int i = 0; i < sms.length; ++i) {

                        SmsMessage smsMessage;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //KITKAT
                            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                            smsMessage = msgs[0];
                        } else {
                            Object pdus[] = (Object[]) intentExtras.get("pdus");
                            smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
                        }


                        String phone = smsMessage.getOriginatingAddress();
                        String message = smsMessage.getMessageBody().toString();
                        eventStartTime = SMSInfo.GetCurrentTime();

                        ///live data to firebase
                       // Toast.makeText(context, phone + ": " + message+" at "+eventStartTime, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Phone: " + phone + "     Message: " + message+" at "+eventStartTime);

                        //////////////////////////////////////////////////////////
                        String contactName = "";

                        contactName=getContactName(context, phone.toString());

                        //Saving Sent SMS in Firebase via Intent Service
                        Gson gson = new Gson();
                        String incomingSMS_JSON = "";
                        SMSInfo smsInfoObj;



                        if(contactName != null)
                        {
                            Log.d(TAG, "IncomingSMS From  "+contactName +" : " + phone + ": " + message);
                            smsInfoObj = new SMSInfo(message,phone, contactName, SMSInfo.GetCurrentTime(),"Received");
                            incomingSMS_JSON = gson.toJson(smsInfoObj);

                            Intent SMSHarassmentCheckerIntentServiceIntent = new Intent(context, SMSHarassmentCheckerIntentService.class);
                            SMSHarassmentCheckerIntentServiceIntent.putExtra("incomingSMS_JSON", incomingSMS_JSON);
                            context.startService(SMSHarassmentCheckerIntentServiceIntent);


                            if (WatchListContactsMap.size()>0)
                            {
                                Log.d(TAG, "WatchListContactsMap checking containsValue: "+new Gson().toJson(WatchListContactsMap));

                                if(WatchListContactsMap.containsKey(contactName.trim()))
                                {
                                    sharedPreferences =context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                    parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                                    if(!parentDeviceTokenID.equals(""))
                                    {
                                        // Sending Notification to Parent Device
                                        PushNotificationHelper.SendPushNotification(context,parentDeviceTokenID,Server_API_key,ContentType," SMS Received From : "+contactName,"Watch List Alert!");
                                    }

                                    Intent WatchListSMSIntentServiceIntent = new Intent(context, WatchListSMSIntentService.class);
                                    //WatchListSMSIntentServiceIntent.putExtra("test","test");
                                    WatchListSMSIntentServiceIntent.putExtra("incomingSMS_JSON", incomingSMS_JSON);
                                    context.startService(WatchListSMSIntentServiceIntent);
                                }
                            }
                            else
                            {
                                Log.d(TAG,"WatchListContactsMap is empty");
                            }

                        }
                        else
                        {
                            Log.d(TAG, "IncomingSMS From Unknown Number : " + phone + ": " + message);
                            smsInfoObj = new SMSInfo(message,phone, "Unknown", SMSInfo.GetCurrentTime(),"Received");
                            incomingSMS_JSON = gson.toJson(smsInfoObj);

                            Intent SMSHarassmentCheckerIntentServiceIntent = new Intent(context, SMSHarassmentCheckerIntentService.class);
                            SMSHarassmentCheckerIntentServiceIntent.putExtra("incomingSMS_JSON", incomingSMS_JSON);
                            context.startService(SMSHarassmentCheckerIntentServiceIntent);


                            if (WatchListContactsMap.size()>0)
                            {
                                Log.d(TAG, "WatchListContactsMap checking containsValue: "+new Gson().toJson(WatchListContactsMap));

                                String phoneToCompare           = phone.trim();

                                if(phoneToCompare.startsWith("+"))
                                {
                                    phoneToCompare = phoneToCompare.substring(phoneToCompare.length() - 10);
                                    phoneToCompare = "0"+phoneToCompare;
                                }

                                Log.d(TAG,this.getClass().getName()+": Comparing: "+phoneToCompare);

                                if(WatchListContactsMap.containsValue(phoneToCompare.trim()))
                                {

                                    sharedPreferences =context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                    parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                                    if(!parentDeviceTokenID.equals(""))
                                    {
                                        // Sending Notification to Parent Device
                                        PushNotificationHelper.SendPushNotification(context,parentDeviceTokenID,Server_API_key,ContentType," SMS Received From : "+phoneToCompare,"Watch List Alert!");
                                    }
                                    Intent WatchListSMSIntentServiceIntent = new Intent(context, WatchListSMSIntentService.class);
                                    //WatchListSMSIntentServiceIntent.putExtra("test","test");
                                    WatchListSMSIntentServiceIntent.putExtra("incomingSMS_JSON", incomingSMS_JSON);
                                    context.startService(WatchListSMSIntentServiceIntent);
                                }
                            }
                            else
                            {
                                Log.d(TAG,"WatchListContactsMap is empty");
                            }

                        }



                        //////////////////////////////////////////////////////////


                        if (BlockListContactsMap.size()>0)
                        {
                            Log.d(TAG, "BlockListContactsMap checking containsValue: "+new Gson().toJson(BlockListContactsMap));

                            if(BlockListContactsMap.containsValue(phone))
                            {
                                abortBroadcast();
                            }
                        }
                        else
                        {
                            Log.d(TAG,"BlockListContactsMap is empty");
                        }



                    }
                }

            }
            else if (intent.getAction().equals("android.provider.Telephony.SEND_SMS"))
            {
                Log.d(TAG,"SEND_SMS mein aya hai!");
            }
            else if (intent.getAction().equals("android.provider.Telephony.SMS_SENT"))
            {
                Log.d(TAG,"SMS_SENT mein aya hai!");
            }
            else if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION))
            {
                Log.d(TAG,"SMS_RECEIVED_ACTION mein b aya hai!");
            }
            else if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
            {


                eventStartTime = SMSInfo.GetCurrentTime();

                start = true;
                outgoing = true;
                Stime = System.currentTimeMillis();
                number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);


                //Toast.makeText(context, "Outgoing call to: " + intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)+" at "+eventStartTime, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Outgoing call to: " + intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)+" at "+eventStartTime);


                PushIncomingAndOutgoingCallDataInFireBase(context,number,true);

            }


        }catch (Exception ex)
        {
            Log.d(TAG,""+ex.getMessage().toString());
        }




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


    public void PushIncomingAndOutgoingCallDataInFireBase(Context context,String incomingNumber, boolean isOutgoing)
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
                            setResultData(null);

                            String blockCallMsg = "Outgoing call to: " + contactName +" is Blocked at "+ CallInfo.GetCurrentTime();
                            ServiceUtils.updateTriggerForBlockCallInFireBase(context,blockCallMsg);

                            Log.d(TAG, this.getClass().getName()+": "+blockCallMsg);

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
                            setResultData(null);

                            String blockCallMsg = "Outgoing call to: " + phoneToCompare +" is Blocked at "+ CallInfo.GetCurrentTime();
                            ServiceUtils.updateTriggerForBlockCallInFireBase(context,blockCallMsg);

                            Log.d(TAG, this.getClass().getName()+": "+blockCallMsg);

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
                WatchListContactsBiMap.putAll(WatchListContactsMap);    //copying
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


}