package com.zerotechiot.eg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

// import com.thingclips.smart.demo_login.base.activity.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_LOGGED_IN = 500; // Reduced to 0.5s
    private static final int SPLASH_TIME_LOGGED_OUT = 1500; // Reduced to 1.5s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check login status immediately
        SharedPreferences sharedPreferences = getSharedPreferences("ZeroTechPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        // Determine splash time based on login status
        int splashTime = isLoggedIn ? SPLASH_TIME_LOGGED_IN : SPLASH_TIME_LOGGED_OUT;

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (isLoggedIn) {
                    // User is logged in, go directly to main activity
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    // User is not logged in, go to login activity
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, splashTime);
    }
}
