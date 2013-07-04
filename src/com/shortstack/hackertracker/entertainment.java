package com.shortstack.hackertracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 8/29/12
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class entertainment extends HackerTracker {

    public AlertDialog.Builder builder;
    public AlertDialog alertDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entertainment);

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

        //query database for entertainment
        SQLiteDatabase dbEntertainment = myDbHelper.getReadableDatabase();
        Cursor cursorEntertainmentDay1 = dbEntertainment.query("entertainment", new String[]{"_id", "title", "body", "startTime", "endTime", "date", "location"},
                "date like " + "'1'", null, null, null, "startTime");
        Cursor cursorEntertainmentDay2 = dbEntertainment.query("entertainment", new String[] {"_id", "title", "body", "startTime", "endTime", "date", "location"},
                "date like " + "'2'", null, null, null, "startTime");
        Cursor cursorEntertainmentDay3 = dbEntertainment.query("entertainment", new String[] {"_id", "title", "body", "startTime", "endTime", "date", "location"},
                "date like " + "'3'", null, null, null, "startTime");
        Cursor cursorEntertainmentDay4 = dbEntertainment.query("entertainment", new String[] {"_id", "title", "body", "startTime", "endTime", "date", "location"},
                "date like " + "'4'", null, null, null, "startTime");

        // set up listviews
        final ListView listContent1 = (ListView)findViewById(R.id.entertainment_day1);
        final ListView listContent2 = (ListView)findViewById(R.id.entertainment_day2);
        final ListView listContent3 = (ListView)findViewById(R.id.entertainment_day3);
        final ListView listContent4 = (ListView)findViewById(R.id.entertainment_day4);
        startManagingCursor(cursorEntertainmentDay1);
        startManagingCursor(cursorEntertainmentDay2);
        startManagingCursor(cursorEntertainmentDay3);
        startManagingCursor(cursorEntertainmentDay4);

        // put entertainment data into listview
        String[] fromEntertainment = new String[]{dbAdapter.KEY_TITLE};
        int[] toEntertainment = new int[]{R.id.text};
        SimpleCursorAdapter cursorAdapter1 =
                new SimpleCursorAdapter(this, R.layout.entertainment_row, cursorEntertainmentDay1, fromEntertainment, toEntertainment);
        listContent1.setAdapter(cursorAdapter1); // day 1
        listContent1.setOnItemClickListener(listContentOnItemClickListener);
        SimpleCursorAdapter cursorAdapter2 =
                new SimpleCursorAdapter(this, R.layout.entertainment_row, cursorEntertainmentDay2, fromEntertainment, toEntertainment);
        listContent2.setAdapter(cursorAdapter2); // day 2
        listContent2.setOnItemClickListener(listContentOnItemClickListener);
        SimpleCursorAdapter cursorAdapter3 =
                new SimpleCursorAdapter(this, R.layout.entertainment_row, cursorEntertainmentDay3, fromEntertainment, toEntertainment);
        listContent3.setAdapter(cursorAdapter3); // day 3
        listContent3.setOnItemClickListener(listContentOnItemClickListener);
        SimpleCursorAdapter cursorAdapter4 =
                new SimpleCursorAdapter(this, R.layout.entertainment_row, cursorEntertainmentDay4, fromEntertainment, toEntertainment);
        listContent4.setAdapter(cursorAdapter4); // day 4
        listContent4.setOnItemClickListener(listContentOnItemClickListener);


        // close databases
        dbDates.close();
        dbEntertainment.close();

    }

    // toggle day 1 entertainment
    private void showListView1(){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand1);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
        ListView entertainment_day1 = (ListView) findViewById(R.id.entertainment_day1);
        if (entertainment_day1.getVisibility() == View.VISIBLE) {
            entertainment_day1.setVisibility(View.GONE);
            day2Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            entertainment_day1.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 2 entertainment
    private void showListView2(){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand2);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
        ListView entertainment_day2 = (ListView) findViewById(R.id.entertainment_day2);
        if(entertainment_day2.getVisibility() == View.VISIBLE) {
            entertainment_day2.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            entertainment_day2.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 3 entertainment
    private void showListView3(){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand3);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day4Layout = (RelativeLayout)findViewById(R.id.day4Layout);
        ListView entertainment_day3 = (ListView) findViewById(R.id.entertainment_day3);
        if(entertainment_day3.getVisibility() == View.VISIBLE) {
            entertainment_day3.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.VISIBLE);
            day4Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            entertainment_day3.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day2Layout.setVisibility(View.GONE);
            day4Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // toggle day 4 entertainment
    private void showListView4(){
        TextView collapseExpand = (TextView)findViewById(R.id.collapseExpand4);
        RelativeLayout day1Layout = (RelativeLayout)findViewById(R.id.day1Layout);
        RelativeLayout day2Layout = (RelativeLayout)findViewById(R.id.day2Layout);
        RelativeLayout day3Layout = (RelativeLayout)findViewById(R.id.day3Layout);
        ListView entertainment_day4 = (ListView) findViewById(R.id.entertainment_day4);
        if(entertainment_day4.getVisibility() == View.VISIBLE) {
            entertainment_day4.setVisibility(View.GONE);
            day1Layout.setVisibility(View.VISIBLE);
            day2Layout.setVisibility(View.VISIBLE);
            day3Layout.setVisibility(View.VISIBLE);
            collapseExpand.setText("[+]");
        } else {
            entertainment_day4.setVisibility(View.VISIBLE);
            day1Layout.setVisibility(View.GONE);
            day2Layout.setVisibility(View.GONE);
            day3Layout.setVisibility(View.GONE);
            collapseExpand.setText("[-]");
        }
    }

    // click listeners for listview items to show entertainment details
    private ListView.OnItemClickListener listContentOnItemClickListener = new ListView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // get entertainment details from cursor
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            String title = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_TITLE));
            String body = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_BODY));

            String startTime = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_STARTTIME));
            String endTime = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_ENDTIME));
            String location = cursor.getString(cursor.getColumnIndex(dbAdapter.KEY_LOCATION));

            // build entertainment details into string
            StringBuilder sb = new StringBuilder();
            sb.append("Start Time: " + startTime + " \n");
            sb.append("End Time: " + endTime + " \n");
            sb.append("Location: " + location + " \n\n");
            if (body != null) {
                sb.append(body);
            }

            // build alert dialog layout
            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.entertainment_details,
                    (ViewGroup) findViewById(R.id.layout_root));

            // assign values to layout parts
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(sb);

            // set up & show alert dialog
            builder = new AlertDialog.Builder(entertainment.this);
            builder.setView(layout);
            builder.setTitle(title.split("- ")[1]);
            alertDialog = builder.create();
            alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }};

}
