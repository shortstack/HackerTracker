package com.shortstack.hackertracker.network.service

import android.app.IntentService
import android.content.Intent
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.database.DatabaseManager
import javax.inject.Inject

class UpdateDatabaseService : IntentService("DEFCONUpdateDatabaseService") {

    @Inject
    lateinit var database: DatabaseManager

    init {
        App.application.myComponent.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        // TODO: Check the database
//        database.checkDatabase()
//        BusProvider.bus.post(SetupDatabaseEvent())
    }
}
