package com.example.lapcs.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.models.Mobile;
import com.example.lapcs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.lapcs.AppConsts.DEFAULT_EMERGENCY_NUMBER;
import static com.example.lapcs.AppConsts.TAG;

public class LoginActivity extends Activity {
    EditText editTextEmail,editTextPassword;
    TextView m_tv_forgot_password;
    //TextView goToSignUp;
    FirebaseUser firebaseUser;
    Button login;
    TelephonyManager telephonyManager;
    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorIndigo));
        }

        setContentView(R.layout.activity_login);

        firebaseAuth=FirebaseAuth.getInstance();

        //if getCurrentUser does not return null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();
            //and open profile activity
            startActivity(new Intent(getApplicationContext(), SettingActivity.class));
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editTextEmail=(EditText)findViewById(R.id.getEmail);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        editTextPassword=(EditText)findViewById(R.id.getPass);
        login=(Button) findViewById(R.id.Login);

//        goToSignUp=(TextView) findViewById(R.id.goToRegister);
//        goToSignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(getBaseContext(), SignUPActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Enter a valid email");
                    editTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("Password required");
                    editTextPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    editTextPassword.setError("Password should be atleast 6 character long");
                    editTextPassword.requestFocus();
                    return;
                }
                else
                {
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {

                                        final boolean[] isComingFromSwitchUserActivity = {false};

                                        String PhoneIMEI = sharedPreferences.getString("Imei", "");
                                        String userID = sharedPreferences.getString("UserID", "");

                                        if(     ("".equals(PhoneIMEI.toString())) && (TextUtils.isEmpty(PhoneIMEI.toString()))  &&
                                                ("".equals(userID.toString())) && (TextUtils.isEmpty(userID.toString()))
                                        )
                                        {
                                            isComingFromSwitchUserActivity[0] = true;
                                            Log.d(TAG,this.getClass().getName()+": "+"Imei and userID is empty !!");
                                        }

                                        firebaseUser=firebaseAuth.getCurrentUser();
                                        editor.putString("Email", email);
                                        editor.putString("UserID", firebaseUser.getUid());
                                        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                            return;
                                        }
                                        final String Imei=telephonyManager.getDeviceId();
                                        editor.putString("Imei", Imei);
                                        editor.putString("PIN","NA");
                                        editor.putString("SmsCommands","false");
                                        Mobile newMobile = new Mobile(Build.BRAND, Build.MODEL);
                                        mDatabase.child("Mobiles").child(Imei).setValue(newMobile.model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                                    Toast.makeText(LoginActivity.this,user.getEmail()+" Loggedin Successfully" ,Toast.LENGTH_SHORT).show();

                                                    editor.putString("mobileAdded", "Y");
                                                    editor.putString("userAddedWithMobile", "Y");



                                                    if(isComingFromSwitchUserActivity[0] == true)
                                                    {

                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getCalls").setValue("NA");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getMessages").setValue("NA");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getContacts").setValue("NA");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getPhotos").setValue("NA");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getCurrentPhoto").setValue("NA");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("blockedContacts").setValue("");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("watchedContacts").setValue("");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getLocations").setValue("NA");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("watchListDetails").setValue("");
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getApps").setValue("NA");


                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("trigger").setValue("NA");

                                                        editor.putString("EmergencyNumber", DEFAULT_EMERGENCY_NUMBER);


                                                        editor.putString("userAdded", "Y");
                                                        Log.d(TAG,this.getClass().getName()+":Before Commit: "+"IMEI: "+Imei+"userID: "+userID);


                                                        editor.putString("LoginActivityVisited", "Y");
                                                        editor.commit();

                                                        String userID = sharedPreferences.getString("UserID", "");
                                                        Log.d(TAG,this.getClass().getName()+"After Commit: "+"IMEI: "+Imei+"userID: "+userID);

                                                        PushNotificationHelper.getParentDeviceToken(LoginActivity.this,AppConsts.Parent_Meta_Data_Node_Ref);
                                                        //logging out the user
                                                        firebaseAuth.signOut();

                                                        finish();
                                                        Intent intent=new Intent(getBaseContext(), PublicHomeActivity.class);
                                                        startActivity(intent);

                                                    }
                                                    else
                                                    {
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getApps").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("NA".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "getApps Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "getApps Don't Exists in Database. Now Creating !");
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getApps").setValue("NA");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getPhotos").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("NA".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "getPhotos Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "getPhotos Don't Exists in Database. Now Creating !");
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getPhotos").setValue("NA");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getCurrentPhoto").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("NA".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "getCurrentPhoto Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "getCurrentPhoto Don't Exists in Database. Now Creating !");
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getCurrentPhoto").setValue("NA");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("blockedContacts").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "blockedContacts Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "blockedContacts Don't Exists in Database. Now Creating !");
                                                                    //  "NA" is omitted purposely
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("blockedContacts").setValue("");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("watchedContacts").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "watchedContacts Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "watchedContacts Don't Exists in Database. Now Creating !");
                                                                    //  "NA" is omitted purposely
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("watchedContacts").setValue("");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getLocations").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("NA".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "getLocations Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "getLocations Don't Exists in Database. Now Creating !");
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getLocations").setValue("NA");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("watchListDetails").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "watchListDetails Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "watchListDetails Don't Exists in Database. Now Creating !");
                                                                    //  "NA" is omitted purposely
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("watchListDetails").setValue("");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getContacts").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("NA".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "getContacts Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "getContacts Don't Exists in Database. Now Creating !");
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getContacts").setValue("NA");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getMessages").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("NA".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "getMessages Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "getMessages Don't Exists in Database. Now Creating !");
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getMessages").setValue("NA");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getCalls").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("NA".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "getCalls Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "getCalls Don't Exists in Database. Now Creating !");
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("getCalls").setValue("NA");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                        mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("trigger").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                if (dataSnapshot.getValue() != null &&  !("NA".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                                                                {
                                                                    Log.d(TAG, "trigger Already Exists in Database= "+ dataSnapshot.getValue().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d(TAG, "trigger Don't Exists in Database. Now Creating !");
                                                                    mDatabase.child("Users").child(firebaseUser.getUid()).child(Imei).child("trigger").setValue("NA");
                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });


                                                        editor.putString("userAdded", "Y");
                                                        editor.commit();

                                                        Intent intent = new Intent(LoginActivity.this,SettingActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                    }
                                                }
                                                else
                                                {
                                                    //Toast.makeText(getApplicationContext(),"Try Again",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                        if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                        {
                                            Toast.makeText(LoginActivity.this,"Invalid Password" ,Toast.LENGTH_SHORT).show();
                                        }
                                        else if(task.getException() instanceof FirebaseAuthInvalidUserException)
                                        {
                                            Toast.makeText(LoginActivity.this,"User not Exist" ,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressDialog.hide();
                                }

                            }
                    );
                }
            }
        });

        m_tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        m_tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}