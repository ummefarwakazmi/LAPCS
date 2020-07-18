package com.example.lapcs.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.lapcs.Helpers.AppUsageStatsHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class getAppUsageStatsIntentService extends IntentService {

    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public getAppUsageStatsIntentService() {
        super("getAppUsageStatsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        AppUsageStatsHelper appUsageStats = new AppUsageStatsHelper(getApplicationContext());
        String data = appUsageStats.prepareAppUsageStatsStringForDB();

        mDatabase.child("Users").child(userID).child(Imei).child("getAppUsageStats").setValue("NA");
        mDatabase.child("Users").child(userID).child(Imei).child("getAppUsageStats").setValue("//LAPCS//requestAppUsageStats//"+data);

    }

}
