package com.example.lapcs.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapcs.AppConsts;
import com.example.lapcs.R;
import com.example.lapcs.Utils.ServiceUtils;
import com.example.lapcs.models.Child;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.lapcs.AppConsts.TAG;

public class ReassignDeviceActivity extends AppCompatActivity {

    private Button m_BtnLogout;
    private Button mReAssignDevice;
    TextView mDeviceName;
    EditText editTextReassign;

    //defining firebaseauth object
    private FirebaseAuth mAuth;
    private DatabaseReference mFirebaseDatabaseMobiles;
    private FirebaseDatabase mFirebaseInstance;

    SharedPreferences sharedPreferences;

    String deviceName="";
    String PhoneIMEI="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorIndigo));
        }
        setContentView(R.layout.activity_reassign_device);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        mDeviceName = (TextView) findViewById(R.id.tv_device_name);
        mDeviceName.setGravity(Gravity.LEFT );
        PhoneIMEI = sharedPreferences.getString("Imei", "");

        //editTextReassign
        editTextReassign = (EditText) findViewById(R.id.editTextReassign);

        mFirebaseDatabaseMobiles = mFirebaseInstance.getReference(AppConsts.Mobiles_Node_Ref+"/"+PhoneIMEI);

        mFirebaseDatabaseMobiles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null)
                {
                    Log.d(TAG, this.getClass().getName()+": "+" MobilesSnapshot Key= "+ dataSnapshot.getKey());
                    Log.d(TAG, this.getClass().getName()+": "+" MobilesSnapshot Value= "+ dataSnapshot.getValue().toString());

                    deviceName = dataSnapshot.getValue().toString();
                    mDeviceName.setText(deviceName);
                    editTextReassign.setText(deviceName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mReAssignDevice = (Button) findViewById(R.id.btn_reassign_device);
        mReAssignDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(     !("".equals(PhoneIMEI.toString())) && !(TextUtils.isEmpty(PhoneIMEI.toString()))  &&
                        !("".equals(deviceName.toString())) && !(TextUtils.isEmpty(deviceName.toString()))
                )
                {
                    String newName = editTextReassign.getText().toString();
                    if (!deviceName.equals(newName))
                    {
                        ServiceUtils.updateMobileDataInFireBase(ReassignDeviceActivity.this, PhoneIMEI, newName);
                        mDeviceName.setText( "Device Name: " + newName);
                        editTextReassign.setText("");
                    }
                    else
                    {
                        //Toast.makeText(ReassignDeviceActivity.this, "Device Name not Changed! ", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                   // Toast.makeText(ReassignDeviceActivity.this, "Device Name or Device IMEI is Empty! ", Toast.LENGTH_SHORT).show();
                }
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
        Intent intent = new Intent(ReassignDeviceActivity.this, PublicHomeActivity.class);
        startActivity(intent);

    }

}
