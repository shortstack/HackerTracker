package com.shortstack.hackertracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.*;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/29/12
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class contests extends HackerTracker {

    public AlertDialog.Builder builder;
    public AlertDialog alertDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contests);

        //query database for dates
        SQLiteDatabase dbDates = myDbHelper.getReadableDatabase();
        Cursor cursorDates = dbDates.query("dates", new String[] {"_id", "day", "month", "date"},
                null, null, null, null, null);

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
                showListView1();
            }
        });
        day2Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView2();
            }
        });
        day3Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView3();
            }
        });
        day4Layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListView4();
            }
        });

        // put dates into textviews
        cursorDates.moveToFirst();
        day1.setText(cursorDates.getString(cursorDates.getColumnIndex("day")) + ", " + cursorDates.getString(cursorDates.getColumnIndex("month")) + " " + cursorDates.getString(cursorDates.getColumnIndex("date")));
        cursorDates.moveToNext();
        day2.setText(cursorDates.getString(cursorDates.getColumnIndex("day")) + ", " + cursorDates.getString(cursorDates.getColumnIndex("month")) + " " + cursorDates.getString(cursorDates.getColumnIndex("date")));
        cursorDates.moveToNext();
        day3.setText(cursorDates.getString(cursorDates.getColumnIndex("day")) + ", " + cursorDates.getString(cursorDates.getColumnIndex("month")) + " " + cursorDates.getString(cursorDates.getColumnIndex("date")));
        cursorDates.moveToNext();
        day4.setText(cursorDates.getString(cursorDates.getColumnIndex("day")) + ", " + cursorDates.getString(cursorDates.getColumnIndex("month")) + " " + cursorDates.getString(cursorDates.getColumnIndex("date")));

        //query database for contests
        SQLiteDatabase dbContests = myDbHelper.getReadableDatabase();
        Cursor cursorContestsDay1 = dbContests.query("contests", new String[]{"_id", "title", "body", "startTime", "endTime", "date", "location", "forum"},
                "date like " + "'1'", null, null, null, "startTime");
        Cursor cursorContestsDay2 = dbContests.query("contests", new String[] {"_id", "title", "body", "startTime", "endTime", "date", "location", "forum"},
                "date like " + "'2'", null, null, null, "startTime");
        Cursor cursorContestsDay3 = dbContests.query("contests", new String[] {"_id", "title", "body", "startTime", "endTime", "date", "location", "forum"},
                "date like " + "'3'", null, null, null, "startTime");
        Cursor cursorContestsDay4 = dbContests.query("contests", new String[] {"_id", "title", "body", "startTime", "endTime", "date", "location", "forum"},
                "date like " + "'4'", null, null, null, "startTime");

        // set up listviews
        final ListView listContent1 = (ListView)findViewById(R.id.contests_day1);
        final ListView listContent2 = (ListView)findViewById(R.id.contests_day2);
        final ListView listContent3 = (ListView)findViewById(R.id.contests_day3);
        final ListView listContent4 = (ListView)findViewById(R.id.contests_day4);
        startManagingCursor(cursorContestsDay1);
        startManagingCursor(cursorContestsDay2);
        startManagingCursor(cursorContestsDay3);
        startManagingCursor(cursorContestsDay4);

        // put contest data into listview
        String[] fromContests = new String[]{dbAdapter.KEY_TITLE};
        int[] toContests = new int[]{R.id.text};
        SimpleCursorAdapter cursorAdapter1 =
                new SimpleCursorAdapter(this, R.layout.contest_row, cursorContestsDay1, fromContests, toContests);
        listContent1.setAdapter(cursorAdapter1); // day 1
        listContent1.setOnItemClickListener(listContentOnItemClickListener);
        SimpleCursorAdapter cursorAdapter2 =
                new SimpleCursorAdapter(this, R.layout.contest_row, cursorContestsDay2, fromContests, toContests);
        listContent2.setAdapter(cursorAdapter2); // day 2
        listContent2.setOnItemClickListener(listContentOnItemClickListener);
        SimpleCursorAdapter cursorAdapter3 =
                new SimpleCursorAdapter(this, R.layout.contest_row, cursorContestsDay3, fromContests, toContests);
        listContent3.setAdapter(cursorAdapter3); // day 3
        listContent3.setOnItemClickListener(listContentOnItemClickListener);
        SimpleCursorAdapter cursorAdapter4 =
                new SimpleCursorAdapter(this, R.layout.contest_row, cursorContestsDay4, fromContests, toContests);
        listContent4.setAdapter(cursorAdapter4); // day 4
        listContent4.setOnItemClickListener(listContentOnItemClickListener);


        // close databases
        dbDates.close();
        dbContests.close();

    }

    // toggle day 1 contests
    private void showListView1(){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand1);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
        ListView contests_day1 = (ListView) findViewById(R.id.contests_day1);
        if (contests_day1.getVisibility() == View.VISIBLE) {
            contests_day1.setVisibility(View.GONE);
            day2Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            contests_day1.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 2 contests
    private void showListView2(){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand2);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
        ListView contests_day2 = (ListView) findViewById(R.id.contests_day2);
        if(contests_day2.getVisibility() == View.VISIBLE) {
            contests_day2.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            contests_day2.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 3 contests
    private void showListView3(){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand3);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
        ListView contests_day3 = (ListView) findViewById(R.id.contests_day3);
        if(contests_day3.getVisibility() == View.VISIBLE) {
            contests_day3.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            contests_day3.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day2Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 4 contests
    private void showListView4(){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand4);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        ListView contests_day4 = (ListView) findViewById(R.id.contests_day4);
        if(contests_day4.getVisibility() == View.VISIBLE) {
            contests_day4.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            contests_day4.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day2Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // click listeners for listview items to show contest details
    private ListView.OnItemClickListener listContentOnItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // get contest details from cursor
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            String title = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_TITLE));
            String body = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_BODY));

            String startTime = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_STARTTIME));
            String endTime = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_ENDTIME));
            String location = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_LOCATION));

            String forum = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_FORUM));

            // build contest details into string
            StringBuilder sb = new StringBuilder();
            sb.append("Start Time: " + startTime + " \n");
            sb.append("End Time: " + endTime + " \n");
            sb.append("Location: " + location + " \n\n");
            if (forum != null) {
                sb.append("Forum: \n\n" + forum + "\n\n");
            }
            if (body != null) {
                sb.append(body);
            }

            // make links
            final SpannableString s = new SpannableString(sb);
            Linkify.addLinks(s, Linkify.ALL);

            // build alert dialog layout
            Context mContext = contests.this;
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.contest_details,
                    (ViewGroup) findViewById(R.id.layout_root));

            // assign values to layout parts
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(s);

            // set up & show alert dialog
            builder = new AlertDialog.Builder(contests.this);
            builder.setView(layout);
            builder.setTitle(title.split("- ")[1]);
            alertDialog = builder.create();
            alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();

            // make the textview clickable
            ((TextView)alertDialog.findViewById(R.id.text)).setMovementMethod(LinkMovementMethod.getInstance());
        }};

}
