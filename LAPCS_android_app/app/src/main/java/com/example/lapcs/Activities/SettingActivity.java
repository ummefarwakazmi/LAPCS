package com.example.lapcs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lapcs.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private Button BtnUpdateProfile;
    private Button BtnReAssignDevice;
    private Button m_BtnSetEmergencyNumber;
    private Button m_BtnLogout;

    //defining firebaseauth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();

        BtnUpdateProfile = (Button) findViewById(R.id.btn_update_password);

        BtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SettingActivity.this,"Update Password Button Pressed!" ,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SettingActivity.this,UpdatePasswordActivity.class);
                startActivity(intent);
            }
        });

        BtnReAssignDevice = (Button) findViewById(R.id.btn_reassign_device);
        BtnReAssignDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,ReassignDeviceActivity.class);
                startActivity(intent);
            }
        });

        m_BtnSetEmergencyNumber = (Button) findViewById(R.id.btn_set_emergency_num);
        m_BtnSetEmergencyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this,SaveEmergencyNumberActivity.class);
                startActivity(intent);
            }
        });

        m_BtnLogout = (Button) findViewById(R.id.BtnLogout);
        m_BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

    }


    private void logout() {

        //logging out the user
        mAuth.signOut();
        //closing activity
        finish();
        //starting login activity
        Intent intent = new Intent(SettingActivity.this, PublicHomeActivity.class);
        startActivity(intent);

    }



}
