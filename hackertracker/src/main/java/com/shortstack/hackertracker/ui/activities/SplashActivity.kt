package com.shortstack.hackertracker.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.event.SetupDatabaseEvent
import com.shortstack.hackertracker.network.service.UpdateDatabaseService
import com.squareup.otto.Subscribe
import java.util.*


class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 500L
    private var isComplete = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        App.application.registerBusListener(this)

        Logger.e("Waiting for database setup.")


//        App.application.cons.conferenceDao().getCurrentCon()


//        if( App.application.databaseController.databaseName == Constants.TOORCON_DATABASE_NAME ) {
//            splash_image.setBackgroundResource(R.drawable.tc_19_wallpaper)
//        } else if (App.application.databaseController.databaseName == Constants.SHMOOCON_DATABASE_NAME) {
//            splash_image.setBackgroundResource(R.drawable.shmoocon_14_wallpaper)
//        } else if (App.application.databaseController.databaseName == Constants.HACKWEST_DATABASE_NAME) {
//            splash_image.setBackgroundResource(R.drawable.hackwest_wallpaper)
//        } else if (App.application.databaseController.databaseName == Constants.LAYERONE_DATABASE_NAME) {
//            splash_image.setBackgroundResource(R.drawable.layerone_wallpaper)
//        } else if (App.application.databaseController.databaseName == Constants.BSIDESORL_DATABASE_NAME) {
//            splash_image.setBackgroundResource(R.drawable.bsidesorl_wallpaper)
//        }


        startService(Intent(this@SplashActivity, UpdateDatabaseService::class.java))

//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                startHomeActivity()
//            }
//        }, SPLASH_DELAY)

    }

    override fun onDestroy() {
        App.application.unregisterBusListener(this)
        super.onDestroy()
    }


    private fun startHomeActivity() {
        if (!App.application.database.db.initialized) {
            Logger.e("Database still not initialized.")
            return
        }

        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }


    @Subscribe
    public fun handleDatabaseSetup(event: SetupDatabaseEvent) {
        Logger.d("Database initialized " + (System.currentTimeMillis() - App.application.timeToLaunch))
        startHomeActivity()
    }
}
