package com.example.lapcs.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lapcs.AppConsts;
import com.example.lapcs.InitApp.App;
import com.example.lapcs.api.RetrofitClient;
import com.example.lapcs.models.RequestNotificaton;
import com.example.lapcs.models.SendNotificationModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.lapcs.AppConsts.TAG;

public class PushNotificationHelper {

    public static void SendPushNotification(Context context, String DeviceToken, String Server_API_key, String ContentType, String notificationBody, String notificationTitle)
    {

        Log.d(AppConsts.TAG, PushNotificationHelper.class.getName()+": DeviceToken Value --> "+ DeviceToken);

        if( !("NIL".equals(DeviceToken.toString())) &&
                !(TextUtils.isEmpty(DeviceToken.toString()))
        )
        {
            Log.d(AppConsts.TAG, "Preparing Notification");

            SendNotificationModel sendNotificationModel = new SendNotificationModel(notificationBody,notificationTitle);
            RequestNotificaton requestNotificaton = new RequestNotificaton();
            requestNotificaton.setSendNotificationModel(sendNotificationModel);
            //token is id , whom you want to send notification
            requestNotificaton.setToken(DeviceToken);

            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getApi()
                    .sendPushNotification(requestNotificaton,Server_API_key,ContentType);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    try
                    {
                        Toast.makeText(context,"FCM Notification Sent Successfully.", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception ex)
                    {
                        Log.d(AppConsts.TAG, "FCM Notification Exception "+ ex.getMessage());
                        Toast.makeText(context,"FCM Notification Exception "+ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }

                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(AppConsts.TAG, "FCM Notification Sending Failed "+ t.getMessage());
                    Toast.makeText(context,"FCM Notification Sending Failed "+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            Toast.makeText(context,"Can't Send Notification !! Your Device is Not Linked.", Toast.LENGTH_LONG).show();
        }

    }

    public static void getParentDeviceToken(Context context, String nodeRef)
    {

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();

        String PhoneIMEI = sharedPreferences.getString("Imei", "");
        String userID = sharedPreferences.getString("UserID", "");

        if(     ("".equals(PhoneIMEI.toString())) && (TextUtils.isEmpty(PhoneIMEI.toString()))  &&
                ("".equals(userID.toString())) && (TextUtils.isEmpty(userID.toString()))
        )
        {
            Toast.makeText(context,"Imei and userID is empty !!", Toast.LENGTH_LONG).show();
            Log.d(TAG,PushNotificationHelper.class.getName()+": "+"Imei and userID is empty !!");
        }
        else
        {
            String LoggedInParentID = userID;

            DatabaseReference mDatabase;
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child(nodeRef).child(LoggedInParentID).child("tokenId").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() != null &&  !("".equals(dataSnapshot.getValue().toString())) && !(TextUtils.isEmpty(dataSnapshot.getValue().toString())))
                    {
                        editor.putString("ParentDeviceToken", dataSnapshot.getValue().toString());
                        editor.commit();

                        Log.d(TAG, this.getClass().getName()+": Parent Device Token is Saved in SharedPref = "+  dataSnapshot.getValue().toString());
                    }
                    else
                    {
                        Log.d(TAG, this.getClass().getName()+": Parent Device Token Don't Exists in Database.");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });





        }

    }


}
