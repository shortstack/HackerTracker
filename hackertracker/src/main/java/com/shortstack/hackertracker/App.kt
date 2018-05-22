package com.shortstack.hackertracker

import android.app.Application
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
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.di.DaggerMyComponent
import com.shortstack.hackertracker.di.MyComponent
import com.shortstack.hackertracker.di.modules.*
import com.shortstack.hackertracker.network.task.SyncJob
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import com.shortstack.hackertracker.utils.TimeHelper
import io.fabric.sdk.android.Fabric
import java.util.*


class App : Application() {

    lateinit var myComponent: MyComponent

    // Storage
    @Deprecated(message = "Use DI")
    val storage: SharedPreferencesUtil by lazy { SharedPreferencesUtil(applicationContext) }
    // Database
    @Deprecated("use DI")
    lateinit var databaseController: DEFCONDatabaseController
    // Notifications
    @Deprecated("use DI")
    val notificationHelper: NotificationHelper by lazy { NotificationHelper(applicationContext) }
    // Time
    @Deprecated("use DI")
    val timeHelper: TimeHelper by lazy { TimeHelper(applicationContext) }

    @Deprecated("use DI")
    val gson: Gson by lazy { GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() }

    @Deprecated("use DI")
    val dispatcher: FirebaseJobDispatcher by lazy { FirebaseJobDispatcher(GooglePlayDriver(applicationContext)) }


    override fun onCreate() {
        super.onCreate()

        application = this

        initFabric()
        initLogger()
        initFeedback()

        updateDatabaseController()

        if (!storage.isSyncScheduled) {
            storage.setSyncScheduled()
            scheduleSync()
        }

        myComponent = DaggerMyComponent.builder()
                .sharedPreferencesModule(SharedPreferencesModule())
                .databaseModule(DatabaseModule())
                .analyticsModule(AnalyticsModule())
                .notificationsModule(NotificationsModule())
                .contextModule(ContextModule(this))
                .build()
    }

    fun updateDatabaseController() {
        val name = if (storage.databaseSelected == 0) Constants.DEFCON_DATABASE_NAME
        else if (storage.databaseSelected == 1)
            Constants.TOORCON_DATABASE_NAME
        else if (storage.databaseSelected == 2)
            Constants.SHMOOCON_DATABASE_NAME
        else if (storage.databaseSelected == 3)
            Constants.HACKWEST_DATABASE_NAME
        else if (storage.databaseSelected == 4)
            Constants.LAYERONE_DATABASE_NAME
        else
            Constants.BSIDESORL_DATABASE_NAME

        setTheme(if (storage.databaseSelected == 0)
            R.style.AppTheme
        else if (storage.databaseSelected == 1)
            R.style.AppTheme_Toorcon
        else if (storage.databaseSelected == 2)
            R.style.AppTheme_Shmoocon
        else if (storage.databaseSelected == 3)
            R.style.AppTheme_Hackwest
        else if (storage.databaseSelected == 4)
            R.style.AppTheme_LayerOne
        else
            R.style.AppTheme_BsidesOrl)

        Logger.d("Creating database controller with database: $name")
        databaseController = DEFCONDatabaseController(applicationContext, name = name)

        if (databaseController.exists()) {
            databaseController.checkDatabase()
        }
    }


    fun scheduleSync() {

        cancelSync()

        var value = storage.syncInterval

        if (value == 0) {
            cancelSync()
            return
        }

        value *= Constants.TIME_SECONDS_IN_HOUR

        val job = dispatcher.newJobBuilder()
                .setService(SyncJob::class.java)
                .setTag(SyncJob.TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(value, value + Constants.TIME_SECONDS_IN_HOUR))
                .build()

        dispatcher.mustSchedule(job)
    }

    private fun cancelSync() {
        dispatcher.cancel(SyncJob.TAG)
    }

    private fun initFeedback() {
        Amplify.initSharedInstance(this)
                .setFeedbackEmailAddress(Constants.FEEDBACK_EMAIL)
                .applyAllDefaultRules()
                .setLastUpdateTimeCooldownDays(1)
    }

    private fun initLogger() {
        Logger.init().methodCount(1).hideThreadInfo()
    }

    private fun initFabric() {
        if (!BuildConfig.DEBUG)
            Fabric.with(this, Crashlytics())
    }

    companion object {

        lateinit var application: App

        @Deprecated(message = "Use DI to get a handle to TimeHelper instead.")
        fun getCurrentCalendar(): Calendar = application.timeHelper.currentCalendar

        @Deprecated(message = "Use DI to get a handle to TimeHelper instead.")
        fun getCurrentDate(): Date = application.timeHelper.currentDate

        @Deprecated(message = "Use DI to get a handle to TimeHelper instead.")
        fun getRelativeDateStamp(date: Date): String = application.timeHelper.getRelativeDateStamp(date)
    }
}
