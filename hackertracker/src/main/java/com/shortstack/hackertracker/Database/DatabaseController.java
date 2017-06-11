package com.shortstack.hackertracker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Event.FavoriteEvent;
import com.shortstack.hackertracker.Model.Company;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.Utils.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DatabaseController {

    private static final String SCHEDULE = "hackertracker.sqlite";
    private static final String VENDORS = "vendors.sqlite";


    private static final int SCHEDULE_VERSION = 210;
    private static final int VENDOR_VERSION = 14;
    public static final String SCHEDULE_TABLE_NAME = "data";

    private SQLiteDatabase mSchedule;
    private SQLiteDatabase mVendors;

    private Gson mGson;

    public DatabaseController(Context context) {
        DatabaseHelper databaseAdapter;

        databaseAdapter = new DatabaseHelper(context, SCHEDULE, SCHEDULE_VERSION);
        mSchedule = databaseAdapter.getWritableDatabase();

        databaseAdapter = new DatabaseHelper(context, VENDORS, VENDOR_VERSION);
        mVendors = databaseAdapter.getWritableDatabase();

        mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

//        Logger.d("Version: " + mSchedule.getVersion());
//        Logger.d("Version: " + mVendors.getVersion());


//        checkBookmark();
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
        App.Companion.getApplication().postBusEvent(new FavoriteEvent(item.getId()));

        if (item.isBookmarked()) {
            App.Companion.getApplication().getNotificationHelper().scheduleItemNotification(item);
        }
    }

    private void setScheduleBookmarked(int state, int id) {
        mSchedule.execSQL("UPDATE data SET starred=" + state + " WHERE id=" + id);
    }

    public void addScheduleItem(Item item) {
        mSchedule.insert(SCHEDULE_TABLE_NAME, null, item.getContentValues(mGson));

        Logger.d("Inserted item.");

        Cursor cursor = mSchedule.rawQuery("SELECT * FROM data", new String[]{});
        int count = cursor.getCount();
        Logger.d("Count now: " + count);


        cursor.moveToFirst();

        do{


        } while(cursor.moveToNext());



        cursor.close();
    }

    public void updateScheduleItem(Item item) {
        String filter = "id=?";
        String[] args = new String[]{String.valueOf(item.getId())};

        SQLiteDatabase schedule = App.Companion.getApplication().getDatabaseController().getSchedule();
        ContentValues values = item.getContentValues(mGson);
        values.put("is_new", "1");

        int rowsUpdated = schedule.update(SCHEDULE_TABLE_NAME, values, filter, args);
        if( rowsUpdated == 0 ) {
            schedule.insert(SCHEDULE_TABLE_NAME, null, values);
            // Change to insert new event.
//            App.Companion.getApplication().postBusEvent(new UpdateListContentsEvent());
        } else {
            // Change to update event.
//            App.Companion.getApplication().postBusEvent(new UpdateListContentsEvent());





            if( item.getId() == 7 ) {
                Logger.d("Updating Cafe Bar.  " + item.isBookmarked());

                checkBookmark();


            }

            //long time = System.currentTimeMillis();

            Item item1 = getScheduleItem(item.getId());

            //Logger.d("Time to fetch: " + (System.currentTimeMillis() - time));

            if( item1.isBookmarked() ) {

                NotificationHelper notificationHelper = App.Companion.getApplication().getNotificationHelper();
                // Cancel the notification, in case the time changes.
                notificationHelper.cancelNotification( item.getId() );

                // Set a new one.
                notificationHelper.scheduleItemNotification(item);

                notificationHelper.postNotification( notificationHelper.getUpdatedItemNotification(item), item.getId());
            }
        }
    }

    public void updateSchedule(List<Item> items) {
        for (Item item : items) {
            updateScheduleItem(item);
        }
    }

    public Item getScheduleItem( int id ) {


        Cursor cursor = mSchedule.query("data", null, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if( cursor.moveToFirst() ) {
            Item item = Item.CursorToItem(mGson, cursor);
            cursor.close();



            return item;
        }

        return new Item();

        //throw new IllegalStateException("Could not find schedule item with id=" + id);
    }

    public void checkBookmark() {
        Cursor cursor = mSchedule.query("data", null, "id=?", new String[]{String.valueOf(14)}, null, null, null, null);

        Logger.d("Cursor length: " + cursor.getCount());
        cursor.moveToFirst();
        Item item1 = Item.CursorToItem(mGson, cursor);

        Logger.d("Item: " + item1.getTitle() + " " + item1.isBookmarked());

        cursor.close();
    }

    public SQLiteDatabase getSchedule() {
        return mSchedule;
    }

    public List<Item> getItemByDate(String... type) throws SQLiteException {
        ArrayList<Item> result = new ArrayList<>();
        ArrayList<String> args = new ArrayList<>(Arrays.asList(type));


        long time;


        // Types
        String selection = "(type=?";
        for (int i = 0; i < type.length - 1; i++) {
            selection = selection.concat(" OR type=?");

        }
        selection = selection.concat(")");

        // Date
        if( App.Companion.getStorage().showActiveEventsOnly()) {
            Calendar currentDate = App.Companion.getCurrentCalendar();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");

            selection = selection.concat("AND (( date = ? AND end > ? )");

            args.add(dateFormat.format(currentDate.getTime()));
            args.add(timeFormat.format(currentDate.getTime()));

            // Next Day
            if( true ) {
                currentDate.roll(Calendar.DAY_OF_YEAR, true);
                // All next day
                selection = selection.concat(" OR date = ?");
                args.add(dateFormat.format(currentDate.getTime()));

                // Any days after it.
                selection = selection.concat(" OR date > ?");
                args.add(dateFormat.format(currentDate.getTime()));

            }

            selection = selection.concat(")");
        }


        // Debugging
        String args_print = "";
        for (String arg : args) {
            args_print = args_print.concat(arg + ", ");
        }

        Logger.d("Selection: " + selection + " Args: " + args_print);
        time = System.currentTimeMillis();


        // Query
        Cursor cursor = mSchedule.query(SCHEDULE_TABLE_NAME, null, selection, args.toArray(new String[args.size()]), null, null, "date, begin");

        Logger.d("Cursor: " + cursor.getCount() + " Took: " + (System.currentTimeMillis() - time));


        time = System.currentTimeMillis();

        // Adding to list
        if( cursor.moveToFirst() ) {

            do {
                 result.add(Item.CursorToItem(mGson, cursor));
            } while (cursor.moveToNext());

        }
        cursor.close();

        Logger.d("Size of Results: " + result.size() + " Took: " + (System.currentTimeMillis() - time));

        return result;
    }


    @NonNull
    private String getQueryString(String[] type) {
        String query = "SELECT * FROM data WHERE";
        if (type.length > 0) {
            query = query.concat(" (");
            for (int i = 0; i < type.length; i++) {
                query = query.concat("type=?");
                if (i < type.length - 1) query = query.concat(" OR ");
            }
            query = query.concat(") OR");
        }
        query = query.concat(" starred=1 ORDER BY date, begin");
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
