package com.example.lapcs.models;

public class CallLog {

    private int CallLog_ID;
    private String Sender, SIM_No;

    public CallLog(int callLog_ID, String sender, String SIM_No) {
        CallLog_ID = callLog_ID;
        Sender = sender;
        this.SIM_No = SIM_No;
    }

    public int getCallLog_ID() {
        return CallLog_ID;
    }

    public String getSender() {
        return Sender;
    }

    public String getSIM_No() {
        return SIM_No;
    }

    public void setCallLog_ID(int callLog_ID) {
        CallLog_ID = callLog_ID;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public void setSIM_No(String SIM_No) {
        this.SIM_No = SIM_No;
    }


}
