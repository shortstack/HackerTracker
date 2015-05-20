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
public class DatabaseAdapterOfficial extends SQLiteOpenHelper {

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.shortstack.hackertracker/databases/";

    private static String DB_NAME = "hackertracker.sqlite";

    private static int DB_VERSION = 191;

    private SQLiteDatabase myDataBase;

    private final Context myContext;


    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseAdapterOfficial(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{

        boolean dbExist = checkDataBase();

        if (!dbExist) {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
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

        this.close();

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
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
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException{

        //Open the database
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    public static void copyStarred() {

        // check if entry is already in starred database
        DatabaseAdapterStarred myDbHelperStars = new DatabaseAdapterStarred(HackerTrackerApplication.getAppContext());
        DatabaseAdapterOfficial myOfficialDbHelper = new DatabaseAdapterOfficial(HackerTrackerApplication.getAppContext());

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

        DatabaseAdapterOfficial myDbHelper = new DatabaseAdapterOfficial(HackerTrackerApplication.getAppContext());

        SQLiteDatabase dbDefault = myDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("id", queryValues.get("id"));
        values.put("title", queryValues.get("title"));
        values.put("name", queryValues.get("name"));
        values.put("begin", queryValues.get("begin"));
        values.put("end", queryValues.get("end"));
        values.put("date", queryValues.get("date"));
        values.put("where", queryValues.get("where"));
        values.put("body", queryValues.get("body"));
        values.put("type", queryValues.get("type"));
        values.put("starred", queryValues.get("starred"));
        values.put("image", queryValues.get("image"));
        values.put("forum", queryValues.get("forum"));
        values.put("is_new", queryValues.get("is_new"));
        values.put("demo", queryValues.get("demo"));
        values.put("tool", queryValues.get("tool"));
        values.put("exploit", queryValues.get("exploit"));

        dbDefault.insert("data", null, values);
        dbDefault.close();

    }
}

