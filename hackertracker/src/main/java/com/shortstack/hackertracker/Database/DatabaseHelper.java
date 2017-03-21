package com.shortstack.hackertracker.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class DatabaseHelper extends SQLiteOpenHelper {

    private static final int BUFFER_SIZE = 1024;
    private final Context mContext;
    private final String DB_NAME;
    private final int DB_VERSION;

    DatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
        mContext = context;
        DB_NAME = name;
        DB_VERSION = version;

        if( isDatabaseEmpty() ) {
            initDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // TODO: Create the local database.
        Logger.d("onCreate database -> " + DB_NAME + ": " + DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // TODO: Update the local database, instead of just overriding it.
        Logger.d("onUpgrade database -> " + DB_NAME + ": " + oldVersion + " to " + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.d("onDowngrade database -> " + DB_NAME + ": " + oldVersion + " to " + newVersion);
        //super.onDowngrade(db, oldVersion, newVersion);
    }

    protected void initDatabase() {
        //SQLiteDatabase database = getWritableDatabase();
        //database.setLocale(Locale.getDefault());
        //database.setVersion(DB_VERSION);

        Logger.d("Creating DB " + DB_NAME);

        try {
            copyDatabaseFromAssets();
        } catch (IOException e) {
            Logger.e(e.getCause(), "Unable to copy database from assets.");
        }
    }


    private void copyDatabaseFromAssets() throws IOException {

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
