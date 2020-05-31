package com.example.lapcs.storage;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public static synchronized SharedPrefManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(mCtx);
        }
        return mInstance;
    }

    public static void clearSharedPreferences(Context mCtx)
    {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        sharedPreferences = mCtx.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //////////////////////////////////////////////////
        editor.putString("LoginActivityVisited","");
        editor.putString("Email","");
        editor.putString("Mode","");
        editor.putString("userAddedWithMobile","");
        editor.putString("ParentDeviceToken","");
        editor.putString("Imei","");
        editor.putString("PIN","");
        editor.putString("UserID","");
        editor.putString("DeviceToken","");
        editor.putString("SmsCommands","");
        editor.putString("IntroSlider5ActivityVisited","");
        editor.putString("userAdded","");
        editor.putString("mobileAdded","");
        editor.putString("BlockListContactsMap","");
        editor.putString("WatchListContactsMap","");
        editor.commit();
        //////////////////////////////////////////////////

        editor.putString("IntroSlider5ActivityVisited", "Y");
        editor.commit();

    }

}
