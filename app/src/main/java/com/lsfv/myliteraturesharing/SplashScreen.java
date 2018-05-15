package com.lsfv.myliteraturesharing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {
    long Delay = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        // Create a Timer
        Timer RunSplash = new Timer();

        // Task to do when the timer ends
        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                // Close SplashScreenActivity.class
                finish();

                // Start MainActivity.class
                /*Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(myIntent);*/

                SharedPreferences settings = getSharedPreferences("profile", MODE_PRIVATE);
                String s= "default";
                s=settings.getString("number",null);
                if (s==null) {
                    //Toast.makeText(SplashScreen.this, s, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    //Toast.makeText(SplashScreen.this, s, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        // Start the timer
        RunSplash.schedule(ShowSplash, Delay);
    }
}
