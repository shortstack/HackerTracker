package com.shortstack.hackertracker.Service

import android.app.IntentService
import android.content.Intent
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Event.SetupDatabaseEvent

class UpdateDatabaseService : IntentService("DEFCONUpdateDatabaseService") {

    override fun onHandleIntent(intent : Intent?) {
        val databaseController = App.application.databaseController
        databaseController.checkDatabase()
        App.application.postBusEvent(SetupDatabaseEvent())
    }
}
