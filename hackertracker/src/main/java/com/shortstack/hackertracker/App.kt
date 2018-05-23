package com.shortstack.hackertracker

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger
import com.github.stkent.amplify.tracking.Amplify
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.di.DaggerMyComponent
import com.shortstack.hackertracker.di.MyComponent
import com.shortstack.hackertracker.di.modules.*
import com.shortstack.hackertracker.network.task.SyncJob
import com.shortstack.hackertracker.utils.NotificationHelper
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import com.shortstack.hackertracker.utils.TimeUtil
import io.fabric.sdk.android.Fabric
import java.util.*


class App : Application() {

    lateinit var myComponent: MyComponent

    // Storage
    @Deprecated(message = "Use DI")
    val storage: SharedPreferencesUtil by lazy { SharedPreferencesUtil(applicationContext) }
    // Database
    @Deprecated("use DI")
    private
    lateinit var databaseController: DEFCONDatabaseController

    @Deprecated("use DI")
    private val dispatcher: FirebaseJobDispatcher by lazy { FirebaseJobDispatcher(GooglePlayDriver(applicationContext)) }

    // TODO: Remove, this is just for measuring launch time.
    var timeToLaunch : Long = System.currentTimeMillis()

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
                .dispatcherModule(DispatcherModule())
                .contextModule(ContextModule(this))
                .build()

        // TODO: Remove, this is only for debugging.
        Logger.d("Time to complete onCreate " + (System.currentTimeMillis() - timeToLaunch))
    }

    fun updateDatabaseController() {
        val name = Constants.DEFCON_DATABASE_NAME

        Logger.d("Creating database controller with database: $name")
        databaseController = DEFCONDatabaseController(applicationContext, SharedPreferencesUtil(this), name = name)

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
    }
}
