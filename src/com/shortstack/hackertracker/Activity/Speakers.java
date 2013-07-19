package com.shortstack.hackertracker.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.shortstack.hackertracker.Adapter.SpeakerAdapter;
import com.shortstack.hackertracker.Model.Speaker;
import com.shortstack.hackertracker.R;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 8/29/12
 * Time: 2:26 PM
 */
public class Speakers extends HackerTracker {

    public Speaker[] speakerData;
    public SpeakerAdapter adapter;
    public ListView speakers;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speakers);

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
                showListView1((ListView) findViewById(R.id.speakers_day1));
            }
        });
        day2Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView2((ListView) findViewById(R.id.speakers_day2));
            }
        });
        day3Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView3((ListView) findViewById(R.id.speakers_day3));
            }
        });
        day4Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView4((ListView) findViewById(R.id.speakers_day4));
            }
        });

        // put dates into textviews
        day1.setText(getDates("1"));
        day2.setText(getDates("2"));
        day3.setText(getDates("3"));
        day4.setText(getDates("4"));

        //query database for speakers
        SQLiteDatabase dbSpeakers = myDbHelper.getReadableDatabase();

        // populate day 1
        List<Speaker> speakers = getSpeakersByDate("1");
        if (!(speakers.size() < 1)) {

            speakerData = speakers.toArray(new Speaker[speakers.size()]);

            adapter = new SpeakerAdapter(this, R.layout.speaker_row, speakerData);

            this.speakers = (ListView) findViewById(R.id.speakers_day1);

            this.speakers.setAdapter(adapter);
        }

        // populate day 2
        List<Speaker> speakers2 = getSpeakersByDate("2");
        if (!(speakers2.size() < 1)) {

            speakerData = speakers2.toArray(new Speaker[speakers2.size()]);

            adapter = new SpeakerAdapter(this, R.layout.speaker_row, speakerData);

            this.speakers = (ListView) findViewById(R.id.speakers_day2);

            this.speakers.setAdapter(adapter);
        }

        // populate day 3
        List<Speaker> speakers3 = getSpeakersByDate("3");
        if (!(speakers3.size() < 1)) {

            speakerData = speakers3.toArray(new Speaker[speakers3.size()]);

            adapter = new SpeakerAdapter(this, R.layout.speaker_row, speakerData);

            this.speakers = (ListView) findViewById(R.id.speakers_day3);

            this.speakers.setAdapter(adapter);
        }

        // populate day 4
        List<Speaker> speakers4 = getSpeakersByDate("4");
        if (!(speakers4.size() < 1)) {

            speakerData = speakers4.toArray(new Speaker[speakers4.size()]);

            adapter = new SpeakerAdapter(this, R.layout.speaker_row, speakerData);

            this.speakers = (ListView) findViewById(R.id.speakers_day4);

            this.speakers.setAdapter(adapter);
        }

        // close databases
        dbSpeakers.close();

    }


    // get list of speakers by the day
    public List<Speaker> getSpeakersByDate(String day) {
        String[] args={day};
        ArrayList<Speaker> result = new ArrayList<Speaker>();
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM speakers WHERE date=? ORDER BY startTime", args);

        try{
            if (myCursor.moveToFirst()){
                do{
                    Speaker speaker = new Speaker();
                    speaker.setTitle(myCursor.getString((myCursor.getColumnIndex("title"))));
                    speaker.setBody(myCursor.getString((myCursor.getColumnIndex("body"))));
                    speaker.setSpeaker(myCursor.getString((myCursor.getColumnIndex("speaker"))));
                    speaker.setDate(myCursor.getString((myCursor.getColumnIndex("date"))));
                    speaker.setEndTime(myCursor.getString((myCursor.getColumnIndex("endTime"))));
                    speaker.setStartTime(myCursor.getString((myCursor.getColumnIndex("startTime"))));
                    speaker.setLocation(myCursor.getString((myCursor.getColumnIndex("location"))));
                    speaker.setDemo(BooleanUtils.toBoolean(Integer.parseInt(myCursor.getString((myCursor.getColumnIndex("demo"))))));
                    speaker.setExploit(BooleanUtils.toBoolean(Integer.parseInt(myCursor.getString((myCursor.getColumnIndex("exploit"))))));
                    speaker.setTool(BooleanUtils.toBoolean(Integer.parseInt(myCursor.getString((myCursor.getColumnIndex("tool"))))));
                    speaker.setInfo(BooleanUtils.toBoolean(Integer.parseInt(myCursor.getString((myCursor.getColumnIndex("info"))))));
                    speaker.setStarred(myCursor.getInt((myCursor.getColumnIndex("starred"))));

                    result.add(speaker);
                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }
        db.close();
        return result;
    }







}
