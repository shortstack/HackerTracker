package com.shortstack.hackertracker.network.service

import android.app.IntentService
import android.content.Intent
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.event.SetupDatabaseEvent

class UpdateDatabaseService : IntentService("DEFCONUpdateDatabaseService") {

    override fun onHandleIntent(intent : Intent?) {
        val databaseController = App.application.databaseController
        databaseController.checkDatabase()
        App.application.postBusEvent(SetupDatabaseEvent())
    }
}
