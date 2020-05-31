package com.example.lapcs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lapcs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class UpdatePasswordActivity extends AppCompatActivity {

    private Button m_BtnUpdatePassword;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;

    private Button m_BtnAuthenticatePassword;
    private EditText editTextOldPassword;

    //defining firebaseauth object
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private LinearLayout LinearLayoutAuthenticatePassword;
    private LinearLayout LinearLayoutUpdatePassword;

    private Button m_BtnLogout;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        LinearLayoutAuthenticatePassword   = (LinearLayout) findViewById(R.id.linear_layout_authenticate_password);
        LinearLayoutUpdatePassword         = (LinearLayout) findViewById(R.id.linear_layout_update_password);

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        editTextOldPassword         = findViewById(R.id.editTextOldPassword);
        m_BtnAuthenticatePassword   = (Button) findViewById(R.id.btn_Authenticate_password);

        m_BtnAuthenticatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthenticatePassword();
            }
        });

        editTextNewPassword         = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword     = findViewById(R.id.editTextConfirmPassword);
        m_BtnUpdatePassword         = (Button) findViewById(R.id.btn_update_password);

        m_BtnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
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

    private void AuthenticatePassword() {

        String userEmail = "";
        //if getCurrentUser does not return null
        if(mAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            userEmail =  mAuth.getCurrentUser().getEmail();
        }
        else
        {
            Toast.makeText(UpdatePasswordActivity.this,"User not LoggedIn" ,Toast.LENGTH_LONG).show();
            return;
        }

        String oldPassword = editTextOldPassword.getText().toString().trim();

        if (oldPassword.isEmpty()) {
            editTextOldPassword.setError("Password required");
            editTextOldPassword.requestFocus();
            return;
        }

        //if the old password is not empty
        //displaying a progress dialog
        progressDialog.setMessage("Authenticating Password. Please Wait...");
        progressDialog.show();

        AuthCredential credential = EmailAuthProvider
                .getCredential(userEmail, oldPassword);

        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(UpdatePasswordActivity.this,"Authentication Succeeded" ,Toast.LENGTH_LONG).show();

                            LinearLayoutAuthenticatePassword.setVisibility(View.GONE);
                            LinearLayoutUpdatePassword.setVisibility(View.VISIBLE);
                        }
                        else
                        {

                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(UpdatePasswordActivity.this,"Invalid Password" ,Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Toast.makeText(UpdatePasswordActivity.this,"Password Authentication Failed" ,Toast.LENGTH_LONG).show();
                            }
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void updatePassword() {

        String NewPassword = editTextNewPassword.getText().toString().trim();

        if (NewPassword.isEmpty()) {
            editTextNewPassword.setError("Password required");
            editTextNewPassword.requestFocus();
            return;
        }

        if (NewPassword.length() < 6) {
            editTextNewPassword.setError("Password should be atleast 6 character long");
            editTextNewPassword.requestFocus();
            return;
        }

        String ConfirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (ConfirmPassword.isEmpty()) {
            editTextConfirmPassword.setError("Password required");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (ConfirmPassword.length() < 6) {
            editTextConfirmPassword.setError("Password should be atleast 6 character long");
            editTextConfirmPassword.requestFocus();
            return;
        }

        //if the new password is not empty
        //displaying a progress dialog
        progressDialog.setMessage("Updating Password. Please Wait...");
        progressDialog.show();

        mAuth.getCurrentUser().updatePassword(NewPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(UpdatePasswordActivity.this,"Password Updated" ,Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            logout();
                        }
                        else
                        {
                            Toast.makeText(UpdatePasswordActivity.this,"Password Authentication Failed" ,Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void logout() {

        //logging out the user
        mAuth.signOut();
        //closing activity
        finish();
        //starting login activity

        if(sharedPreferences.getString("Mode", "").equalsIgnoreCase("ParentActivity"))
        {
            Intent intent=new Intent(getApplicationContext(), LoginParentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            progressDialog.dismiss();
            startActivity(intent);
            finish();
        }
        else if(sharedPreferences.getString("Mode", "").equalsIgnoreCase("ChildActivity"))
        {
            Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            progressDialog.dismiss();
            startActivity(intent);
            finish();
        }

    }


}
