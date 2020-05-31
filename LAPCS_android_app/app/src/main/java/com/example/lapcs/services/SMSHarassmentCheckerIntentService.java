package com.example.lapcs.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lapcs.AppConsts;
import com.example.lapcs.Helpers.PushNotificationHelper;
import com.example.lapcs.models.SMSInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.lapcs.AppConsts.ContentType;
import static com.example.lapcs.AppConsts.HarassmentWordsList;
import static com.example.lapcs.AppConsts.Server_API_key;
import static com.example.lapcs.AppConsts.TAG;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SMSHarassmentCheckerIntentService extends IntentService {

    SharedPreferences sharedPreferences;
    String parentDeviceTokenID= "";

    public SMSHarassmentCheckerIntentService() {
        super("SMSHarassmentCheckerIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,this.getClass().getName()+": onCreate");
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG,this.getClass().getName()+": onHandleIntent");

        if(intent!=null)
        {
            Gson gson = new Gson();
            SMSInfo incomingSMSObj = gson.fromJson(intent.getStringExtra("incomingSMS_JSON"), SMSInfo.class);
            String contactNameToQuery = "";

            if("Unknown".equals(incomingSMSObj.ContactName))
            {
                contactNameToQuery = "Unknown";
            }
            else
            {
                contactNameToQuery = incomingSMSObj.ContactName.trim();
            }

            if(incomingSMSObj.Body.length()>0 && !incomingSMSObj.Body.isEmpty())
            {
                List<String> IncomingSMSWordsList = new ArrayList<String>(Arrays.asList(incomingSMSObj.Body.split("\\s+")));    //s+ for single or multiple whitespaces

                Log.d(TAG, this.getClass().getName()+":  IncomingSMSWordsList after Splitting=" + new Gson().toJson(IncomingSMSWordsList));

                List<String> IncomingSMSWordsLowerCaseList = new ArrayList<>();

                for(int i = 0; i<IncomingSMSWordsList.size(); i++) {
                    String SMSWordLower = IncomingSMSWordsList.get(i).toLowerCase();
                    Log.d(TAG, this.getClass().getName()+":  SMSWordLower=" + SMSWordLower);
                    IncomingSMSWordsLowerCaseList.add(SMSWordLower);
                }
                Log.d(TAG, this.getClass().getName()+":  IncomingSMSWordsLowerCaseList=" + new Gson().toJson(IncomingSMSWordsLowerCaseList));

                Collection<String> SimilarWordsList = new HashSet<String>( IncomingSMSWordsLowerCaseList );

                for (String IncomingSMSWord : IncomingSMSWordsLowerCaseList) {
                    Log.d(TAG, this.getClass().getName()+": IncomingSMSWordsLowerCaseList after Splitting= "+IncomingSMSWord);
                }

                SimilarWordsList.retainAll(HarassmentWordsList);
                Log.d(TAG, this.getClass().getName()+":  SimilarWordsList=" + new Gson().toJson(SimilarWordsList));

                StringBuilder HarrasmentWordsFoundString  = new StringBuilder("");
                HarrasmentWordsFoundString.append("");
                for (String HarassmentWordFound : SimilarWordsList) {
                    Log.d(TAG, this.getClass().getName()+": HarassmentWordFound = "+HarassmentWordFound);
                    HarrasmentWordsFoundString.append(HarassmentWordFound+",");
                }

                if(SimilarWordsList.size()<=0)
                {
                    for(int i = 0; i<HarassmentWordsList.size(); i++) {
                        String HarassmentWord = ((List<String>)HarassmentWordsList).get(i) ;
                        String msgBody = incomingSMSObj.Body;
                        if( containsIgnoreCase(msgBody , HarassmentWord))
                        {
                            Log.d(TAG, this.getClass().getName()+": Found " + HarassmentWord + " within " + msgBody + "." );
                            HarrasmentWordsFoundString.append(HarassmentWord+",");
                        }

                    }
                }

                if(
                    HarrasmentWordsFoundString.length()>0   &&
                    !"".equals(HarrasmentWordsFoundString)  &&
                    HarrasmentWordsFoundString != null
                )
                {
                    parentDeviceTokenID = sharedPreferences.getString("ParentDeviceToken", "");
                    if(!parentDeviceTokenID.equals(""))
                    {
                        // Sending Notification to Parent Device
                        PushNotificationHelper.SendPushNotification(getApplicationContext(),parentDeviceTokenID,Server_API_key,ContentType,contactNameToQuery + " is harassing your Child! ","Harassment Alert!");
                    }
                }

            }

        }

    }

    public boolean containsIgnoreCase( String haystack, String needle ) {
        if("".equals(needle))
            return true;
        if(haystack == null || needle == null || "".equals(haystack))
            return false;

        Pattern p = Pattern.compile(needle,Pattern.CASE_INSENSITIVE|Pattern.LITERAL);
        Matcher m = p.matcher(haystack);
        return m.find();
    }

}
