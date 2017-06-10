package com.shortstack.hackertracker.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shortstack.hackertracker.R
import java.util.*

class SplashActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startHomeActivity()
            }
        }, SPLASH_DELAY.toLong())
    }

    private fun startHomeActivity() {
        startActivity(Intent(this@SplashActivity, TabHomeActivity::class.java))
        finish()
    }

    companion object {

        val SPLASH_DELAY = 450
    }
}
