package com.shortstack.hackertracker.Activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Adapter.DatabaseAdapterStarred;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin on 8/8/2016.
 */
public class DetailsActivity extends AppCompatActivity {

    private Default item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getIntent().getExtras() != null) {
            item = Parcels.unwrap(getIntent().getExtras().getParcelable("item"));
        } else if (savedInstanceState != null) {
            item = Parcels.unwrap(savedInstanceState.getParcelable("item"));
        }

        myToolbar.setTitle(item.getTitle());

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView titleText = (TextView) findViewById(R.id.title);
        TextView nameText = (TextView) findViewById(R.id.speaker);
        TextView timeText = (TextView) findViewById(R.id.when);
        TextView locationText = (TextView) findViewById(R.id.where);
        TextView bodyText = (TextView) findViewById(R.id.description);
        //LinearLayout whereLayout = (LinearLayout) findViewById(R.id.where_holder);
        View demo = findViewById(R.id.demo);
        View exploit = findViewById(R.id.exploit);
        View tool = findViewById(R.id.tool);


        // set title
        titleText.setText(item.getTitle());

        // if not a speaker, hide speaker name
        if (!item.getType().equals(Constants.TYPE_SPEAKER) || item.getName() == null) {
            nameText.setVisibility(View.GONE);
        } else if (item.getName().isEmpty()) {
            nameText.setVisibility(View.GONE);
        } else {
            nameText.setText(item.getName());
        }

        // if speaker, show speaker type

        if (item.getTool() == 1) {
            tool.setVisibility(View.VISIBLE);
        }
        if (item.getExploit() == 1) {
            exploit.setVisibility(View.VISIBLE);
        }
        if (item.getDemo() == 1) {
            demo.setVisibility(View.VISIBLE);
        }


        // set location
        if (item.getLocation() != null) {
            locationText.append(item.getLocation());
        } else {
        }

        // set body
        bodyText.setText(item.getDescription());

        // set date & time


        String dateStr = item.getDate() + ", " + item.getBegin();
        DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm");
        DateFormat writeFormat = new SimpleDateFormat("EEEE h:mm aa");
        Date date = null;
        try {
            date = readFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedDate = "";
        if (date != null) {
            formattedDate = writeFormat.format(date);
        }


        timeText.setText(item.getDate() + " @ " + item.getBegin() + " - " + item.getEnd() + "\n" + formattedDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);

        if (item.getLink() == null || item.getLink().equals("") || item.getLink().equals(" ")) {
            menu.findItem(R.id.information).setVisible(false);
        } else {
            menu.findItem(R.id.information).setVisible(true);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.information:

                break;
            case R.id.share:
                break;

            case R.id.star:
                updateSchedule();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSchedule() {
        DatabaseAdapter myDbOfficialHelper = new DatabaseAdapter(this);
        DatabaseAdapterStarred myDbHelperStars = new DatabaseAdapterStarred(this);
        SQLiteDatabase dbOfficial = myDbOfficialHelper.getWritableDatabase();
        SQLiteDatabase dbStars = myDbHelperStars.getWritableDatabase();

        // if not starred, star it
        if (item.getStarred()==0) {

            // add to starred database
            dbStars.execSQL("INSERT INTO data VALUES (" + item.getId() + ")");
            dbOfficial.execSQL("UPDATE data SET starred=" + 1 + " WHERE id=" + item.getId());

            // set up alarm
            if (!item.getBegin().equals("")) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(item.getDate().split("-")[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(item.getDate().split("-")[1])-1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(item.getDate().split("-")[2]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(item.getBegin().split(":")[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(item.getBegin().split(":")[1]));
                calendar.set(Calendar.SECOND, 0);
                long when = calendar.getTimeInMillis() - 1200000;

                HackerTrackerApplication.scheduleNotification(HackerTrackerApplication.getNotification("\"" + item.getTitle() + "\" is starting in 20 minutes in " + item.getLocation() + "."), when, item.getId());
            }

            // change star
            item.setStarred(1);
            Toast.makeText(this, R.string.schedule_added, Toast.LENGTH_SHORT).show();

        } else {

            // remove from starred database
            dbStars.delete("data", "id=" + item.getId(), null);
            dbOfficial.execSQL("UPDATE data SET starred=" + 0 + " WHERE id=" + item.getId());

            // remove alarm
            HackerTrackerApplication.cancelNotification(item.getId());

            // change star
            item.setStarred(0);
            Toast.makeText(this,R.string.schedule_removed,Toast.LENGTH_SHORT).show();
        }

        dbOfficial.close();
        dbStars.close();
    }
}
