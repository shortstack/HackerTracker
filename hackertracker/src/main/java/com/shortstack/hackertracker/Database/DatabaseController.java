package com.shortstack.hackertracker.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Event.FavoriteEvent;
import com.shortstack.hackertracker.Model.Item;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private SQLiteDatabase mSchedule;

    public DatabaseController(Context context) {
        DatabaseHelper databaseAdapter = new DatabaseHelper(context);
        mSchedule = databaseAdapter.getWritableDatabase();
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mSchedule.close();
    }

    public void toggleBookmark( Item item ) {
        int value = item.isBookmarked() ? Constants.UNBOOKMARKED : Constants.BOOKMARKED;
        setScheduleBookmarked(value, item.getId());

        item.toggleBookmark();
        App.getApplication().postBusEvent(new FavoriteEvent(item.getId()));

        if( item.isBookmarked() ) {
            App.getApplication().scheduleItemNotification(item);
        }
    }

    private void setScheduleBookmarked(int state, int id) {
        mSchedule.execSQL("UPDATE data SET starred=" + state + " WHERE id=" + id);
    }

    public SQLiteDatabase getSchedule() {
        return mSchedule;
    }

    public List<Item> getItemByDate(String ... type) {
        ArrayList<Item> result = new ArrayList<>();

        boolean expiredEvents = App.getStorage().showExpiredEvents();

        Cursor cursor = mSchedule.rawQuery(getQueryString(type), type );

        try{
            if (cursor.moveToFirst()){
                do{
                    Item obj = getDefaultFromCursor(cursor);
                    if( expiredEvents || !obj.hasExpired())
                        result.add(obj);
                }while(cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }

        return result;
    }

    @NonNull
    private String getQueryString(String[] type) {
        String query = "SELECT * FROM data ";
        if( type.length > 0 ) {
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

    public List<Item> getStars() {
        ArrayList<Item> result = new ArrayList<>();

        Cursor cursor = mSchedule.rawQuery("SELECT * FROM data WHERE starred=1 ORDER BY date, begin", new String[]{});

        // get items from database
        try{
            if (cursor.moveToFirst()){
                do{
                    result.add(getDefaultFromCursor(cursor));
                }while(cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }


        // return all items
        return result;
    }


    @NonNull
    private Item getDefaultFromCursor(Cursor cursor) {
        Item item = new Item();

        item.setId(cursor.getInt(cursor.getColumnIndex("id")));
        item.setType(cursor.getString(cursor.getColumnIndex("type")));
        item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        item.setName(cursor.getString(cursor.getColumnIndex("who")));
        item.setDate(cursor.getString(cursor.getColumnIndex("date")));
        item.setEnd(cursor.getString(cursor.getColumnIndex("end")));
        item.setBegin(cursor.getString(cursor.getColumnIndex("begin")));
        item.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        item.setStarred(cursor.getInt(cursor.getColumnIndex("starred")));
        item.setImage(cursor.getString(cursor.getColumnIndex("image")));
        item.setLink(cursor.getString(cursor.getColumnIndex("link")));
        item.setIsNew(cursor.getInt(cursor.getColumnIndex("is_new")));
        item.setDemo(cursor.getInt(cursor.getColumnIndex("demo")));
        item.setTool(cursor.getInt(cursor.getColumnIndex("tool")));
        item.setExploit(cursor.getInt(cursor.getColumnIndex("exploit")));

        return item;
    }
}
