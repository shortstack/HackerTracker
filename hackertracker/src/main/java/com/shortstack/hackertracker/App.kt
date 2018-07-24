package com.shortstack.hackertracker

import android.app.Application
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.crashlytics.android.Crashlytics
import com.github.stkent.amplify.feedback.DefaultEmailFeedbackCollector
import com.github.stkent.amplify.feedback.GooglePlayStoreFeedbackCollector
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
                .timerModule(TimerModule())
                .contextModule(ContextModule(this))
                .build()
    }

    fun scheduleSyncTask() {
        WorkManager.getInstance()?.cancelAllWorkByTag(SyncWorker.TAG_SYNC)

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

        WorkManager.getInstance()?.enqueue(request)
    }

    private fun initFeedback() {
        Amplify.initSharedInstance(this)
                .setPositiveFeedbackCollectors(GooglePlayStoreFeedbackCollector())
                .setCriticalFeedbackCollectors(DefaultEmailFeedbackCollector(Constants.FEEDBACK_EMAIL))
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
