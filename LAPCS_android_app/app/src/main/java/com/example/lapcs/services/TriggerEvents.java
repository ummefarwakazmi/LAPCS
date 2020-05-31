package com.example.lapcs.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lapcs.Activities.LockScreen;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.Utils.ScreenStateUtils;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;

public class TriggerEvents extends Service {
    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    HashMap<String,String> WatchListContactsMap=new HashMap<String,String>();
    HashMap<String,String> BlockListContactsMap=new HashMap<String,String>();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public TriggerEvents() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate(){
        super.onCreate();

        Log.d(TAG,"onCreate: TriggerEvents");

        WatchListContactsMap =  new HashMap<String, String> (LoadHashMapFromSharedPref("WatchListContactsMap"));
        Log.d(TAG, "onCreate: TriggerEvents WatchListContactsMap :"+new Gson().toJson(WatchListContactsMap));

        BlockListContactsMap =  new HashMap<String, String> (LoadHashMapFromSharedPref("BlockListContactsMap"));
        Log.d(TAG, "onCreate: TriggerEvents BlockListContactsMap :"+new Gson().toJson(BlockListContactsMap));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {

            Log.d(TAG,"Imei: "+Imei+"userID: "+userID);

            //getting DB values at start without any event occur
            mDatabase.child("Users").child(userID).child(Imei).child("blockedContacts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                    {
                        String blockedContactsString = dataSnapshot.getValue().toString();
                        Log.d(TAG,"\nblockedContactsString=> "+blockedContactsString);
                        PopulateBlockListContactsMap(blockedContactsString);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //getting DB values at start without any event occur
            mDatabase.child("Users").child(userID).child(Imei).child("watchedContacts").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(!("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                    {
                        String watchedContactsString = dataSnapshot.getValue().toString();
                        Log.d(TAG,"\nwatchedContactsString=> "+watchedContactsString);
                        PopulateWatchListContactsMap(watchedContactsString);
                    }
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


        mDatabase.child("Users").child(userID).child(Imei).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.getKey().equalsIgnoreCase("trigger")) {

                    if (dataSnapshot.getValue().toString().startsWith(Imei + "/calls")) {
                        Intent intent = new Intent(getApplicationContext(), getCalls.class);
                        startService(intent);
                    }
                    if (dataSnapshot.getValue().toString().startsWith(Imei + "/message")) {
                        Intent intent = new Intent(getApplicationContext(), getMessages.class);
                        startService(intent);
                    }
                    if (dataSnapshot.getValue().toString().startsWith(Imei + "/contacts")) {
                        Intent intent = new Intent(getApplicationContext(), getContacts.class);
                        startService(intent);
                    }
                    if (dataSnapshot.getValue().toString().startsWith(Imei + "/installedapps")) {
                        Intent intent = new Intent(getApplicationContext(), getInstalledApps.class);
                        startService(intent);
                    }
                    if(dataSnapshot.getValue().toString().startsWith(Imei + "/LockDevice")){

                        String[] passs = dataSnapshot.getValue().toString().split("/");
                        Intent intent = new Intent(getApplicationContext(), LockScreen.class);
                        intent.putExtra("pass",passs[passs.length -1]);
                        Log.d(TAG,passs[passs.length -1]+"");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mDatabase.child("Users").child(userID).child(Imei).child("trigger").setValue("NA");
                        mDatabase.child("Users").child(userID).child(Imei).child("trigger").setValue("DeviceLocked");
                        startActivity(intent);
                    }
                    if (dataSnapshot.getValue().toString().startsWith(Imei + "/photo")) {
                        Intent intent = new Intent(getApplicationContext(), getPhotosIntentService.class);
                        startService(intent);
                    }
                    if (dataSnapshot.getValue().toString().startsWith(Imei + "}proxmityalert")) {

                        String[] proxmityalertSplit = dataSnapshot.getValue().toString().split("\\}");

                        for (int i = 0; i < proxmityalertSplit.length; i++) {
                            Log.d(TAG,"proxmityalertSplit["+i+"]= "+proxmityalertSplit[i]);
                        }

                        String ProxmityAlertMessage = proxmityalertSplit[proxmityalertSplit.length -1];
                        Log.d(TAG,"ProxmityAlertMessage= "+ProxmityAlertMessage);

                        String parentDeviceTokenID= "";
                        parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                        if(!parentDeviceTokenID.equals(""))
                        {
                            // Sending Notification to Parent Device
                            PushNotificationHelper.SendPushNotification(getApplicationContext(),parentDeviceTokenID,Server_API_key,ContentType,ProxmityAlertMessage,"LAPCS Proximity Alert!");
                            Log.d(TAG,"Proximity Alert Message sent to parent");
                        }
                        else
                        {
                            Log.d(TAG,"Parent Device is Not Linked. Notification To Parent Sending Failed! ");
                            Toast.makeText(getApplicationContext(), "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (dataSnapshot.getValue().toString().startsWith(Imei + "/remove")) {
                        Intent intent = new Intent(getApplicationContext(), RemoveChildIntentService.class);
                        startService(intent);
                    }


                }

                //getting DB values at child changed means any number is blocked or watched or unblokcked or unwatched
                if (dataSnapshot.getKey().equalsIgnoreCase("blockedContacts")) {
                    String blockedContactsString = dataSnapshot.getValue().toString();
                    Log.d(TAG,"\nblockedContactsString=> "+blockedContactsString);
                    PopulateBlockListContactsMap(blockedContactsString);
                }

                if (dataSnapshot.getKey().equalsIgnoreCase("watchedContacts")) {
                    String watchedContactsString = dataSnapshot.getValue().toString();
                    Log.d(TAG,"\nwatchedContactsString=> "+watchedContactsString);
                    PopulateWatchListContactsMap(watchedContactsString);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }

    public void PopulateBlockListContactsMap(String blockedContactsString)
    {
        HashMap<String,String> TempMap=new HashMap<String,String>();

        if(blockedContactsString.startsWith("//LAPCS//"))   //Trimming "//LAPCS//" at start bec it causes empty string at 0 index after splitting
        {
            blockedContactsString = blockedContactsString.replaceFirst("//LAPCS//","");
        }

        String[] blockedContactsSplit = blockedContactsString.split("//LAPCS//");
        for (int i = 0; i < blockedContactsSplit.length; i++)
        {
            if(blockedContactsSplit[i].length()>0)
            {
                Log.d(TAG,"\nblockedContactsSplit[i]=> "+blockedContactsSplit[i]);
                String ContactName      = blockedContactsSplit[i].split("Number:")[0].trim().replace("Name:","").trim();
                String ContactNumber    = blockedContactsSplit[i].split("Number:")[1].trim();

                    //+92abc
                if(ContactNumber.startsWith("+"))
                {
                    ContactNumber = ContactNumber.substring(ContactNumber.length() - 10);
                    ContactNumber = "0"+ContactNumber;
                }

                Log.d(TAG,"\nBlocked  : ContactName=> "+ContactName+"  ContactNumber=> "+ContactNumber);

                TempMap.put(ContactName,ContactNumber);

            }
        }

        MapDifference<String, String> diff = Maps.difference(TempMap, BlockListContactsMap);
        Set<String> keysOnlyInTempMap = diff.entriesOnlyOnLeft().keySet();
        Set<String> keysOnlyInBlockListContactsMap = diff.entriesOnlyOnRight().keySet();

        Map SyncBlockListContactsMap= Collections.synchronizedMap(BlockListContactsMap);

        if(keysOnlyInBlockListContactsMap.size()>0) //means some contacts are unblocked on Web so we will remove it from our Block List
        {
            synchronized(SyncBlockListContactsMap){
                //Removing Keys efficiently all at once
                SyncBlockListContactsMap.keySet().removeAll(keysOnlyInBlockListContactsMap);
            }
        }

        if(keysOnlyInTempMap.size()>0) //means some new contacts are blocked on Web so we will add it in our Block List
        {
            synchronized(SyncBlockListContactsMap){
                for(String contactName : keysOnlyInTempMap){
                    SyncBlockListContactsMap.put(contactName,TempMap.get(contactName));
                }
            }
        }

        //saving in shared pref
        SaveHashMapToSharedPref("BlockListContactsMap",SyncBlockListContactsMap);
    }

    public void PopulateWatchListContactsMap(String watchedContactsString)
    {
        HashMap<String,String> TempMap=new HashMap<String,String>();
        if(watchedContactsString.startsWith("//LAPCS//"))   //Trimming "//LAPCS//" at start bec it causes empty string at 0 index after splitting
        {
            watchedContactsString = watchedContactsString.replaceFirst("//LAPCS//","");
        }
        String[] watchedContactsSplit = watchedContactsString.split("//LAPCS//");
        for (int i = 0; i < watchedContactsSplit.length; i++)
        {
            if(watchedContactsSplit[i].length()>0)
            {
                Log.d(TAG,"\nwatchedContactsSplit[i]=> "+watchedContactsSplit[i]);
                String ContactName      = watchedContactsSplit[i].split("Number:")[0].trim().replace("Name:","").trim();
                String ContactNumber    = watchedContactsSplit[i].split("Number:")[1].trim();

                if(ContactNumber.startsWith("+"))
                {
                    ContactNumber = ContactNumber.substring(ContactNumber.length() - 10);
                    ContactNumber = "0"+ContactNumber;
                }


                Log.d(TAG,"\nWatched  : ContactName=> "+ContactName+"  ContactNumber=> "+ContactNumber);

                TempMap.put(ContactName,ContactNumber);
            }
        }

        MapDifference<String, String> diff = Maps.difference(TempMap, WatchListContactsMap);
        Set<String> keysOnlyInTempMap = diff.entriesOnlyOnLeft().keySet();
        Set<String> keysOnlyInWatchListContactsMap = diff.entriesOnlyOnRight().keySet();

        Map SyncWatchListContactsMap= Collections.synchronizedMap(WatchListContactsMap);

        if(keysOnlyInWatchListContactsMap.size()>0) //means some contacts are unwatched on Web so we will remove it from our Watch List
        {
            synchronized(SyncWatchListContactsMap){
                //Removing Keys efficiently all at once
                SyncWatchListContactsMap.keySet().removeAll(keysOnlyInWatchListContactsMap);
            }
        }

        if(keysOnlyInTempMap.size()>0) //means some new contacts are watched on Web so we will add it in our Watch List
        {
            synchronized(SyncWatchListContactsMap){
                for(String contactName : keysOnlyInTempMap){
                    SyncWatchListContactsMap.put(contactName,TempMap.get(contactName));
                }
            }
        }

        //saving in shared pref
        SaveHashMapToSharedPref("WatchListContactsMap",SyncWatchListContactsMap);
    }


    public void SaveHashMapToSharedPref(String key, Map<String,String> inputMap)
    {
        sharedPreferences = getApplicationContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        if (sharedPreferences != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            editor=sharedPreferences.edit();
            editor.remove(key).commit();
            editor.putString(key, jsonString);
            editor.commit();
        }
    }

    public Map<String,String> LoadHashMapFromSharedPref(String key)
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


}
