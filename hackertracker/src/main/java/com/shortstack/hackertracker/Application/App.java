package com.shortstack.hackertracker.Application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Adapter.DatabaseAdapterVendors;
import com.shortstack.hackertracker.BuildConfig;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.List.GenericRowFragment;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.AlarmReceiver;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.io.IOException;
import java.util.Date;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Whitney Champion on 3/19/14.
 */
public class App extends Application {

    private static App application;
    private static Context context;
    //public static DatabaseAdapter dbHelper;
    public static DatabaseAdapterVendors vendorDbHelper;
    private static AlarmManager alarmManager;
    private BroadcastReceiver receiver;
    private PendingIntent pendingIntent;
    private SharedPreferencesUtil storage;


    private static DatabaseController mDatabaseController;


    private static Bus bus = new Bus();

    public void onCreate() {
        super.onCreate();
        
        if( !BuildConfig.DEBUG )
            Fabric.with(this, new Crashlytics());

        Logger.init().methodCount(1).hideThreadInfo();

        application = this;

        // Assign the context to the Application Scope
        context = getApplicationContext();

        // set up database
        setUpDatabase();

        // set up shared preferences
        storage = new SharedPreferencesUtil();

        // register alarm broadcast
        if (storage.allowPushNotifications()) {
            RegisterAlarmBroadcast();
        }

        bus = new Bus(ThreadEnforcer.MAIN);


    }

    private void RegisterAlarmBroadcast() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }

        };

        // register the alarm broadcast
        registerReceiver(receiver, new IntentFilter(getPackageName()));
        pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(getPackageName()), 0);
        alarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));

    }

    private void UnregisterAlarmBroadcast() {
        alarmManager.cancel(pendingIntent);
        getBaseContext().unregisterReceiver(receiver);
    }

    public static void cancelNotification(int id) {

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();

        alarmManager.cancel(pendingIntent);
    }

    public static void scheduleNotification(Notification notification, long when, int id) {

        Intent notificationIntent = new Intent(context, AlarmReceiver.class);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION_ID, id);
        notificationIntent.putExtra(AlarmReceiver.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
    }

    public static Notification createNotification(Context context, Item content) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int color = context.getResources().getColor(R.color.colorPrimary);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(content.getTitle());
        builder.setContentText(String.format(context.getString(R.string.notification_text), content.getLocation()));
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setSound(soundUri);
        builder.setVibrate(new long[] { 0, 250, 500, 250 });
        builder.setLights(Color.MAGENTA, 3000, 1000);

        builder.setSmallIcon(R.drawable.skull);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            builder.setColor(color);
        }
        builder.setAutoCancel(true);

        return builder.build();
    }

    private void setUpDatabase() {

        mDatabaseController = new DatabaseController(context);

        vendorDbHelper = new DatabaseAdapterVendors(context);

        try {
            vendorDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static App getApplication() {
        if (application == null) {
            throw new IllegalStateException("Application not initialized");
        }
        return application;
    }

    private SharedPreferencesUtil getPrivateStorage() {
        if( storage == null ) {
            storage = new SharedPreferencesUtil();
        }
        return storage;
    }

    public static SharedPreferencesUtil getStorage() {
        return getApplication().getPrivateStorage();
    }

    public Date getCurrentDate() {
        if (BuildConfig.DEBUG) {
            Date date = new Date();
            date.setTime(Constants.DEBUG_FORCE_TIME_DATE);
            return date;
        }
        return new Date();
    }

    public DatabaseController getDatabaseController() {
        return mDatabaseController;
    }

    public void registerBusListener(GenericRowFragment context ) {
        bus.register(context);
    }

    public void unregisterBusListener( GenericRowFragment context ) {
        bus.unregister(context);
    }

    public void postBusEvent( Object event ) {
        bus.post(event);
    }
}