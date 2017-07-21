package com.shortstack.hackertracker.Application

import android.app.Application
import android.content.Context
import com.crashlytics.android.Crashlytics
import com.github.stkent.amplify.tracking.Amplify
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Analytics.AnalyticsController
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.Database.DatabaseController
import com.shortstack.hackertracker.Network.NetworkController
import com.shortstack.hackertracker.Utils.NotificationHelper
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil
import com.shortstack.hackertracker.Utils.TimeHelper
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import io.fabric.sdk.android.Fabric
import java.util.*


class App : Application() {

    lateinit var appContext: Context
        private set

    // Eventbus
    val mBus: Bus by lazy { Bus(ThreadEnforcer.MAIN) }
    // Storage
    val mStorage: SharedPreferencesUtil by lazy { SharedPreferencesUtil() }
    // Database
    lateinit var databaseController: DatabaseController
        private set
    // Notifications
    val notificationHelper: NotificationHelper by lazy { NotificationHelper(appContext) }
    // Analytics
    val analyticsController: AnalyticsController by lazy { AnalyticsController() }
    // Networking
    val networkController: NetworkController by lazy { NetworkController(appContext) }
    // Time
    val timeHelper: TimeHelper by lazy { TimeHelper(appContext) }

    val gson : Gson by lazy { GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() }


    override fun onCreate() {
        super.onCreate()

        init()
        initFabric()
        initLogger()
        initFeedback()

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

        databaseController = DatabaseController(appContext)
    }

    private fun initLogger() {
        Logger.init().methodCount(1).hideThreadInfo()
    }

    private fun initFabric() {
        if (!BuildConfig.DEBUG)
            Fabric.with(this, Crashlytics())
    }


    fun postBusEvent(event: Any) {
        mBus.post(event)
    }

    fun unregisterBusListener(itemView: Any) {
        mBus.unregister(itemView)
    }

    fun registerBusListener(itemView: Any) {
        mBus.register(itemView)
    }



    companion object {

        lateinit var application: App

        val storage: SharedPreferencesUtil by lazy { application.mStorage }

        fun getCurrentCalendar() : Calendar = application.timeHelper.currentCalendar

        fun getCurrentDate() : Date = application.timeHelper.currentDate

        fun getRelativeDateStamp( date: Date ) : String = application.timeHelper.getRelativeDateStamp(date)


    }
}