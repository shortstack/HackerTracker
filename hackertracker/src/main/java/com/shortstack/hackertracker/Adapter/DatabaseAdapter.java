package com.shortstack.hackertracker.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.shortstack.hackertracker.Application.HackerTrackerApplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 8/29/12
 * Time: 5:52 PM
 */
public class DatabaseAdapter extends SQLiteOpenHelper {

    private static String DB_PATH = "/data/data/com.shortstack.hackertracker/databases/";
    private static String DB_NAME = "hackertracker.sqlite";
    private static int DB_VERSION = 211;

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    public DatabaseAdapter(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    /**
     * creates sqlite database
     * */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if (!dbExist) {

            // create database
            SQLiteDatabase db = this.getWritableDatabase();
            db.setLocale(Locale.getDefault());
            db.setLockingEnabled(true);
            db.setVersion(DB_VERSION);

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

        // TODO: remove this on next version
        copyDataBase();

        this.close();

    }

    /**
     * check if database exists
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            checkDB.setLocale(Locale.getDefault());
            checkDB.setLockingEnabled(true);
            checkDB.setVersion(DB_VERSION);

        } catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * copy database from assets to device
     * */
    private void copyDataBase() throws IOException{

        // get db from assets
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // path of db to create
        String outFileName = DB_PATH + DB_NAME;

        // initialize output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        // close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException{

        // open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            copyDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void copyStarred() {

        // check if entry is already in starred database
        DatabaseAdapterStarred myDbHelperStars = new DatabaseAdapterStarred(HackerTrackerApplication.getAppContext());
        DatabaseAdapter myOfficialDbHelper = new DatabaseAdapter(HackerTrackerApplication.getAppContext());

        SQLiteDatabase dbStars = myDbHelperStars.getWritableDatabase();
        SQLiteDatabase dbDefault = myOfficialDbHelper.getWritableDatabase();

        Cursor myCursor = dbStars.rawQuery("SELECT * FROM data", null);
        try{
            if (myCursor.moveToFirst()){
                do{

                    dbDefault.execSQL("UPDATE data SET starred=" + 1 + " WHERE id=" + myCursor.getInt(myCursor.getColumnIndex("id")));

                } while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }
        dbStars.close();
        dbDefault.close();

    }

    public static void updateDatabase(HashMap<String, String> queryValues) {

        DatabaseAdapter myDbHelper = new DatabaseAdapter(HackerTrackerApplication.getAppContext());

        // open database
        SQLiteDatabase dbDefault = myDbHelper.getWritableDatabase();

        // create set of all values
        ContentValues values = new ContentValues();
        values.put("id", queryValues.get("id"));
        values.put("title", queryValues.get("title"));
        values.put("who", queryValues.get("who"));
        values.put("location", queryValues.get("location"));
        values.put("begin", queryValues.get("begin"));
        values.put("end", queryValues.get("end"));
        values.put("date", queryValues.get("date"));
        values.put("description", queryValues.get("description"));
        values.put("type", queryValues.get("type"));
        values.put("link", queryValues.get("link"));
        values.put("demo", queryValues.get("demo"));
        values.put("tool", queryValues.get("tool"));
        values.put("exploit", queryValues.get("exploit"));

        // check if previously starred
        Cursor c = dbDefault.rawQuery("SELECT starred FROM data WHERE id="+queryValues.get("id"),null);
        if (c.moveToFirst())
            values.put("starred", c.getString(0));
        else
            values.put("starred", "0");

        // close cursor
        c.close();

        // insert new record or replace if already exists
        dbDefault.insertWithOnConflict("data", null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // close database
        dbDefault.close();

    }
}

