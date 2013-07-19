package com.shortstack.hackertracker.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.shortstack.hackertracker.Adapter.StarAdapter;
import com.shortstack.hackertracker.Model.Event;
import com.shortstack.hackertracker.Model.Star;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/18/13
 * Time: 10:13 PM
 */
public class Stars extends HackerTracker {

    public Star[] listOfStars;
    public StarAdapter adapter;
    public ListView stars;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stars);

        //set up textviews for dates
        TextView day1 = (TextView)findViewById(R.id.day1);
        TextView day2 = (TextView)findViewById(R.id.day2);
        TextView day3 = (TextView)findViewById(R.id.day3);
        TextView day4 = (TextView)findViewById(R.id.day4);

        // set up layouts for dates
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);

        // button listeners for textviews
        day1Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView1((ListView) findViewById(R.id.stars_day1));
            }
        });
        day2Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView2((ListView) findViewById(R.id.stars_day2));
            }
        });
        day3Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView3((ListView) findViewById(R.id.stars_day3));
            }
        });
        day4Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView4((ListView) findViewById(R.id.stars_day4));
            }
        });

        // put dates into textviews
        day1.setText(getDates("1"));
        day2.setText(getDates("2"));
        day3.setText(getDates("3"));
        day4.setText(getDates("4"));

        //query database for stars
        SQLiteDatabase dbStars = myDbHelper.getReadableDatabase();

        // populate day 1
        List<Star> stars1 = getStarsByDate("1");
        if (!(stars1.size() < 1)) {

            listOfStars = stars1.toArray(new Star[stars1.size()]);

            adapter = new StarAdapter(this, R.layout.star_row, listOfStars);

            stars = (ListView) findViewById(R.id.stars_day1);

            stars.setAdapter(adapter);
        }

        // populate day 2
        List<Star> stars2 = getStarsByDate("2");
        if (!(stars2.size() < 1)) {

            listOfStars = stars2.toArray(new Star[stars2.size()]);

            adapter = new StarAdapter(this, R.layout.star_row, listOfStars);

            stars = (ListView) findViewById(R.id.stars_day2);

            stars.setAdapter(adapter);
        }

        // populate day 3
        List<Star> stars3 = getStarsByDate("3");
        if (!(stars3.size() < 1)) {

            listOfStars = stars3.toArray(new Star[stars3.size()]);

            adapter = new StarAdapter(this, R.layout.star_row, listOfStars);

            stars = (ListView) findViewById(R.id.stars_day3);

            stars.setAdapter(adapter);
        }

        // populate day 4
        List<Star> stars4 = getStarsByDate("4");
        if (!(stars4.size() < 1)) {

            listOfStars = stars4.toArray(new Star[stars4.size()]);

            adapter = new StarAdapter(this, R.layout.star_row, listOfStars);

            stars = (ListView) findViewById(R.id.stars_day4);

            stars.setAdapter(adapter);

            stars.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View view, int position, long id) {


                    registerForContextMenu(stars);
                    view.showContextMenu();

                    return false;
                }
            });
        }

        // close databases
        dbStars.close();



    }


    // get list of events by the day
    public List<Star> getStarsByDate(String day) {
        String[] args={day};
        ArrayList<Star> result = new ArrayList<Star>();
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM stars WHERE date=? ORDER BY startTime", args);

        try{
            if (myCursor.moveToFirst()){
                do{
                    Star star = new Star();
                    star.setId(myCursor.getString((myCursor.getColumnIndex("_id"))));
                    star.setTitle(myCursor.getString((myCursor.getColumnIndex("title"))));
                    star.setBody(myCursor.getString((myCursor.getColumnIndex("body"))));
                    star.setDate(myCursor.getString((myCursor.getColumnIndex("date"))));
                    star.setEndTime(myCursor.getString((myCursor.getColumnIndex("endTime"))));
                    star.setStartTime(myCursor.getString((myCursor.getColumnIndex("startTime"))));
                    star.setLocation(myCursor.getString((myCursor.getColumnIndex("location"))));
                    star.setStarred(myCursor.getInt((myCursor.getColumnIndex("starred"))));

                    result.add(star);
                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }
        db.close();
        return result;
    }


}
