package com.example.lapcs.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.Helpers.SmsHelper;
import com.example.lapcs.InitApp.App;
import com.example.lapcs.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;

public class SOSMessageActivity extends AppCompatActivity {

    private Button BtnSendPanicAlert;
    private Button BtnSendSOSMessage;

    private static final String PREF_USER_MOBILE_PHONE = "pref_user_mobile_phone";


    private String mUserMobilePhone;

    private EditText mNumberEditText;
    //private SharedPreferences mSharedPreferences;

    private FirebaseDatabase mFirebaseInstance;
    //private DatabaseReference mFirebaseDatabaseLinkedUers;

    SharedPreferences sharedPreferences;

    String parentDeviceTokenID= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sosmessage);

        if (!SmsHelper.hasReadSmsPermission(SOSMessageActivity.this)) {
            SmsHelper.showRequestPermissionsInfoAlertDialog(SOSMessageActivity.this);
        }

        mFirebaseInstance = FirebaseDatabase.getInstance();

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        BtnSendPanicAlert = (Button) findViewById(R.id.btn_send_panic_alert);

        App.DeviceToken = sharedPreferences.getString("DeviceToken", "NIL");
        if(App.DeviceToken.equals("NIL"))
        {
            BtnSendPanicAlert.setEnabled(false);
        }

        BtnSendPanicAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SOSMessageActivity.this,"Send Panic Alert Button Pressed", Toast.LENGTH_SHORT).show();


                parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                if(!parentDeviceTokenID.equals(""))
                {
                    PushNotificationHelper.SendPushNotification(SOSMessageActivity.this, parentDeviceTokenID,Server_API_key,ContentType,"Your Child is in Danger","LAPCS Alert!");
                }
                else
                {
                    Toast.makeText(SOSMessageActivity.this, "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_LONG).show();
                }

            }
        });

        mNumberEditText = (EditText) findViewById(R.id.et_number);
        mUserMobilePhone  = sharedPreferences.getString("EmergencyNumber", "");

        if (!TextUtils.isEmpty(mUserMobilePhone)) {
            mNumberEditText.setText(mUserMobilePhone);
            mNumberEditText.setEnabled(false);
        }

        BtnSendSOSMessage = (Button) findViewById(R.id.btn_send_sos_message);

        BtnSendSOSMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SmsHelper.hasValidPreConditions(SOSMessageActivity.this,mNumberEditText.getText().toString())) return;

                SmsHelper.sendDebugSms(String.valueOf(mNumberEditText.getText()), "SOS Message From Your ChildActivity! He is in Danger.");
                Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();

            }
        });

    }

}
