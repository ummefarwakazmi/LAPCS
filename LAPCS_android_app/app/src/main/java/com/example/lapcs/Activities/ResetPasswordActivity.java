package com.example.lapcs.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lapcs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button m_BtnReset;
    private TextView m_tvLogin;
    private EditText editTextEmail;
    SharedPreferences sharedPreferences;


    //defining firebaseauth object
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(this);

        //if getCurrentUser does not return null
        if(mAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();
            //and open profile activity
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }


        editTextEmail = findViewById(R.id.editTextEmail);

        m_BtnReset = (Button) findViewById(R.id.buttonReset);

        m_BtnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        m_tvLogin = (TextView) findViewById(R.id.textViewLogin);

        m_tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(sharedPreferences.getString("Mode", "").equalsIgnoreCase("ParentActivity"))
                {
                    Intent intent=new Intent(getApplicationContext(), LoginParentActivity.class);
                    progressDialog.hide();
                    startActivity(intent);
                    finish();
                }
                else if(sharedPreferences.getString("Mode", "").equalsIgnoreCase("ChildActivity"))
                {
                    Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                    progressDialog.hide();
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    private void resetPassword() {

        String email = editTextEmail.getText().toString().trim();

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

        //if the email is not empty
        //displaying a progress dialog
        progressDialog.setMessage("Sending Email. Please Wait...");
        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(ResetPasswordActivity.this,"We have sent you Email to Reset your password!" ,Toast.LENGTH_LONG).show();

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
                        else
                        {
                            Toast.makeText(ResetPasswordActivity.this,"Failed to Send Reset Email!",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }

                });

    }

}
