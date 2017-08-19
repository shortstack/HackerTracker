package com.shortstack.hackertracker.Application

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.crashlytics.android.Crashlytics
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger
import com.github.stkent.amplify.tracking.Amplify
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Analytics.AnalyticsController
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Database.DEFCONDatabaseController
import com.shortstack.hackertracker.Event.MainThreadBus
import com.shortstack.hackertracker.Task.SyncJob
import com.shortstack.hackertracker.Utils.NotificationHelper
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil
import com.shortstack.hackertracker.Utils.TimeHelper
import com.squareup.otto.Bus
import io.fabric.sdk.android.Fabric
import java.util.*


class App : Application() {

    val SECONDS_TO_HOURS = 3600

    lateinit var appContext: Context
        private set

    // Eventbus
    val bus: Bus by lazy { MainThreadBus() }
    // Storage
    val storage: SharedPreferencesUtil by lazy { SharedPreferencesUtil() }
    // Database
    lateinit var databaseController: DEFCONDatabaseController
    // Notifications
    val notificationHelper: NotificationHelper by lazy { NotificationHelper(appContext) }
    // Analytics
    val analyticsController: AnalyticsController by lazy { AnalyticsController() }
    // Time
    val timeHelper: TimeHelper by lazy { TimeHelper(appContext) }

    val gson: Gson by lazy { GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() }

    val dispatcher: FirebaseJobDispatcher by lazy { FirebaseJobDispatcher(GooglePlayDriver(appContext)) }


    override fun onCreate() {
        super.onCreate()

        var time = System.currentTimeMillis()

//        Logger.d("Creating App!")

        init()
//        Logger.d("init -- took " + (System.currentTimeMillis() - time) + " ms.")

        initFabric()
//        Logger.d("init fabric -- took " + (System.currentTimeMillis() - time) + " ms.")
        initLogger()
//        Logger.d("init logger -- took " + (System.currentTimeMillis() - time) + " ms.")
        initFeedback()
//        Logger.d("init feedback -- took " + (System.currentTimeMillis() - time) + " ms.")

        updateDatabaseController()

        if (!storage.isSyncScheduled) {
            storage.setSyncScheduled()
            scheduleSync()
        }
    }


    fun updateDatabaseController() {
        val name = if (storage.databaseSelected == 0) Constants.DEFCON_DATABASE_NAME else Constants.TOORCON_DATABASE_NAME
        Logger.d("Creating database controller with database: $name")
        databaseController = DEFCONDatabaseController(appContext)
        databaseController.checkDatabase()
    }


    fun scheduleSync() {

        cancelSync()

        val hours = PreferenceManager.getDefaultSharedPreferences(this).getString("sync_interval", "6")

        var value = hours.toIntOrNull()

        if (value == null)
            value = 6

        Logger.d("Scheduling the sync. $value")
        if (value == 0) {
            cancelSync()
            return
        }

        value *= SECONDS_TO_HOURS

        val job = dispatcher.newJobBuilder()
                .setService(SyncJob::class.java)
                .setTag(SyncJob.TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(value, value + SECONDS_TO_HOURS))
                .build()

        dispatcher.mustSchedule(job)
    }

    fun cancelSync() {
        Logger.d("Cancelling the sync.")
        dispatcher.cancel(SyncJob.TAG)
    }

    private fun initFeedback() {
        Amplify.initSharedInstance(this)
                .setFeedbackEmailAddress(Constants.FEEDBACK_EMAIL)
                .applyAllDefaultRules()
                .setLastUpdateTimeCooldownDays(1)
    }

    private fun init() {
        application = this
        appContext = applicationContext
    }

    private fun initLogger() {
        Logger.init().methodCount(1).hideThreadInfo()
    }

    private fun initFabric() {
        if (!BuildConfig.DEBUG)
            Fabric.with(this, Crashlytics())
    }


    fun postBusEvent(event: Any) {
        bus.post(event)
    }

    fun unregisterBusListener(itemView: Any) {
        bus.unregister(itemView)
    }

    fun registerBusListener(itemView: Any) {
        bus.register(itemView)
    }


    companion object {

        lateinit var application: App

        fun getCurrentCalendar(): Calendar = application.timeHelper.currentCalendar

        fun getCurrentDate(): Date = application.timeHelper.currentDate

        fun getRelativeDateStamp(date: Date): String = application.timeHelper.getRelativeDateStamp(date)


    }


    object Storage {
        fun getStorage(): SharedPreferencesUtil {
            return SharedPreferencesUtil()
        }
    }

}