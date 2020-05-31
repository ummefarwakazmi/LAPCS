package com.example.lapcs.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CallInfo {

    public String Number = "";
    public String DateTime = "", DateTimeOnly = "";
    public boolean OutGoingCall = false;
    //public int CallLength = 0;
    //public boolean Missed = false;
    public String Name = "";
    public String Type = "";    //Incoming, Outgoing or Missed

    //public CallInfo(String number, boolean outGoingCall,int length, String time, String name,String type)
    public CallInfo(String number, boolean outGoingCall, String time, String name,String type)
    {
        Number = number;
        OutGoingCall = outGoingCall;
        //CallLength = length;
        DateTime = time;
        //Missed = false;
        DateTimeOnly = GetCurrentTimeOnly();
        Name = name;
        Type = type;
    }

    public CallInfo(String number, boolean outGoingCall,int length)
    {
        Number = number;
        OutGoingCall = outGoingCall;
        //CallLength = length;
        //Missed = false;
        DateTimeOnly = GetCurrentTimeOnly();
    }

    public CallInfo(String number, String time)
    {
        Number = number;
        OutGoingCall = false;
        //CallLength = 0;
        DateTime = time;
        //Missed = true;
        DateTimeOnly = GetCurrentTimeOnly();
    }

    public static String GetCurrentTimeOnly()
    {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

    public static String GetCurrentTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        return format.format(Calendar.getInstance().getTime());
    }

}