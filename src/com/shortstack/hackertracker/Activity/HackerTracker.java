package com.shortstack.hackertracker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Adapter.StarDatabaseAdapter;
import com.shortstack.hackertracker.R;

import java.io.IOException;

public class HackerTracker extends Activity
{

    public DatabaseAdapter myDbHelper;
    public StarDatabaseAdapter myDbHelperStars;

    public void setMainScreen() {

        // button listener for speakers

        Button btnSpeakers = (Button)findViewById(R.id.speakers);
        btnSpeakers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        Speakers.class));

            }
        });

        // button listener for entertainment

        Button btnEntertainment = (Button)findViewById(R.id.entertainment);
        btnEntertainment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        Entertainment.class));

            }
        });

        // button listener for maps

        Button btnMaps = (Button)findViewById(R.id.maps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        Maps.class));

            }
        });

        // button listener for twitter

        Button btnTwitter = (Button)findViewById(R.id.twitter);
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        TwitterFeed.class));

            }
        });

        // button listener for contests

        Button btnContests = (Button)findViewById(R.id.contests);
        btnContests.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        Contests.class));

            }
        });

        // button listener for vendors

        Button btnFaq = (Button)findViewById(R.id.vendors);
        btnFaq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        Vendors.class));

            }
        });

        // set up database

        myDbHelper = new DatabaseAdapter(this);
        myDbHelperStars = new StarDatabaseAdapter(this);

        try {

            myDbHelper.createDataBase();
            myDbHelperStars.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }



    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setMainScreen();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main:
                startActivity(new Intent(HackerTracker.this,
                        HackerTracker.class));
                break;
            case R.id.about:
                startActivity(new Intent(HackerTracker.this,
                        About.class));
                break;
            case R.id.faq:
                startActivity(new Intent(HackerTracker.this,
                        Faq.class));
                break;
            case R.id.starred:
                startActivity(new Intent(HackerTracker.this,
                        Stars.class));
                break;

        }
        return true;
    }


    // get list of dates
    public String getDates(String x) {
        String[] args={x};
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM dates WHERE date=?", args);

        String fullDate = null;

        try{
            if (myCursor.moveToFirst()){
                do{
                    String day = myCursor.getString((myCursor.getColumnIndex("day")));
                    String month = myCursor.getString((myCursor.getColumnIndex("month")));
                    String date = myCursor.getString((myCursor.getColumnIndex("date")));
                    String year = myCursor.getString((myCursor.getColumnIndex("year")));

                    fullDate = day + ", " + month + " " + date;

                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }
        db.close();
        return fullDate;
    }


    // toggle day 1
    public void showListView1(ListView listView){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand1);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
        if(listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            day2Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            listView.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 2
    public void showListView2(ListView listView){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand2);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
         if(listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
             listView.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 3
    public void showListView3(ListView listView){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand3);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
        if(listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            listView.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day2Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 4
    public void showListView4(ListView listView){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand4);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        if(listView.getVisibility() == View.VISIBLE) {
            listView.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            listView.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day2Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

}
