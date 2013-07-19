package com.shortstack.hackertracker.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.shortstack.hackertracker.Adapter.ContestAdapter;
import com.shortstack.hackertracker.Model.Contest;
import com.shortstack.hackertracker.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 8/29/12
 * Time: 2:26 PM
 */
public class Contests extends HackerTracker {

    public Contest[] contestData;
    public ContestAdapter adapter;
    public ListView contests;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contests);

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
                showListView1((ListView) findViewById(R.id.contests_day1));
            }
        });
        day2Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView2((ListView) findViewById(R.id.contests_day2));
            }
        });
        day3Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView3((ListView) findViewById(R.id.contests_day3));
            }
        });
        day4Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView4((ListView) findViewById(R.id.contests_day4));
            }
        });

        // put dates into textviews
        day1.setText(getDates("1"));
        day2.setText(getDates("2"));
        day3.setText(getDates("3"));
        day4.setText(getDates("4"));

        //query database for contests
        SQLiteDatabase dbSpeakers = myDbHelper.getReadableDatabase();

        // populate day 1
        List<Contest> contests = getContestsByDate("1");
        if (!(contests.size() < 1)) {

            contestData = contests.toArray(new Contest[contests.size()]);

            adapter = new ContestAdapter(this, R.layout.contest_row, contestData);

            this.contests = (ListView) findViewById(R.id.contests_day1);

            this.contests.setAdapter(adapter);
        }

        // populate day 2
        List<Contest> contests2 = getContestsByDate("2");
        if (!(contests2.size() < 1)) {

            contestData = contests2.toArray(new Contest[contests2.size()]);

            adapter = new ContestAdapter(this, R.layout.contest_row, contestData);

            this.contests = (ListView) findViewById(R.id.contests_day2);

            this.contests.setAdapter(adapter);
        }

        // populate day 3
        List<Contest> contests3 = getContestsByDate("3");
        if (!(contests3.size() < 1)) {

            contestData = contests3.toArray(new Contest[contests3.size()]);

            adapter = new ContestAdapter(this, R.layout.contest_row, contestData);

            this.contests = (ListView) findViewById(R.id.contests_day3);

            this.contests.setAdapter(adapter);
        }

        // populate day 4
        List<Contest> contests4 = getContestsByDate("4");
        if (!(contests4.size() < 1)) {

            contestData = contests4.toArray(new Contest[contests4.size()]);

            adapter = new ContestAdapter(this, R.layout.contest_row, contestData);

            this.contests = (ListView) findViewById(R.id.contests_day4);

            this.contests.setAdapter(adapter);
        }

        // close databases
        dbSpeakers.close();

    }


    // get list of contests by the day
    public List<Contest> getContestsByDate(String day) {
        String[] args={day};
        ArrayList<Contest> result = new ArrayList<Contest>();
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM contests WHERE date=? ORDER BY startTime", args);

        try{
            if (myCursor.moveToFirst()){
                do{
                    Contest contest = new Contest();
                    contest.setId(myCursor.getString((myCursor.getColumnIndex("_id"))));
                    contest.setTitle(myCursor.getString((myCursor.getColumnIndex("title"))));
                    contest.setBody(myCursor.getString((myCursor.getColumnIndex("body"))));
                    contest.setDate(myCursor.getString((myCursor.getColumnIndex("date"))));
                    contest.setForum(myCursor.getString((myCursor.getColumnIndex("forum"))));
                    contest.setEndTime(myCursor.getString((myCursor.getColumnIndex("endTime"))));
                    contest.setStartTime(myCursor.getString((myCursor.getColumnIndex("startTime"))));
                    contest.setLocation(myCursor.getString((myCursor.getColumnIndex("location"))));
                    contest.setStarred(myCursor.getInt((myCursor.getColumnIndex("starred"))));

                    result.add(contest);
                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }
        db.close();
        return result;
    }









}
