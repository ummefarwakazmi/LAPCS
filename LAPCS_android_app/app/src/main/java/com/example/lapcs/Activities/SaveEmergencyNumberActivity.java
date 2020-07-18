package com.example.lapcs.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lapcs.R;
import com.google.firebase.auth.FirebaseAuth;

public class SaveEmergencyNumberActivity extends AppCompatActivity {

    private Button m_BtnLogout;
    private Button mUpdateNumber;
    TextView mParentPhoneNumber;
    private EditText mNumberEditText;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //defining firebaseauth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorIndigo));
        }
        setContentView(R.layout.activity_set_emergency_number);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mNumberEditText = (EditText) findViewById(R.id.et_number);
        mParentPhoneNumber = (TextView) findViewById(R.id.tv_current_phone_number);
        String EmergencyNumber  = sharedPreferences.getString("EmergencyNumber", "");

        if (EmergencyNumber.equalsIgnoreCase("")) {
            mNumberEditText.setText("Number Not Set");
        }
        else
        {
            mNumberEditText.setText(EmergencyNumber);
            mParentPhoneNumber.setText(EmergencyNumber);
        }

        mUpdateNumber = (Button) findViewById(R.id.btn_save_number);
        mUpdateNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String EmergencyNumber  = mNumberEditText.getText().toString();
                editor.putString("EmergencyNumber", EmergencyNumber);
                editor.commit();
                mParentPhoneNumber.setText(EmergencyNumber);
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
        Intent intent = new Intent(SaveEmergencyNumberActivity.this, PublicHomeActivity.class);
        startActivity(intent);

    }


}
