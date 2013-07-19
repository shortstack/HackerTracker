package com.shortstack.hackertracker.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.shortstack.hackertracker.Adapter.EventAdapter;
import com.shortstack.hackertracker.Model.Event;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 8/29/12
 * Time: 2:26 PM
 */
public class Entertainment extends HackerTracker {

    public Event[] eventData;
    public EventAdapter adapter;
    public ListView events;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entertainment);

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
                showListView1((ListView) findViewById(R.id.entertainment_day1));
            }
        });
        day2Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView2((ListView) findViewById(R.id.entertainment_day2));
            }
        });
        day3Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView3((ListView) findViewById(R.id.entertainment_day3));
            }
        });
        day4Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView4((ListView) findViewById(R.id.entertainment_day4));
            }
        });

        // put dates into textviews
        day1.setText(getDates("1"));
        day2.setText(getDates("2"));
        day3.setText(getDates("3"));
        day4.setText(getDates("4"));

        //query database for events
        SQLiteDatabase dbSpeakers = myDbHelper.getReadableDatabase();

        // populate day 1
        List<Event> events = getEventsByDate("1");
        if (!(events.size() < 1)) {

            eventData = events.toArray(new Event[events.size()]);

            adapter = new EventAdapter(this, R.layout.entertainment_row, eventData);

            this.events = (ListView) findViewById(R.id.entertainment_day1);

            this.events.setAdapter(adapter);
        }

        // populate day 2
        List<Event> events2 = getEventsByDate("2");
        if (!(events2.size() < 1)) {

            eventData = events2.toArray(new Event[events2.size()]);

            adapter = new EventAdapter(this, R.layout.entertainment_row, eventData);

            this.events = (ListView) findViewById(R.id.entertainment_day2);

            this.events.setAdapter(adapter);
        }

        // populate day 3
        List<Event> events3 = getEventsByDate("3");
        if (!(events3.size() < 1)) {

            eventData = events3.toArray(new Event[events3.size()]);

            adapter = new EventAdapter(this, R.layout.entertainment_row, eventData);

            this.events = (ListView) findViewById(R.id.entertainment_day3);

            this.events.setAdapter(adapter);
        }

        // populate day 4
        List<Event> events4 = getEventsByDate("4");
        if (!(events4.size() < 1)) {

            eventData = events4.toArray(new Event[events4.size()]);

            adapter = new EventAdapter(this, R.layout.entertainment_row, eventData);

            this.events = (ListView) findViewById(R.id.entertainment_day4);

            this.events.setAdapter(adapter);
        }

        // close databases
        dbSpeakers.close();

    }


    // get list of events by the day
    public List<Event> getEventsByDate(String day) {
        String[] args={day};
        ArrayList<Event> result = new ArrayList<Event>();
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM entertainment WHERE date=? ORDER BY startTime", args);

        try{
            if (myCursor.moveToFirst()){
                do{
                    Event event = new Event();
                    event.setTitle(myCursor.getString((myCursor.getColumnIndex("title"))));
                    event.setBody(myCursor.getString((myCursor.getColumnIndex("body"))));
                    event.setDate(myCursor.getString((myCursor.getColumnIndex("date"))));
                    event.setEndTime(myCursor.getString((myCursor.getColumnIndex("endTime"))));
                    event.setStartTime(myCursor.getString((myCursor.getColumnIndex("startTime"))));
                    event.setLocation(myCursor.getString((myCursor.getColumnIndex("location"))));
                    event.setStarred(myCursor.getInt((myCursor.getColumnIndex("starred"))));

                    result.add(event);
                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }
        db.close();
        return result;
    }





}
