package com.example.lapcs.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lapcs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.lapcs.AppConsts.TAG;

public class LoginParentActivity extends Activity {
    EditText email,password;
    TextView goToSignUp;
    TextView m_tv_forgot_password;
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
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        setContentView(R.layout.activity_login_parent);
        firebaseAuth=FirebaseAuth.getInstance();

        //if getCurrentUser does not return null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();
            //and open profile activity
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        email=(EditText)findViewById(R.id.getEmail);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        password=(EditText)findViewById(R.id.getPass);
        login=(Button) findViewById(R.id.Login);
        goToSignUp=(TextView) findViewById(R.id.goToRegister);
        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(), SignUPActivity.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(getApplicationContext(),"Email Can't be Empty",Toast.LENGTH_SHORT).show();
                else if (password.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(getApplicationContext(),"Password Can't be Empty",Toast.LENGTH_SHORT).show();
                else
                {
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        firebaseUser=firebaseAuth.getCurrentUser();
                                        editor.putString("Email", email.getText().toString());
                                        editor.putString("UserID", firebaseUser.getUid());
                                        editor.commit();

                                        Log.d(TAG,this.getClass().getName()+": "+"Email: "+
                                                sharedPreferences.getString("Email", "")+
                                                " and UserID: "+sharedPreferences.getString("UserID", "")+
                                                " is Saved in Shared Pref");
                                        //Intent i = new Intent(getApplicationContext(), ParentActivity.class);
                                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(i);
                                    }
                                    else
                                        Toast.makeText(getApplicationContext(),task.getException()+"",Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(LoginParentActivity.this,ResetPasswordActivity.class);
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