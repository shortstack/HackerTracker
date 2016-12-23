package com.shortstack.hackertracker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shortstack.hackertracker.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_DELAY = 450;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startHomeActivity();
            }
        }, SPLASH_DELAY);
    }

    private void startHomeActivity() {
        startActivity(new Intent(SplashActivity.this, TabHomeActivity.class));
        finish();
    }
}
