package com.shortstack.hackertracker

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger
import com.github.stkent.amplify.tracking.Amplify
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.di.AppComponent
import com.shortstack.hackertracker.di.DaggerAppComponent
import com.shortstack.hackertracker.di.modules.*
import com.shortstack.hackertracker.network.task.SyncJob
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import io.fabric.sdk.android.Fabric


class App : Application() {

    lateinit var component: AppComponent

    // Storage
    private val storage: SharedPreferencesUtil by lazy { SharedPreferencesUtil(applicationContext) }

    private val dispatcher: FirebaseJobDispatcher by lazy { FirebaseJobDispatcher(GooglePlayDriver(applicationContext)) }


    // TODO: Remove, this is just for measuring launch time.
    var timeToLaunch: Long = System.currentTimeMillis()

    override fun onCreate() {
        super.onCreate()

        application = this

        initFabric()
        initLogger()
        initFeedback()

        component = DaggerAppComponent.builder()
                .sharedPreferencesModule(SharedPreferencesModule())
                .databaseModule(DatabaseModule())
                .gsonModule(GsonModule())
                .analyticsModule(AnalyticsModule())
                .notificationsModule(NotificationsModule())
                .dispatcherModule(DispatcherModule())
                .contextModule(ContextModule(this))
                .build()

        // TODO: Remove, this is only for debugging.
        Logger.d("Time to complete onCreate " + (System.currentTimeMillis() - timeToLaunch))
    }

    fun scheduleSyncTask() {
        cancelPreviousSyncTask()

        if (storage.syncingDisabled)
            return

//        val hourInSeconds = Constants.HOUR_IN_SECONDS
//        val value = storage.syncInterval * hourInSeconds

        val value = 60
        val hourInSeconds = 60

        val job = dispatcher.newJobBuilder()
                .setService(SyncJob::class.java)
                .setTag(SyncJob.TAG)
//                .setRecurring(true)
//                .setLifetime(Lifetime.FOREVER)
//                .setTrigger(Trigger.executionWindow(value, value + hourInSeconds))
                .setTrigger(Trigger.NOW)
                .build()

        dispatcher.mustSchedule(job)
    }

    private fun cancelPreviousSyncTask() {
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
