package com.shortstack.hackertracker.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.shortstack.hackertracker.Application.App;

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
public class DatabaseAdapterVendors extends SQLiteOpenHelper {

    private static String DB_NAME = "vendors.sqlite";

    private static int DB_VERSION = 12;

    private SQLiteDatabase myDataBase;

    private final Context myContext;


    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DatabaseAdapterVendors(Context context) {

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
            db.setVersion(DB_VERSION);
            db.setLockingEnabled(true);

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
            String myPath = myContext.getDatabasePath(DB_NAME).getPath();
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            checkDB.setLocale(Locale.getDefault());
            checkDB.setVersion(DB_VERSION);
            checkDB.setLockingEnabled(true);

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
        String outFileName = myContext.getDatabasePath(DB_NAME).getPath();

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
        String myPath = myContext.getDatabasePath(DB_NAME).getPath();
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE);

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

    public static void updateDatabase(HashMap<String, String> queryValues) {

        DatabaseAdapterVendors myDbHelper = new DatabaseAdapterVendors(App.getApplication().getAppContext());

        SQLiteDatabase dbDefault = myDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("id", queryValues.get("id"));
        values.put("title", queryValues.get("title"));
        values.put("description", queryValues.get("description"));
        values.put("image", queryValues.get("image"));
        values.put("link", queryValues.get("link"));

        dbDefault.insert("data", null, values);
        dbDefault.close();

    }
}

