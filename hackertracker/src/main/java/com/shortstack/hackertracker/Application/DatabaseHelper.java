package com.shortstack.hackertracker.Application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int BUFFER_SIZE = 1024;
    private static String DB_NAME = "hackertracker.sqlite";
    private static int DB_VERSION = 334;

    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;

        if( isDatabaseEmpty() ) {
            initDatabase();
        }
    }

    private void initDatabase() {
        SQLiteDatabase database = getWritableDatabase();
        database.setLocale(Locale.getDefault());
        database.setVersion(DB_VERSION);

        try {
            copyDatabaseFromAssets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        // Do nothing, database already on device.
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

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

    @NonNull
    private String getDatabasePath() {
        return mContext.getDatabasePath(DB_NAME).getPath();
    }
}
