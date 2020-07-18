package com.example.lapcs.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.view.Window;

import com.example.lapcs.R;
import com.example.lapcs.Receivers.ControllerDeviceAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    DevicePolicyManager devicePolicyManager;
    ComponentName componentName;
    SharedPreferences sharedPreferences;
    TelephonyManager telephonyManager;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    String userEmail, userID;
    PowerManager powerManager;
    boolean isOptimize = true;
    NotificationManager mNotificationManager;
    boolean isDoNotDisturbOn = true;
    DatabaseReference mDatabase;
    boolean adminActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorIndigo));
        }
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        progressDialog.setMessage("Please Wait..");
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("Email", "");
        userID = sharedPreferences.getString("UserID", "");
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(MainActivity.this, ControllerDeviceAdmin.class);
        adminActive = devicePolicyManager.isAdminActive(componentName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            isDoNotDisturbOn=mNotificationManager.isNotificationPolicyAccessGranted();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            isOptimize = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || !isOptimize
                || !isDoNotDisturbOn ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, GrantPermissionsActivity.class);
            startActivityForResult(intent, 100);
        } else if (!adminActive) {
            Intent intent = new Intent(this, DeviceAdminActivity.class);
            startActivityForResult(intent, 101);
        } else if (sharedPreferences.getString("Mode", "").equalsIgnoreCase("")) {
            Intent i = new Intent(this, SwitchUserActivity.class);
            startActivity(i);
        } else if (userEmail.equalsIgnoreCase("")) {
            Intent i = new Intent(this, SwitchUserActivity.class);
            startActivity(i);
        }
        else if (sharedPreferences.getString("LoginActivityVisited", "").equalsIgnoreCase("")) {
            Intent i = new Intent(this, LoginParentActivity.class);
            startActivity(i);
        }
        else if (sharedPreferences.getString("Mode", "").equalsIgnoreCase("ParentActivity")) {
            //Intent i = new Intent(this, ParentActivity.class);
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
        } else if (sharedPreferences.getString("Mode", "").equalsIgnoreCase("ChildActivity")) {
            //Intent i = new Intent(this, ChildActivity.class);
            Intent i = new Intent(this, PublicHomeActivity.class);
            startActivity(i);
        } else {
            Intent i = new Intent(this, SwitchUserActivity.class);
            startActivity(i);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == 101) {
                adminActive = devicePolicyManager.isAdminActive(componentName);
                if (!adminActive) {
                    Intent intent = new Intent(this, DeviceAdminActivity.class);
                    startActivityForResult(intent, 101);
                }else if (userEmail.equalsIgnoreCase("")) {
                    Intent i = new Intent(this, SwitchUserActivity.class);
                    startActivity(i);
                } else if (sharedPreferences.getString("Mode", "").equalsIgnoreCase("ParentActivity")) {
                    //Intent i = new Intent(this, ParentActivity.class);
                    Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(i);
                } else if (sharedPreferences.getString("Mode", "").equalsIgnoreCase("ChildActivity")) {
                    //Intent i = new Intent(this, ChildActivity.class);
                    Intent i = new Intent(this, PublicHomeActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(this, SwitchUserActivity.class);
                    startActivity(i);
                }
            }
            if (resultCode == 102) {
                finish();
            }
        }

        if (requestCode == 101) {
            if (resultCode == 102) {
                finish();
            }
            if (resultCode == 103) {
                if (userEmail.equalsIgnoreCase(""))
                {
                    Intent i = new Intent(this, SwitchUserActivity.class);
                    startActivity(i);
                }
            }
        }
        if (requestCode == 102) {
            if (resultCode == 101) {
                userEmail = sharedPreferences.getString("Email", "");
                if (userEmail.equalsIgnoreCase(""))
                {
                    Intent i = new Intent(this, SignUPActivity.class);
                    startActivity(i);
                }
            }
        }
    }
}
