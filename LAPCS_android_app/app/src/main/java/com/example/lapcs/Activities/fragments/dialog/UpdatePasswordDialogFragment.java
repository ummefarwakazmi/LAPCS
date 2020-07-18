package com.example.lapcs.Activities.fragments.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lapcs.Activities.LoginActivity;
import com.example.lapcs.Activities.LoginParentActivity;
import com.example.lapcs.Activities.UpdatePasswordActivity;
import com.example.lapcs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class UpdatePasswordDialogFragment extends DialogFragment {

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

    SharedPreferences sharedPreferences;

    public UpdatePasswordDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_update_password, container);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setClipToOutline(true);
        }

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(getActivity());

        LinearLayoutAuthenticatePassword   = (LinearLayout) view.findViewById(R.id.linear_layout_authenticate_password);
        LinearLayoutUpdatePassword         = (LinearLayout) view.findViewById(R.id.linear_layout_update_password);

        sharedPreferences =  getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);

        editTextOldPassword         = view.findViewById(R.id.editTextOldPassword);
        m_BtnAuthenticatePassword   = (Button) view.findViewById(R.id.btn_Authenticate_password);

        m_BtnAuthenticatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthenticatePassword();
            }
        });

        editTextNewPassword         = view.findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword     = view.findViewById(R.id.editTextConfirmPassword);
        m_BtnUpdatePassword         = (Button) view.findViewById(R.id.btn_update_password);

        m_BtnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public static UpdatePasswordDialogFragment newInstance(String title) {
        UpdatePasswordDialogFragment frag = new UpdatePasswordDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
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
            Toast.makeText(getActivity(),"User not LoggedIn" ,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(),"Authentication Succeeded" ,Toast.LENGTH_SHORT).show();

                            LinearLayoutAuthenticatePassword.setVisibility(View.GONE);
                            LinearLayoutUpdatePassword.setVisibility(View.VISIBLE);
                        }
                        else
                        {

                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(getActivity(),"Invalid Password" ,Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getActivity(),"Password Authentication Failed" ,Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(),"Password Updated" ,Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            logout();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"Password Authentication Failed" ,Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private void logout() {

        //logging out the user
        mAuth.signOut();

        //starting login activity
        Intent intent=new Intent(getActivity(), LoginParentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        progressDialog.dismiss();
        startActivity(intent);
        dismiss();

    }

}
