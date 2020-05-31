package com.example.lapcs.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SMSInfo {

    public String Body = "";
    public String ContactNumber = "";
    public String ContactName = "";
    public String Time = "";
    public String Type = "";        //Sent or Received

    public SMSInfo(String body, String contactNumber, String contactName, String time, String type) {
        Body = body;
        ContactNumber = contactNumber;
        ContactName = contactName;
        Time = time;
        Type = type;
    }

    public SMSInfo(String contactName, String body)
    {
        Body = body;
        ContactName = contactName;
    }

    public static String GetCurrentTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

}