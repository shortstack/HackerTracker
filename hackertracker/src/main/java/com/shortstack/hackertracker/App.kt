package com.shortstack.hackertracker

import android.app.Application
import androidx.work.*
import com.crashlytics.android.Crashlytics
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.github.stkent.amplify.tracking.Amplify
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.di.AppComponent
import com.shortstack.hackertracker.di.DaggerAppComponent
import com.shortstack.hackertracker.di.modules.*
import com.shortstack.hackertracker.network.task.SyncWorker
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import io.fabric.sdk.android.Fabric
import java.util.concurrent.TimeUnit


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
        WorkManager.getInstance().cancelAllWorkByTag(SyncWorker.TAG_SYNC)

        if (storage.syncingDisabled)
            return

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        val request =
                PeriodicWorkRequestBuilder<SyncWorker>(7, TimeUnit.DAYS)
                        .addTag(SyncWorker.TAG_SYNC)
                        .setConstraints(constraints)
                        .build()

        WorkManager.getInstance().enqueue(request)
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
