package com.shortstack.hackertracker.network.service

import android.app.IntentService
import android.content.Intent
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.event.BusProvider
import com.shortstack.hackertracker.event.SetupDatabaseEvent
import javax.inject.Inject

class UpdateDatabaseService : IntentService("DEFCONUpdateDatabaseService") {

    @Inject
    lateinit var database: DEFCONDatabaseController

    init {
        App.application.myComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        database.checkDatabase()
        BusProvider.bus.post(SetupDatabaseEvent())
    }
}
