package com.example.lapcs.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;


import com.example.lapcs.R;
import com.example.lapcs.storage.SharedPrefManager;

import androidx.appcompat.app.AppCompatActivity;

public class IntroSlider1Activity extends AppCompatActivity {
    private static final long SPLASH_DISPLAY_TIME = 1000;
    private Button m_BtnNext;
    private Button m_BtnSkip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider1);

        m_BtnNext = (Button) findViewById(R.id.BtnNext);
        m_BtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(IntroSlider1Activity.this,
                        IntroSlider2Activity.class);
                startActivity(mainIntent);
                IntroSlider1Activity.this.finish();
            }
        });

        m_BtnSkip = (Button) findViewById(R.id.BtnSkip);
        m_BtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.clearSharedPreferences(getApplicationContext());

                Intent mainIntent = new Intent(IntroSlider1Activity.this,
                        MainActivity.class);
                startActivity(mainIntent);
                IntroSlider1Activity.this.finish();
            }
        });

    }
}
