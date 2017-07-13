package com.shortstack.hackertracker.Database;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DatabaseHelper extends SQLiteAssetHelper {

    private static final int BUFFER_SIZE = 1024;
    private final Context mContext;
    private final String DB_NAME;
    private final int DB_VERSION;

    DatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
        mContext = context;
        DB_NAME = name;
        DB_VERSION = version;

        setForcedUpgrade();

//        if (isDatabaseEmpty())
//            initDatabase();

    }


    protected void initDatabase() {
        //SQLiteDatabase database = getWritableDatabase();
        //database.setLocale(Locale.getDefault());
        //database.setVersion(DB_VERSION);

//        Logger.d("Creating DB " + DB_NAME);

        try {
            Logger.d("Trying to copy " + DB_NAME.toUpperCase() + ".");
            copyDatabaseFromAssets();
        } catch (IOException e) {
            Logger.e(/*e.getCause(),*/ "Unable to copy " + DB_NAME.toUpperCase() + " from assets. " + e.getMessage());
        } finally {
            Logger.d("Finished copying " + DB_NAME.toUpperCase() + ".");
        }
    }


    private void copyDatabaseFromAssets() throws IOException {

        String databasePath = getDatabasePath();
        Logger.d("Path: " + databasePath);

        InputStream input = openDatabaseFromAssets();
        OutputStream output = new FileOutputStream(getDatabasePath());

        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        while ((length = input.read(buffer)) > 0) {
            output.write(buffer, 0, length);
        }

        output.flush();
        output.close();
        input.close();
    }

    private boolean isDatabaseEmpty() {
        return !isDatabaseAvailable();
    }

    private boolean isDatabaseAvailable() {
        File dbFile = mContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    private InputStream openDatabaseFromAssets() throws IOException {
        return mContext.getAssets().open(DB_NAME);
    }

    private String getDatabasePath() {
        return mContext.getDatabasePath(DB_NAME).getPath();
    }

}
