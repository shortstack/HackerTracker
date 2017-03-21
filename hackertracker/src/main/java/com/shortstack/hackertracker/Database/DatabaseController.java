package com.shortstack.hackertracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Event.FavoriteEvent;
import com.shortstack.hackertracker.Model.Company;
import com.shortstack.hackertracker.Model.Item;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private static final String SCHEDULE = "hackertracker.sqlite";
    private static final String VENDORS = "vendors.sqlite";

    private static final int SCHEDULE_VERSION = 340;
    private static final int VENDOR_VERSION = 14;

    private SQLiteDatabase mSchedule;
    private SQLiteDatabase mVendors;

    private Gson mGson;

    public DatabaseController(Context context) {
        DatabaseHelper databaseAdapter = new DatabaseHelper(context, SCHEDULE, SCHEDULE_VERSION);
        mSchedule = databaseAdapter.getWritableDatabase();

        databaseAdapter = new DatabaseHelper(context, VENDORS, VENDOR_VERSION);
        mVendors = databaseAdapter.getWritableDatabase();

        mGson = new GsonBuilder().create();

        Logger.d("Version: " + mSchedule.getVersion());
        Logger.d("Version: " + mVendors.getVersion());
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mSchedule.close();
        mVendors.close();
    }

    public void toggleBookmark(Item item) {
        int value = item.isBookmarked() ? Constants.UNBOOKMARKED : Constants.BOOKMARKED;
        setScheduleBookmarked(value, item.getId());

        item.toggleBookmark();
        App.getApplication().postBusEvent(new FavoriteEvent(item.getId()));

        if (item.isBookmarked()) {
            App.getApplication().getNotificationHelper().scheduleItemNotification(item);
        }
    }

    private void setScheduleBookmarked(int state, int id) {
        mSchedule.execSQL("UPDATE data SET starred=" + state + " WHERE id=" + id);
    }

    public void addScheduleItem( Item item ) {


        ContentValues values = new ContentValues();
        values.put("title", "Hello");
        values.put("who", "Everyone");
        values.put("begin", "10:00");
        values.put("date", "2016-08-05");
        values.put("description", "asdasdalsd");
        values.put("type", "Party");

        mSchedule.insert("data", null, values);
    }

    public SQLiteDatabase getSchedule() {
        return mSchedule;
    }

    public List<Item> getItemByDate(String... type) throws SQLiteException {
        ArrayList<Item> result = new ArrayList<>();
        Item item;


        boolean expiredEvents = App.getStorage().showExpiredEvents();

        Cursor cursor = mSchedule.rawQuery(getQueryString(type), type);

        JSONObject rowObject;

        cursor.moveToFirst();
        do {
            rowObject = new JSONObject();

            int totalColumn = cursor.getColumnCount();

            for (int i = 0; i < totalColumn; i++) {
                try {
                    rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                } catch (Exception e) {
                    Logger.e(e, "Cursor to JSON failed.");
                }
            }

            item = mGson.fromJson(rowObject.toString(), Item.class);
            if (expiredEvents || !item.hasExpired())
                result.add(item);

        } while (cursor.moveToNext());

        cursor.close();

        //result = result.subList(0, 1);


        Logger.d("Size of Results: " + result.size());


        return result;
    }



    @NonNull
    private String getQueryString(String[] type) {
        String query = "SELECT * FROM data ";
        if (type.length > 0) {
            query = query.concat("WHERE (");
            for (int i = 0; i < type.length; i++) {
                query = query.concat("type=?");
                if (i < type.length - 1) query = query.concat(" OR ");
            }
            query = query.concat(")");
        }
        query = query.concat(" OR starred=1 ORDER BY date, begin");
        return query;
    }

    public List<Company> getVendors() {
        ArrayList<Company> result = new ArrayList<>();

        Cursor cursor = mVendors.rawQuery("SELECT * FROM data", new String[]{});

        try {
            if (cursor.moveToFirst()) {
                do {

                    Company item = new Company();

                    item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                    item.setLink(cursor.getString(cursor.getColumnIndex("link")));
                    item.setPartner(cursor.getInt(cursor.getColumnIndex("partner")));

                    result.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return result;
    }
}
