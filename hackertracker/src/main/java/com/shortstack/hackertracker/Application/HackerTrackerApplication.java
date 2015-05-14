package com.shortstack.hackertracker.Application;

import android.app.Application;
import android.content.Context;

import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Adapter.OfficialDatabaseAdapter;
import com.shortstack.hackertracker.Adapter.StarDatabaseAdapter;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;

import java.io.IOException;

/**
 * Created by Whitney Champion on 3/19/14.
 */
public class HackerTrackerApplication extends Application {

    private static HackerTrackerApplication application;
    private static Context context;
    public static OfficialDatabaseAdapter myOfficialDbHelper;
    public static DatabaseAdapter myDbHelper;
    public static StarDatabaseAdapter myDbHelperStars;

    public void onCreate(){
        super.onCreate();

        application = this;

        // Assign the context to the Application Scope
        context = getApplicationContext();

        // set up database
        setUpDatabase();

        // set up shared preferences
        SharedPreferencesUtil.getInstance();
    }

    private void setUpDatabase() {

        myOfficialDbHelper = new OfficialDatabaseAdapter(context);
        myDbHelper = new DatabaseAdapter(context);
        myDbHelperStars = new StarDatabaseAdapter(context);

        try {

            myOfficialDbHelper.createDataBase();
            myDbHelper.createDataBase();
            myDbHelperStars.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        myOfficialDbHelper.copyStarred();
        myDbHelper.copyStarred();

    }

    public static Context getAppContext() {
        return HackerTrackerApplication.context;
    }

    public static HackerTrackerApplication getApplication() {
        if (application == null) {
            throw new IllegalStateException("Application not initialized");
        }
        return application;
    }

}