package com.example.lapcs.models;

public class SMS {

    private int SMS_ID;
    private String SenderName, SMSBody;

    public SMS(int SMS_ID, String senderName, String SMSBody) {
        this.SMS_ID = SMS_ID;
        SenderName = senderName;
        this.SMSBody = SMSBody;
    }

    public int getSMS_ID() {
        return SMS_ID;
    }

    public String getSenderName() {
        return SenderName;
    }

    public String getSMSBody() {
        return SMSBody;
    }

    public void setSMS_ID(int SMS_ID) {
        this.SMS_ID = SMS_ID;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public void setSMSBody(String SMSBody) {
        this.SMSBody = SMSBody;
    }


}
