package com.example.lapcs.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.models.SMSInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.lapcs.AppConsts.TAG;

public class WatchListSMSIntentService extends IntentService {

    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public WatchListSMSIntentService() {
        super("WatchListSMSIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        Log.d(TAG,"onCreate: WatchListSMSIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //android.os.Debug.waitForDebugger();
        Log.d(TAG,"WatchListSMSIntentService: onHandleIntent");
        if(intent!=null)
        {
            final String Imei = sharedPreferences.getString("Imei", "");
            final String userID = sharedPreferences.getString("UserID", "");

            if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                    !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
            )
            {

                Log.d(TAG,"Imei: "+Imei+"userID: "+userID);

                Gson gson = new Gson();
                SMSInfo incomingSMSObj = gson.fromJson(intent.getStringExtra("incomingSMS_JSON"), SMSInfo.class);
                String contactNameToQuery = "";

                if("Unknown".equals(incomingSMSObj.ContactName))
                {
                    contactNameToQuery = "Unknown";
                }
                else
                {
                    contactNameToQuery = incomingSMSObj.ContactName.trim();
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

                        SMSInfoList.add(incomingSMSObj);
                        Log.d(TAG, "WatchListSMSIntentService: SMSInfoList after updation="+new Gson().toJson(SMSInfoList));

                        mDatabase.child("Users").child(userID).child(Imei).child("watchListDetails").child(finalContactNameToQuery).child("sms").setValue(new Gson().toJson(SMSInfoList));

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
        else
        {
            Log.d(TAG, "WatchListSMSIntentService: intent is null");
        }
    }


}