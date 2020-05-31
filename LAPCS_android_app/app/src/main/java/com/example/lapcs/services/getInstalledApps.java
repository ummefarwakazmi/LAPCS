package com.example.lapcs.services;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class getInstalledApps extends IntentService {
    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public getInstalledApps() {
        super("getInstalledApps");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");
        String data = getInstalledApps();
        mDatabase.child("Users").child(userID).child(Imei).child("getApps").setValue("NA");
        mDatabase.child("Users").child(userID).child(Imei).child("getApps").setValue("//LAPCS//requestApps//"+data);

    }
    private String getInstalledApps (){
        String apps="";
        List<PackageInfo> packagelist = getPackageManager().getInstalledPackages(0);
        for(int i=0; i< packagelist.size(); i++){
            PackageInfo packageInfo=packagelist.get(i);
            if ( (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                String appName= packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                Date callDayTime = new Date(Long.valueOf(packageInfo.firstInstallTime));
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String dateTime=outputFormat.format(callDayTime);
                apps = apps + appName +  " InstalledDate: " + dateTime + "//";
                //Log.d("installedapps",appName +": " +dateTime);
            }
        }
        return apps;
    }

}

