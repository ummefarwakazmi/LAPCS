package com.example.lapcs.Activities.fragments.logout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.lapcs.Activities.LoginActivity;
import com.example.lapcs.Activities.LoginParentActivity;
import com.example.lapcs.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.lapcs.AppConsts.TAG;


public class LogoutFragment extends Fragment {


    //defining firebaseauth object
    private FirebaseAuth mAuth;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_logout, container, false);
        final TextView textView = root.findViewById(R.id.text_send);

        mAuth = FirebaseAuth.getInstance();


        logout();


        return root;
    }


    private void logout() {

        //logging out the user
        mAuth.signOut();
        Log.d(TAG,this.getClass().getName()+": "+" User: "+mAuth.getUid()+" Signed Out Successfully! ");
        //closing activity
        getActivity().finish();
        //starting login activity

        Intent intent = new Intent(getActivity(), LoginParentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}