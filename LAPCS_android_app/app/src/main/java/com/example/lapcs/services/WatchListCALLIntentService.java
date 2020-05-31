package com.example.lapcs.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lapcs.Activities.MainActivity;
import com.example.lapcs.models.CallInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.example.lapcs.AppConsts.TAG;

public class WatchListCALLIntentService extends IntentService {

    DatabaseReference mDatabase;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public WatchListCALLIntentService() {
        super("WatchListCALLIntentService");
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

        Log.d(TAG,"WatchListCALLIntentService: onHandleIntent");
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
                CallInfo callInfoObj = gson.fromJson(intent.getStringExtra("callInfoObj_JSON"), CallInfo.class);
                if(callInfoObj!=null)
                {
                    String contactNameToQuery = "";

                    if("Unknown".equals(callInfoObj.Name))
                    {
                        contactNameToQuery = "Unknown";
                    }
                    else
                    {
                        contactNameToQuery = callInfoObj.Name.trim();
                    }


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
                    Log.d(TAG, "WatchListSMSIntentService: callInfoObj is null");
                }

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