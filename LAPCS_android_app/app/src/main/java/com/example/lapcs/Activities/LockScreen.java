package com.example.lapcs.Activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.app.admin.DevicePolicyManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.Helpers.SmsHelper;
import com.example.lapcs.R;
import com.example.lapcs.Receivers.ControllerDeviceAdmin;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;

public class LockScreen extends Activity {
    EditText password;
    TextView tf;
    Button unlock;
    ImageButton imageButtonSendMsg;
    ImageButton imageButtonSendAlert;
    DevicePolicyManager devicePolicyManager;
    ComponentName componentName;
    boolean active;
    String pass;
    boolean close=false;

    SharedPreferences sharedPreferences;
    String parentDeviceTokenID= "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_lock_screen);
        password=(EditText)findViewById(R.id.password);
        unlock=(Button) findViewById(R.id.unlock);
        tf=(TextView) findViewById(R.id.textView4);
        devicePolicyManager=(DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName=new ComponentName(this, ControllerDeviceAdmin.class);
        active=devicePolicyManager.isAdminActive(componentName);
        Intent intent=getIntent();
        pass=intent.getStringExtra("pass");
        Log.d(AppConsts.TAG,pass);
        devicePolicyManager.lockNow();
        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (password.getText().toString().equalsIgnoreCase(""))
                    tf.setText("Enter Valid Password");
                else if (password.getText().toString().equals(pass))
                {
                    if (active)
                    {
                        close=true;
                        finish();
                        Toast.makeText(getApplicationContext(),"Device Unlocked",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    tf.setText("Password Incorrect");
            }
        });

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);


        imageButtonSendMsg = findViewById(R.id.notification_icon_iv);
        imageButtonSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AppConsts.TAG,"Pressed SEND_MESSAGE_ACTION");

                String EmergencyNumber  = sharedPreferences.getString("EmergencyNumber", "");

                if (!EmergencyNumber.equalsIgnoreCase("")) {

                    if (!SmsHelper.hasValidPreConditions(getApplicationContext(),EmergencyNumber)) return;

                    SmsHelper.sendDebugSms(String.valueOf(EmergencyNumber), "SOS Message From Your ChildActivity! He is in Danger.");
                    Toast.makeText(getApplicationContext(), R.string.toast_sending_sms, Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageButtonSendAlert = findViewById(R.id.send_notification_btn_ib);
        imageButtonSendAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AppConsts.TAG,"Pressed SEND_ALERT_ACTION");
                Toast.makeText(getApplicationContext(),"Sending Panic Alert...", Toast.LENGTH_SHORT).show();

                parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");

                if(!parentDeviceTokenID.equals(""))
                {
                    PushNotificationHelper.SendPushNotification(getApplicationContext(), parentDeviceTokenID,Server_API_key,ContentType,"Your Child is in Danger","LAPCS Alert!");
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Parent Device is Not Linked. Notification To Parent Sending Failed! ", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
        if(!close)
        {
            if (!hasFocus) {
                Intent i = new Intent(getBaseContext(), LockScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                i.putExtra("pass",pass);
                startActivity(i);
                this.finish();
                devicePolicyManager.lockNow();
            }
        }
        if(close)
            finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
