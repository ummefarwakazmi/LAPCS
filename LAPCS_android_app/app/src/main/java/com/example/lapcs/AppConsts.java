package com.example.lapcs;

import java.util.Arrays;
import java.util.Collection;

public class AppConsts {

    //-------------------------------Application Constants-------------------

    public static final String TAG = "MyTag";
    public static final String LAPCS_CHANNEL_ID = "LAPCS_CHANNEL_ID";
    public static final String LAPCS_LOCATION_CHANNEL_ID = "LAPCS_LOCATION_CHANNEL_ID";
    public static final String LAPCS_SOS_CHANNEL_ID = "LAPCS_SOS_CHANNEL_ID";

    public static final int INITIAL_DELAY_IN_SECONDS = 5*1000;

    private static final int REQUIRED_INTERVAL_IN_SECONDS = 10;
    public static final long NOTIFY_INTERVAL = REQUIRED_INTERVAL_IN_SECONDS* 1000;

    public static final int Notification_ID = 1002;
    public static final int Location_Notification_ID = 1003;

    public static final String SHARED_PREF_NAME = "LAPCS_SHARED_PREF";



    //lapcs1234
    public static final String Server_API_key       = "key=AAAAfRuu2i8:APA91bHcxlqlxwwpy5QSD_LXV2KlkUBpmGn50NK7cYiIZtJoC-IvctP1TDm2fR53Bv40j0YHFop_Fi1TF4WcR9vOXrJlez6GlKjWEEyF13fn9C4N9Ewwe7Rc4GMLjpf2HNkmj0FtINOx";
    public static final String ContentType          = "application/json";

    public static final String Users_Node_Ref                       = "Users";
    public static final String Mobiles_Node_Ref                     = "Mobiles";
    public static final String Child_Meta_Data_Node_Ref             = "ChildMetaData";
    public static final String Parent_Meta_Data_Node_Ref            = "ParentMetaData";

    public static final String PROX_ALERT_INTENT = "com.example.lapcs.ProximityAlert";


    public static final Collection<String> HarassmentWordsList = Arrays.asList("touching",
                                                                                "groping",
                                                                                "whistles",
                                                                                "sexual",
                                                                                "stalking",
                                                                                "commenting",
                                                                                "rape",
                                                                                "indecent",
                                                                                "snatching",
                                                                                "ogling",
                                                                                "facial",
                                                                                "staring",
                                                                                "pictures",
                                                                                "poor",
                                                                                "harassment");


    public static final String SEND_MESSAGE_ACTION = "SEND_MESSAGE_ACTION";
    public static final String SEND_ALERT_ACTION = "SEND_ALERT_ACTION";
    public static final String DEFAULT_EMERGENCY_NUMBER = "+923365253021";
    public static final int SEND_MESSAGE_FROM_LOCK_SCREEN_NOTIFICATION_ID = 1004;

    public static final String LOCKSCREEN_SOS_MSG_INTENT = "com.example.lapcs.lockscreen_sos_msg";
    public static final String LOCKSCREEN_SOS_ALERT_INTENT = "com.example.lapcs.lockscreen_sos_alert";


}
