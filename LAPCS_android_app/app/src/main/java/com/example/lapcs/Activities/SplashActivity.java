package com.example.lapcs.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.lapcs.R;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    private int i = 0;
    Handler handler = new Handler();

    //defining firebaseauth object
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        mAuth = FirebaseAuth.getInstance();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setScaleY(3f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProgressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(216, 48, 237)));
        }

        i = mProgressBar.getProgress();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (i < 100) {
                    i += 1;
                    //Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(i);
                        }
                    });
                    try {
                        // Sleep for 100 milliseconds to show the progress slowly.
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (sharedPreferences.getString("IntroSlider5ActivityVisited", "").equalsIgnoreCase("")) {
                    Intent i = new Intent(SplashActivity.this, IntroSlider1Activity.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }).start();

    }
}
