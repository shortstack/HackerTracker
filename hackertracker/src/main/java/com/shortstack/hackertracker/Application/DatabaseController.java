package com.shortstack.hackertracker.Application;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Event.FavoriteEvent;
import com.shortstack.hackertracker.Model.Item;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private SQLiteDatabase mSchedule;

    public DatabaseController(Context context) {
        DatabaseAdapter scheduleAdapter = new DatabaseAdapter(context);
        mSchedule = scheduleAdapter.getWritableDatabase();
    }

    @Override
    protected void finalize() throws Throwable {
        Logger.d("Calling finalize.");
        super.finalize();
        mSchedule.close();
    }

    public void bookmark(Item item) {
        if( item.isBookmarked() ) {
            unbookmark(item);
        } else {


            item.setBookmarked();

            setScheduleBookmarked(Constants.BOOKMARKED, item.getId());

            App.getApplication().postBusEvent(new FavoriteEvent(item.getId()));


            List<Item> stars = getStars();
            Logger.d("Stars: " + stars.size());
        }

    }

    public void unbookmark(Item item) {
        item.setUnbookmarked();

        setScheduleBookmarked(Constants.UNBOOKMARKED, item.getId());

        App.getApplication().postBusEvent(new FavoriteEvent(item.getId()));


        List<Item> stars = getStars();
        Logger.d("Stars: "+ stars.size());
    }

    private void setScheduleBookmarked(int state, int id) {
        String sql = "UPDATE data SET starred=" + state + " WHERE id=" + id;
        Logger.d("Writing: " + sql);
        mSchedule.execSQL(sql);
    }

    public SQLiteDatabase getSchedule() {
        return mSchedule;
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

        //db.close();

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
