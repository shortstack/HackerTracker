package com.shortstack.hackertracker.Fragment;

/**
 * Created by Whitney Champion on 4/18/14.
 */

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Model.Vendor;

import java.util.ArrayList;
import java.util.List;

public class HackerTrackerFragment extends Fragment {

    private DatabaseAdapter dbHelper;

    public HackerTrackerFragment() {
        // database
        dbHelper = HackerTrackerApplication.dbHelper;
    }

    public List<Vendor> getVendors() {
        ArrayList<Vendor> result = new ArrayList<>();

        SQLiteDatabase db = HackerTrackerApplication.vendorDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM data ORDER BY title", new String[] {});

        try{
            if (cursor.moveToFirst()){
                do{

                    Vendor item = new Vendor();

                    item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                    item.setImage(cursor.getString(cursor.getColumnIndex("image")));
                    item.setLink(cursor.getString(cursor.getColumnIndex("link")));

                    result.add(item);
                }while(cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }
        db.close();

        return result;
    }




    public List<Default> getItemByDate(String ... type) {
        ArrayList<Default> result = new ArrayList<>();
        SQLiteDatabase db = HackerTrackerApplication.dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(getQueryString(type), type );

        try{
            if (cursor.moveToFirst()){
                do{
                    result.add(getDefaultFromCursor(cursor));
                }while(cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }
        db.close();

        return result;
    }

    @NonNull
    private String getQueryString(String[] type) {
        String query = "SELECT * FROM data WHERE (";
        for (int i = 0; i < type.length; i++) {
            query = query.concat("type=?");
            if( i < type.length - 1 ) query = query.concat(" OR ");
        }
        query = query.concat(") ORDER BY date, begin");
        return query;
    }

    public List<Default> getStars() {
        ArrayList<Default> result = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM data WHERE date=? AND starred=1 ORDER BY date, begin", new String[]{});

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

        db.close();

        // return all items
        return result;
    }

    @NonNull
    private Default getDefaultFromCursor(Cursor cursor) {
        Default item = new Default();

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
