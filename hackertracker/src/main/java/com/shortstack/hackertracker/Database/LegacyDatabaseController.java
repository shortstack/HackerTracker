package com.shortstack.hackertracker.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Item;

import java.io.File;
import java.util.ArrayList;

public class LegacyDatabaseController extends SQLiteOpenHelper {

    private static final String SCHEDULE_DB_NAME = "database.db";
    private static final String VENDORS_DB_NAME = "vendors.sqlite";
    private static final int DB_VERSION = 212;
    private final Context mContext;


    public LegacyDatabaseController(Context context) {
        super(context, SCHEDULE_DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    public Item[] getBookmarkedItems() {
        ArrayList<Item> result = new ArrayList<>();

        SQLiteDatabase readableDatabase = getReadableDatabase();

        Cursor cursor = readableDatabase.rawQuery("SELECT * from Schedule WHERE bookmarked==1", new String[]{});

        if (cursor.moveToFirst()) {
            do {
                Item e = Item.CursorToItem(App.application.getGson(), cursor);
                e.toggleBookmark();
                result.add(e);
            } while (cursor.moveToNext());
        }

        return result.toArray(new Item[result.size()]);
    }

    public void deleteDatabase() {
        close();
        mContext.deleteDatabase(SCHEDULE_DB_NAME);
        mContext.deleteDatabase(VENDORS_DB_NAME);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static boolean exists(Context context ) {
        File dbFile = context.getDatabasePath(SCHEDULE_DB_NAME);
        return dbFile.exists();
    }
}
