package com.shortstack.hackertracker

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.github.stkent.amplify.feedback.DefaultEmailFeedbackCollector
import com.github.stkent.amplify.feedback.GooglePlayStoreFeedbackCollector
import com.github.stkent.amplify.tracking.Amplify
import com.google.firebase.analytics.FirebaseAnalytics
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.di.appModule
import com.shortstack.hackertracker.utilities.Storage
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : MultiDexApplication() {

    companion object {
        val isDeveloper = BuildConfig.DEBUG

        lateinit var instance: App
    }


    val storage: Storage by inject()
    val database: DatabaseManager by inject()

    override fun onCreate() {
        super.onCreate()
        instance = this

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }

        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)

        initLogger()
        initFeedback()

    }

    private fun initFeedback() {
        Amplify.initSharedInstance(this)
            .setPositiveFeedbackCollectors(GooglePlayStoreFeedbackCollector())
            .setCriticalFeedbackCollectors(DefaultEmailFeedbackCollector(Constants.FEEDBACK_EMAIL))
            .applyAllDefaultRules()
            .setLastUpdateTimeCooldownDays(4)
    }

    private fun initLogger() {
        Logger.init().methodCount(1).hideThreadInfo()
    }
}
