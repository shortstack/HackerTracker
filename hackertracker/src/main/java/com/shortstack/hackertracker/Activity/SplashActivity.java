package com.shortstack.hackertracker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.shortstack.hackertracker.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 8/29/12
 * Time: 2:26 PM
 */
public class SplashActivity extends Activity {

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
        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        finish();
    }
}
