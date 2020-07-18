package com.example.lapcs.Activities;


import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.lapcs.AppConsts;
import com.example.lapcs.R;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
public class GrantPermissionsActivity extends Activity implements View.OnClickListener{
    Button askPermission,batteryPermission,soundPermission;
    PowerManager powerManager;
    NotificationManager mNotificationManager;
    boolean isOptimize = true;
    boolean isDoNotDisturbOn = true;
    ArrayList<String> permissions=new ArrayList<String>();
    Drawable btn_bg_perm_disabled_selector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorIndigo));
        }
        setContentView(R.layout.activity_grant_permissions);
        btn_bg_perm_disabled_selector = getResources().getDrawable(R.drawable.btn_bg_perm_disabled);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        askPermission=(Button)findViewById(R.id.PermissionButton);
        batteryPermission=(Button)findViewById(R.id.PermissionBattery);
        batteryPermission.setOnClickListener(this);
        soundPermission=(Button)findViewById(R.id.PermissionSound);
        soundPermission.setOnClickListener(this);
        askPermission.setOnClickListener(this);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.READ_CALL_LOG);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissions.add(Manifest.permission.SEND_SMS);
        setButtons();

        try {
            if (!isAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }catch (Exception ex)
        {
            Log.d(AppConsts.TAG,"USAGE ACCESS SETTINGS: API 21 Required");
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            isOptimize = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        if (!isOptimize) {
            batteryPermission.setEnabled(true);
        }
        else
        {
            batteryPermission.setEnabled(false);
            batteryPermission.setBackground(btn_bg_perm_disabled_selector);
            batteryPermission.setText("PERMITTED");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isDoNotDisturbOn=mNotificationManager.isNotificationPolicyAccessGranted();
        }
        if (!isDoNotDisturbOn) {
            soundPermission.setEnabled(true);
        }
        else
        {
            soundPermission.setEnabled(false);
            soundPermission.setBackground(btn_bg_perm_disabled_selector);
            soundPermission.setText("PERMITTED");
        }
    }

    void setButtons()
    {
        if(checkAllPermissionsGranted())
        {
            askPermission.setEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            isOptimize = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        if(isOptimize)
        {
            batteryPermission.setEnabled(false);
            batteryPermission.setBackground(btn_bg_perm_disabled_selector);
            batteryPermission.setText("PERMITTED");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isDoNotDisturbOn=mNotificationManager.isNotificationPolicyAccessGranted();
        }
        if(isDoNotDisturbOn)
        {
            soundPermission.setEnabled(false);
            soundPermission.setBackground(btn_bg_perm_disabled_selector);
            soundPermission.setText("PERMITTED");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100)
        {
            for(int i=0;i<grantResults.length;i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getBaseContext(), "This application will not work properly without these permissions. If you check 'Don't ask again', you may have to Uninstall Application", Toast.LENGTH_SHORT).show();
            }
            if (checkAllPermissionsGranted())
            {
                askPermission.setEnabled(false);
                askPermission.setBackground(btn_bg_perm_disabled_selector);
                askPermission.setText("PERMITTED");
                finishCheck();
            }
        }
    }

    void finishCheck()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            isOptimize = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isDoNotDisturbOn=mNotificationManager.isNotificationPolicyAccessGranted();
        }
        if (checkAllPermissionsGranted() && isOptimize && isDoNotDisturbOn)
        {
            setResult(101);
            finish();
        }
    }

    boolean checkAllPermissionsGranted()
    {
        for (String permission : permissions)
        {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return  false;
        }
        return true;
    }
    @Override
    public void onClick(View view)
    {
        if(view==askPermission)
        {
            ArrayList<String> remainingPermissions=new ArrayList<String>();
            for (String permission : permissions)
            {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    remainingPermissions.add(permission);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, remainingPermissions.toArray(new String[remainingPermissions.size()]), 100);
            }
        }
        if (view==batteryPermission)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                isOptimize = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            if(!isOptimize)
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    Toast.makeText(this, "Go to  App Managment --> select app --> Power Saver --> Choose \"Allow BackGround Running\"", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Go to All Apps and set LAPCS to 'Don't Optimize'", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            }
            else
            {
                batteryPermission.setEnabled(false);
                batteryPermission.setBackground(btn_bg_perm_disabled_selector);
                batteryPermission.setText("PERMITTED");
            }
        }
        if (view==soundPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                isDoNotDisturbOn = mNotificationManager.isNotificationPolicyAccessGranted();
            if (!isDoNotDisturbOn) {
                Toast.makeText(this, "Enable LAPCS", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
            else
            {
                soundPermission.setEnabled(false);
                soundPermission.setBackground(btn_bg_perm_disabled_selector);
                soundPermission.setText("PERMITTED");
            }
        }
    }

    @Override
    protected void onResume() {
        finishCheck();
        setButtons();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(102);
            finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
