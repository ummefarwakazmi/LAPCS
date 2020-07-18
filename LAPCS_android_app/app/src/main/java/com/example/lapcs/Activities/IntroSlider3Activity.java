package com.example.lapcs.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;


import com.example.lapcs.R;
import com.example.lapcs.storage.SharedPrefManager;

import androidx.appcompat.app.AppCompatActivity;

public class IntroSlider3Activity extends AppCompatActivity {
    private static final long SPLASH_DISPLAY_TIME = 1000;
    private Button m_BtnNext;
    private Button m_BtnSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorIndigo));
        }
        setContentView(R.layout.activity_intro_slider3);

        m_BtnNext = (Button) findViewById(R.id.BtnNext);
        m_BtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(IntroSlider3Activity.this,
                        IntroSlider4Activity.class);
                startActivity(mainIntent);
                IntroSlider3Activity.this.finish();
            }
        });

        m_BtnSkip = (Button) findViewById(R.id.BtnSkip);
        m_BtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.clearSharedPreferences(getApplicationContext());

                Intent mainIntent = new Intent(IntroSlider3Activity.this,
                        MainActivity.class);
                startActivity(mainIntent);
                IntroSlider3Activity.this.finish();
            }
        });

    }
}
