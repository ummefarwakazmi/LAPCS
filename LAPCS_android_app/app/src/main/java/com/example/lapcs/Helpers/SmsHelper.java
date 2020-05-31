package com.example.lapcs.Helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lapcs.Activities.SOSMessageActivity;
import com.example.lapcs.AppConsts;
import com.example.lapcs.R;

public class SmsHelper {

    public static final String SMS_CONDITION = "Some condition";

    private static final int SMS_PERMISSION_CODE = 0;

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static void sendDebugSms(String number, String smsBody) {
        SmsManager smsManager = SmsManager.getDefault();

        smsManager.sendTextMessage(number, null, smsBody, null, null);
    }

    /**
     * Validates if the app has readSmsPermissions and the mobile phone is valid
     *
     * @return boolean validation value
     */
    public static boolean hasValidPreConditions(Context context,String PhoneNumber) {
        if (!hasReadSmsPermission(context)) {
            requestReadAndSendSmsPermission(context);
            return false;
        }

        if (!SmsHelper.isValidPhoneNumber(PhoneNumber)) {
            Toast.makeText(context, R.string.error_invalid_phone_number, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Optional informative alert dialog to explain the user why the app needs the Read/Send SMS permission
     */
    public static void showRequestPermissionsInfoAlertDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.permission_alert_dialog_title);
        builder.setMessage(R.string.permission_dialog_message);
        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestReadAndSendSmsPermission(context);
            }
        });
        builder.show();
    }

    /**
     * Runtime permission shenanigans
     */
    public static boolean hasReadSmsPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestReadAndSendSmsPermission(Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_SMS)) {
            Log.d(AppConsts.TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                SMS_PERMISSION_CODE);
    }


}
