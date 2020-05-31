package com.example.lapcs.Activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.InitApp.App;
import com.example.lapcs.R;
import com.example.lapcs.Utils.ServiceUtils;
import com.example.lapcs.models.ChildMetaData;
import com.example.lapcs.services.LocationService;
import com.example.lapcs.services.LockScreenSOS_Service;
import com.example.lapcs.services.SendMessageFromLockScreenService;
import com.example.lapcs.services.TriggerEvents;
import com.example.lapcs.services.WatchListService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.INITIAL_DELAY_IN_SECONDS;
import static com.example.lapcs.AppConsts.NOTIFY_INTERVAL;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;

public class PublicHomeActivity extends AppCompatActivity {

    String userID="";
    String parentDeviceTokenID= "";
    //Button BtnNext;
    Button BtnSOSMessage;
    Button BtnParentAccess;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabaseChildMetaData;

    private final int REQUEST_READ_PHONE_STATE=1990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setContentView(R.layout.activity_public_home);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
        }

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabaseChildMetaData = mFirebaseInstance.getReference(AppConsts.Child_Meta_Data_Node_Ref);
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(AppConsts.SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID);

        Intent i = new Intent(this, TriggerEvents.class);
        startService(i);

        PushNotificationHelper.getParentDeviceToken(PublicHomeActivity.this,AppConsts.Parent_Meta_Data_Node_Ref);
        PairDevice();
        startLocationService();
        runWatchListService();
        runLockScreenSOS_Service();
        runSendMessageFromLockScreenService();


        BtnSOSMessage = findViewById(R.id.btn_send_sos_message);

        BtnSOSMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicHomeActivity.this, SOSMessageActivity.class);
                startActivity(intent);
            }

        });

        BtnParentAccess = findViewById(R.id.btn_parent_access);

        BtnParentAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PublicHomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    public void runWatchListService()
    {
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {
            if(!ServiceUtils.isServiceRunning(PublicHomeActivity.this,WatchListService.class))
            {
                startService(new Intent(this, WatchListService.class));
                Log.d(AppConsts.TAG,"PublicHomeActivity:    WatchListService is Started !");
            }
            else
            {
                Log.d(AppConsts.TAG,"PublicHomeActivity:    WatchListService is Already Running !");
            }

        }
        else
        {
            Log.d(TAG,"Imei and userID is empty !!");
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startLocationService() {

        if(!(ServiceUtils.isServiceRunning(PublicHomeActivity.this, LocationService.class)))
        {
            startService(new Intent(this, LocationService.class));
            Toast.makeText(this, "GPS Tracking Service Started", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "GPS Tracking Service Already Running", Toast.LENGTH_SHORT).show();
        }

    }

    public void PairDevice()
    {
        String Imei = sharedPreferences.getString("Imei", "");

        Toast.makeText(PublicHomeActivity.this,"Saving Child MetaData . . .!", Toast.LENGTH_SHORT).show();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (task.isSuccessful()) {
                            try {

                                String NewDeviceToken= task.getResult().getToken();

                                Log.d(AppConsts.TAG, "onComplete: Token: " + NewDeviceToken);
                                Toast.makeText(PublicHomeActivity.this,"Token Generated: " + NewDeviceToken, Toast.LENGTH_LONG).show();

                                try
                                {
                                    userID = sharedPreferences.getString("UserID", "");

                                    String LoggedInParentID = userID;
                                    Log.d(AppConsts.TAG, "LoggedInParentID: " + LoggedInParentID);

                                    ChildMetaData childMetaData = new ChildMetaData(NewDeviceToken);

                                    App.DeviceToken = sharedPreferences.getString("DeviceToken", "");

                                    DatabaseReference mDatabase;
                                    mDatabase = FirebaseDatabase.getInstance().getReference();

                                    mDatabase.child(AppConsts.Child_Meta_Data_Node_Ref).child(userID).child(Imei).child("tokenId").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.getValue() != null &&  !("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                            {
                                                Toast.makeText(PublicHomeActivity.this, "Child Metadata Already Saved", Toast.LENGTH_LONG).show();
                                                Log.d(TAG,this.getClass().getName()+": "+"Child Metadata Already Saved");
                                                Log.d(TAG, this.getClass().getName()+"Child Metadata Already Saved: Token Value: "+  dataSnapshot.getValue().toString());

                                                if(! dataSnapshot.getValue().toString().equals(NewDeviceToken))     //if child already exists then just update the Token
                                                {
                                                    App.DeviceToken = NewDeviceToken;
                                                    mFirebaseDatabaseChildMetaData.child(LoggedInParentID).child(Imei).setValue(childMetaData);
                                                    editor.putString("DeviceToken", NewDeviceToken);
                                                    editor.commit();
                                                }
                                                else
                                                {
                                                    App.DeviceToken = dataSnapshot.getValue().toString();
                                                    editor.putString("DeviceToken", dataSnapshot.getValue().toString());
                                                    editor.commit();
                                                }

                                            }
                                            else
                                            {

                                                App.DeviceToken = NewDeviceToken;
                                                Log.d(AppConsts.TAG, this.getClass().getName()+": "+"onComplete: Token: " + App.DeviceToken);
                                                Toast.makeText(PublicHomeActivity.this,"Child Token Generated: " + App.DeviceToken,Toast.LENGTH_LONG).show();

                                                editor.putString("DeviceToken", App.DeviceToken);
                                                editor.commit();
                                                mFirebaseDatabaseChildMetaData.child(LoggedInParentID).child(Imei).setValue(childMetaData);


                                                //displaying a success toast
                                                Toast.makeText(PublicHomeActivity.this, "Child MetaData Saved Successfully!", Toast.LENGTH_LONG).show();

                                                // Sending Notification to Child Device
                                                PushNotificationHelper.SendPushNotification(PublicHomeActivity.this,App.DeviceToken,Server_API_key,ContentType,"Child MetaData Saved Successfully!","LAPCS Alert!");
                                                //Getting Parent Device Token

                                                parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                                                if(!parentDeviceTokenID.equals(""))
                                                {
                                                    // Sending Notification to Parent Device
                                                    PushNotificationHelper.SendPushNotification(PublicHomeActivity.this,parentDeviceTokenID,Server_API_key,ContentType,"Child MetaData Saved Successfully!","LAPCS Alert!");
                                                }
                                                else
                                                {
                                                    Toast.makeText(PublicHomeActivity.this, "Parent Metadata not Saved. Notification To Parent Sending Failed! ", Toast.LENGTH_LONG).show();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            System.out.println("The read failed: " + databaseError.getMessage());
                                            Toast.makeText(PublicHomeActivity.this, "The read failed: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.d(TAG,this.getClass().getName()+": "+"The read failed: " + databaseError.getMessage());
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(PublicHomeActivity.this,"Child Metadata Exception: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(PublicHomeActivity.this,"Token generation Exception", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(PublicHomeActivity.this,"Token generation failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    public void runLockScreenSOS_Service()
    {
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {
            if(!ServiceUtils.isServiceRunning(PublicHomeActivity.this,LockScreenSOS_Service.class))
            {
                startService(new Intent(this, LockScreenSOS_Service.class));
                Log.d(AppConsts.TAG,"PublicHomeActivity:    LockScreenSOS_Service is Started !");
                Toast.makeText(getApplicationContext(),"LockScreenSOS_Service Started",Toast.LENGTH_SHORT);
            }
            else
            {
                Log.d(AppConsts.TAG,"PublicHomeActivity:    LockScreenSOS_Service is Already Running !");
                Toast.makeText(getApplicationContext(),"LockScreenSOS_Service is Already Running !",Toast.LENGTH_SHORT);
            }

        }
        else
        {
            Log.d(TAG,"Imei and userID is empty !!");
        }

    }

    public void runSendMessageFromLockScreenService()
    {
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");

        if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
        )
        {
            if(!ServiceUtils.isServiceRunning(PublicHomeActivity.this, SendMessageFromLockScreenService.class))
            {
                startService(new Intent(this, SendMessageFromLockScreenService.class));
                Log.d(AppConsts.TAG,"PublicHomeActivity:    SendMessageFromLockScreenService is Started !");
                Toast.makeText(getApplicationContext(),"SendMessageFromLockScreenService Started",Toast.LENGTH_SHORT);
            }
            else
            {
                Log.d(AppConsts.TAG,"PublicHomeActivity:    SendMessageFromLockScreenService is Already Running !");
                Toast.makeText(getApplicationContext(),"SendMessageFromLockScreenService is Already Running !",Toast.LENGTH_SHORT);
            }

        }
        else
        {
            Log.d(TAG,"Imei and userID is empty !!");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //TODO
                }
                break;

            default:
                break;
        }
    }

}
