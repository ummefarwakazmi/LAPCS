package com.example.lapcs.Activities;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.lapcs.R;
import com.example.lapcs.Receivers.ControllerDeviceAdmin;

public class DeviceAdminActivity extends Activity implements View.OnClickListener{
    Button grantAdmin;
    DevicePolicyManager devicePolicyManager;
    ComponentName componentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setContentView(R.layout.activity_device_admin);
        devicePolicyManager=(DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName=new ComponentName(getApplicationContext(), ControllerDeviceAdmin.class);
        grantAdmin=(Button)findViewById(R.id.runAsAdmin);
        grantAdmin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(devicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(devicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
        intent.putExtra(devicePolicyManager.EXTRA_ADD_EXPLANATION,"Add this application to admin");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (devicePolicyManager.isAdminActive(componentName))
        {
            setResult(103);
            finish();
        }
        else
            Toast.makeText(getApplicationContext(),"LAPCS will not work properly until you grant this permission",Toast.LENGTH_LONG).show();
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
}
