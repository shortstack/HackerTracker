package com.shortstack.hackertracker.Service

import android.app.IntentService
import android.content.Intent

class SyncService : IntentService("DEFCONSyncService") {

    override fun onHandleIntent(intent: Intent?) {

//        val retrofit = Retrofit.Builder().baseUrl(Constants.API_URL_BASE).addConverterFactory(GsonConverterFactory.create()).build()
//        val service = retrofit.create(HTService::class.java)
//        var response : Response<SyncResponse>
//        try {
//            response = service.getScheduleFull.execute()
//        }  catch (ex : Exception ) {
//            App.application.postBusEvent(SyncResponseEvent(-1, Constants.SYNC_MDOE_MANUAL))
//            return
//        }
//
//        if (!response.isSuccessful) {
//            App.application.postBusEvent(SyncResponseEvent(-1, Constants.SYNC_MDOE_MANUAL))
//            return
//        }
//
//        val body = response.body()
//
//        val storage = App.storage
//
//        storage.lastRefresh = App.getCurrentDate().time
//
//        var rowsUpdated = 0
//
//
//        if (storage.lastUpdated != body.updatedDate) {
//            storage.lastUpdated = body.updatedDate
//
//            val database = App.application.databaseController
//
//            rowsUpdated = database.updateSchedule(response = body)
//
//            if (rowsUpdated > 0) {
//                App.application.notificationHelper.scheduleUpdateNotification(rowsUpdated)
//            }
//
//        } else {
//            Logger.d("Already up to date!")
//        }
//
//        App.application.postBusEvent(SyncResponseEvent(rowsUpdated, Constants.SYNC_MDOE_MANUAL))
    }
}
