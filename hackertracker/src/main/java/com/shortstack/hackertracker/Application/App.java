package com.shortstack.hackertracker.Application;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.github.stkent.amplify.tracking.Amplify;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Analytics.AnalyticsController;
import com.shortstack.hackertracker.BuildConfig;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Database.DatabaseController;
import com.shortstack.hackertracker.Utils.NotificationHelper;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.Date;

import io.fabric.sdk.android.Fabric;


public class App extends Application {

    private static App mApp;
    private Context mContext;


    // Eventbus
    private Bus mBus;
    // Storage
    private SharedPreferencesUtil mStorage;
    // Database
    private DatabaseController mDatabaseController;
    // Notifications
    private NotificationHelper mNotificationHelper;
    // Analytics
    private AnalyticsController mAnalyticsController;


    public long DEBUG_TIME_EXTRA = 0;


    public void onCreate() {
        super.onCreate();

        init();
        initFabric();
        initLogger();
        initDatabase();
        initStorage();
        initAnalytics();
        initNotifications();
        initBus();
        initFeedback();
    }

    private void initBus() {
        mBus = new Bus(ThreadEnforcer.MAIN);
    }

    private void initNotifications() {
        mNotificationHelper = new NotificationHelper(mContext);
    }

    private void initAnalytics() {
        mAnalyticsController = new AnalyticsController();
    }

    private void initStorage() {
        mStorage = new SharedPreferencesUtil();
    }

    private void initFeedback() {
        Amplify.initSharedInstance(this)
                .setFeedbackEmailAddress(Constants.FEEDBACK_EMAIL)
                .applyAllDefaultRules()
                .setLastUpdateTimeCooldownDays(1);
    }

    private void init() {
        mApp = this;
        mContext = getApplicationContext();
    }

    private void initLogger() {
        Logger.init().methodCount(1).hideThreadInfo();
    }

    private void initFabric() {
        if( !BuildConfig.DEBUG )
            Fabric.with(this, new Crashlytics());
    }


    private void initDatabase() {
        mDatabaseController = new DatabaseController(mContext);
    }

    public Context getAppContext() {
        return mContext;
    }

    public static App getApplication() {
        return mApp;
    }

    private SharedPreferencesUtil getPrivateStorage() {
        if( mStorage == null ) {
            mStorage = new SharedPreferencesUtil();
        }
        return mStorage;
    }

    public static SharedPreferencesUtil getStorage() {
        return getApplication().getPrivateStorage();
    }

    public Date getCurrentDate() {
        // TODO: Uncomment when not forcing the time.
//        if (BuildConfig.DEBUG) {
            Date date = new Date();
            date.setTime(Constants.DEBUG_FORCE_TIME_DATE + DEBUG_TIME_EXTRA);
            return date;
//        }
//        return new Date();
    }

    public DatabaseController getDatabaseController() {
        return mDatabaseController;
    }

    public AnalyticsController getAnalyticsController() {
        return mAnalyticsController;
    }

    public void postBusEvent( Object event ) {
        mBus.post(event);
    }

    public void unregisterBusListener(Object itemView) {
        mBus.unregister(itemView);
    }

    public void registerBusListener(Object itemView) {
        mBus.register(itemView);
    }

    public NotificationHelper getNotificationHelper() {
        return mNotificationHelper;
    }
}