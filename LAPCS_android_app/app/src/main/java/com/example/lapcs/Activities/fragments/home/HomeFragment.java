package com.example.lapcs.Activities.fragments.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lapcs.Activities.LoginParentActivity;
import com.example.lapcs.Activities.fragments.children.ChildrenFragment;
import com.example.lapcs.Activities.fragments.logout.LogoutFragment;
import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.InitApp.App;
import com.example.lapcs.R;
import com.example.lapcs.models.ParentMetaData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;


public class HomeFragment extends Fragment {


    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabaseParentMetaData;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    TelephonyManager telephonyManager;
    private Button logoutBtn;
    private Button  m_BtnMoveToChild;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.tv_home_str_1);

        PushDeviceTokenToDB();

       logoutBtn = (Button) root.findViewById(R.id.btn_logout);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
               logout();
            }
        });
        m_BtnMoveToChild = (Button) root.findViewById(R.id.btn_children);
        m_BtnMoveToChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new ChildrenFragment());
            }
        });
        return root;
    }


    public  void PushDeviceTokenToDB()
    {
        //initializing firebase authentication object
        mAuth = FirebaseAuth.getInstance();
        //getting current user
        FirebaseUser user = mAuth.getCurrentUser();

        sharedPreferences = getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        App.DeviceToken = sharedPreferences.getString("DeviceToken", "NIL");
        Log.d(TAG,"Devices Fragment:Oncreate View: Token Value From Shared Pref: "+App.DeviceToken);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'Parent Metadata' Node
        mFirebaseDatabaseParentMetaData = mFirebaseInstance.getReference(AppConsts.Parent_Meta_Data_Node_Ref);

        //Toast.makeText(getContext(),"Saving Parent Metadata  ...",Toast.LENGTH_SHORT).show();
        Log.d(TAG,this.getClass().getName()+": "+"Saving Parent Metadata  ...");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (task.isSuccessful()) {
                            try {

                                String NewDeviceToken= task.getResult().getToken();
                                SaveDeviceToken(NewDeviceToken);

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(),"Token generation Exception",Toast.LENGTH_SHORT).show();
                                Log.d(TAG,this.getClass().getName()+": "+"Token generation Exception");
                            }
                        } else {
                            Toast.makeText(getContext(),"Token generation failed",Toast.LENGTH_SHORT).show();
                            Log.d(TAG,this.getClass().getName()+": "+"Token generation failed");
                        }

                    }
                });
    }

    public  void SaveDeviceToken(String NewDeviceToken)
    {
        try
        {
            String parentID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                //Toast.makeText(getActivity(), "Device Linking Error!! IMEI Reading Permission not Assigned", Toast.LENGTH_SHORT).show();
                Log.d(TAG,this.getClass().getName()+": "+"Device Linking Error!! IMEI Reading Permission not Assigned");
                return;
            }

            final String imei=telephonyManager.getDeviceId();
            ParentMetaData parentMetaData = new ParentMetaData(NewDeviceToken,imei);
            Log.d(TAG,"HomeFragments: SaveDeviceToken: parentid" +parentID);

            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child(AppConsts.Parent_Meta_Data_Node_Ref).child(parentID).child("tokenId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null &&  !("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                    {
                       // Toast.makeText(getContext(), "Token Already Saved", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,this.getClass().getName()+": "+"Token Already Saved");
                        Log.d(TAG, this.getClass().getName()+"Token Already Saved: Token Value: "+  dataSnapshot.getValue().toString());

                        if(! dataSnapshot.getValue().toString().equals(NewDeviceToken))     //if already parent with same uid exists
                        {
                            App.DeviceToken = NewDeviceToken;
                            mFirebaseDatabaseParentMetaData.child(parentID).setValue(parentMetaData);
                            editor.putString("DeviceToken", NewDeviceToken);
                            editor.commit();
                        }
                        else
                        {
                            App.DeviceToken = dataSnapshot.getValue().toString();
                            editor.putString("DeviceToken", dataSnapshot.getValue().toString());
                            editor.commit();
                        }

                    }
                    else
                    {

                        App.DeviceToken = NewDeviceToken;
                        Log.d(AppConsts.TAG, this.getClass().getName()+": "+"onComplete: Token: " + App.DeviceToken);
                       // Toast.makeText(getContext(),"Parent Token Generated: " + App.DeviceToken,Toast.LENGTH_SHORT).show();
                        mFirebaseDatabaseParentMetaData.child(parentID).setValue(parentMetaData);
                        //displaying a success toast
                        //Toast.makeText(getContext(), "Token Saved Successfully. This is Test Notification!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,this.getClass().getName()+": "+"Token Saved Successfully. This is Test Notification!");

                        editor.putString("DeviceToken", App.DeviceToken);
                        editor.commit();

                       // PushNotificationHelper.SendPushNotification(getContext(),App.DeviceToken,Server_API_key,ContentType,"Token Saved Successfully. This is Test Notification!","LAPCS Alert!");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getMessage());
                    Toast.makeText(getContext(), "The read failed: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG,this.getClass().getName()+": "+"The read failed: " + databaseError.getMessage());
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),"Parent Metadata Exception: "+e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d(TAG,this.getClass().getName()+": "+"Parent Metadata Exception: "+e.getMessage());
        }
    }
    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.commit(); // save the changes
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