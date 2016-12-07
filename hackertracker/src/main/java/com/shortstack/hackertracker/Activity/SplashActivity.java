package com.shortstack.hackertracker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.BuildConfig;
import com.shortstack.hackertracker.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 8/29/12
 * Time: 2:26 PM
 */
public class SplashActivity extends AppCompatActivity {

    public static final int SPLASH_DELAY = BuildConfig.DEBUG ? 0 : 450;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startNextActivity();
            }
        }, SPLASH_DELAY);
    }



    private void startNextActivity() {
        boolean seenOnboarding = App.getStorage().seenOnboarding();

        //seenOnboarding = !BuildConfig.DEBUG; // To force debug builds to see the onboarding.

        if( seenOnboarding ) {
            startHomeActivity();
        } else {
            App.getStorage().markOnboardingSeen();
            startOnboardingActivity();
        }
    }

    private void startOnboardingActivity() {
        startActivity(new Intent(SplashActivity.this, TutorialActivity.class));
        finish();
    }

    private void startHomeActivity() {
        startActivity(new Intent(SplashActivity.this, TabHomeActivity.class));
        finish();
    }
}
