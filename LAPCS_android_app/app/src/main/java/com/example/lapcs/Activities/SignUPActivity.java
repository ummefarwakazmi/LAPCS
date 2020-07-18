package com.example.lapcs.Activities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUPActivity extends Activity {
    EditText userName,email,pass,rePass;
    Button register;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ProgressDialog progressDialog;
    TextView goToLogin;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorIndigo));
        }
        setContentView(R.layout.activity_sign_up);
        userName=(EditText)findViewById(R.id.getUserName);
        goToLogin=(TextView)findViewById(R.id.goToLogin);
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getBaseContext(), LoginParentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        email=(EditText)findViewById(R.id.getUserEmail);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Registering New User..");
        pass=(EditText)findViewById(R.id.getUserPass);
        rePass=(EditText)findViewById(R.id.getUserRePass);
        firebaseAuth=FirebaseAuth.getInstance();
        register=(Button)findViewById(R.id.registerButton);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Name Can't be Empty", Toast.LENGTH_SHORT).show();
                    userName.setError("Name Can't be Empty");
                    userName.requestFocus();
                }else if (email.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Email Can't be Empty", Toast.LENGTH_SHORT).show();
                    email.setError("Email Can't be Empty");
                    email.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                    email.setError("Enter a valid email");
                    email.requestFocus();
                }
                else if (pass.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Password Can't be Empty", Toast.LENGTH_SHORT).show();
                    pass.requestFocus();
                }else if (!rePass.getText().toString().equals(pass.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Password does not Match", Toast.LENGTH_SHORT).show();
                    rePass.requestFocus();
                }else
                {
                    progressDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString());
                                        firebaseUser=firebaseAuth.getCurrentUser();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(userName.getText().toString()).build();
                                        firebaseUser.updateProfile(profileUpdates);
                                        if(sharedPreferences.getString("Mode", "").equalsIgnoreCase("ParentActivity"))
                                        {
                                            Intent intent=new Intent(getApplicationContext(), LoginParentActivity.class);
                                            progressDialog.hide();
                                            startActivityForResult(intent,103);
                                            finish();
                                        }

//                                        if(sharedPreferences.getString("Mode", "").equalsIgnoreCase("ChildActivity"))
//                                        {
//                                            Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
//                                            progressDialog.hide();
//                                            startActivityForResult(intent,103);
//                                            finish();
//                                        }

                                    }

                                }
                            }
                    )
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof FirebaseAuthUserCollisionException)
                            {
                                Toast.makeText(SignUPActivity.this,"User Already Exists" ,Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                            }
                        }
                    });
                }

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