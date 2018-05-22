package com.shortstack.hackertracker.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.event.BusProvider
import com.shortstack.hackertracker.event.SetupDatabaseEvent
import com.shortstack.hackertracker.network.service.UpdateDatabaseService
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.splash_activity.*
import java.util.*
import javax.inject.Inject


class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY = 500L
    private var isComplete = false

    @Inject
    lateinit var database: DEFCONDatabaseController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        App.application.myComponent.inject(this)

        val background = when (database.databaseName) {
            Constants.TOORCON_DATABASE_NAME -> R.drawable.tc_19_wallpaper
            Constants.SHMOOCON_DATABASE_NAME -> R.drawable.shmoocon_14_wallpaper
            Constants.HACKWEST_DATABASE_NAME -> R.drawable.hackwest_wallpaper
            Constants.LAYERONE_DATABASE_NAME -> R.drawable.layerone_wallpaper
            Constants.BSIDESORL_DATABASE_NAME -> R.drawable.bsidesorl_wallpaper
            else -> R.drawable.dc_25_wallpaper
        }

        splash_image.setBackgroundResource(background)

        BusProvider.bus.register(this)

        startService(Intent(this@SplashActivity, UpdateDatabaseService::class.java))

        Timer().schedule(object : TimerTask() {
            override fun run() {
                startHomeActivity()
            }
        }, SPLASH_DELAY)

    }

    override fun onDestroy() {
        super.onDestroy()
        BusProvider.bus.unregister(this)
    }


    private fun startHomeActivity() {
        if (!isComplete) {
            isComplete = true
            return
        }

        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }


    @Subscribe
    public fun handleDatabaseSetup(event: SetupDatabaseEvent) {
        startHomeActivity()
    }
}
