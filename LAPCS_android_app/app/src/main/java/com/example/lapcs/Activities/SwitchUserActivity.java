package com.example.lapcs.Activities;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.lapcs.AppConsts;
import com.example.lapcs.R;
import com.example.lapcs.services.WatchListService;

import static com.example.lapcs.AppConsts.TAG;

public class SwitchUserActivity extends Activity {
    Button parent, child;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        setContentView(R.layout.activity_switch_user);
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.remove("Mode").commit();
        editor.putString("Mode", "");
        editor.commit();
        parent=(Button) findViewById(R.id.Login);
        child=(Button) findViewById(R.id.Child);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Mode", "ParentActivity");
                editor.apply();
                Intent intent=new Intent(getBaseContext(), LoginParentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("Mode", "ChildActivity");
                editor.commit();



                final String Imei = sharedPreferences.getString("Imei", "");
                final String userID = sharedPreferences.getString("UserID", "");

                if(     !("".equals(Imei.toString())) && !(TextUtils.isEmpty(Imei.toString()))  &&
                        !("".equals(userID.toString())) && !(TextUtils.isEmpty(userID.toString()))
                )
                {
                    Log.d(TAG,this.getClass().getName()+": "+"Imei: "+Imei+"userID: "+userID);
                    Intent intent=new Intent(getBaseContext(), PublicHomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Log.d(TAG,this.getClass().getName()+": "+"Imei and userID is empty !!");
                    Intent intent=new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }



            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}