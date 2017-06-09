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
import com.shortstack.hackertracker.Network.NetworkController;
import com.shortstack.hackertracker.Utils.NotificationHelper;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;
import com.shortstack.hackertracker.Utils.TimeHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

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
    // Networking
    private NetworkController mNetworkController;
    // Time
    private TimeHelper mTimeHelper;




    public void onCreate() {
        super.onCreate();

        init();
        initFabric();
        initTime();
        initLogger();
        initDatabase();
        initStorage();
        initAnalytics();
        initNetwork();
        initNotifications();
        initBus();
        initFeedback();
    }

    private void initTime() {
        mTimeHelper = new TimeHelper(mContext);
    }

    private void initNetwork() {
        mNetworkController = new NetworkController(mContext);
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

    public TimeHelper getTimeHelper() {
        return mTimeHelper;
    }

    public DatabaseController getDatabaseController() {
        return mDatabaseController;
    }

    public AnalyticsController getAnalyticsController() {
        return mAnalyticsController;
    }

    public NetworkController getNetworkController() {
        return mNetworkController;
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