package com.shortstack.hackertracker.Activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Adapter.DatabaseAdapterStarred;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import org.parceler.Parcels;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Admin on 8/8/2016.
 */
public class DetailsActivity extends AppCompatActivity {

    private Default item;

    @Bind(R.id.my_toolbar)
    Toolbar toolbar;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.speaker)
    TextView host;

    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.location)
    TextView location;

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.demo)
    View demo;

    @Bind(R.id.exploit)
    View exploit;

    @Bind(R.id.tool)
    View tool;

    @Bind(R.id.isNew)
    View isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            item = Parcels.unwrap(getIntent().getExtras().getParcelable("item"));
        } else if (savedInstanceState != null) {
            item = Parcels.unwrap(savedInstanceState.getParcelable("item"));
        }


        toolbar.setTitle(item.getTitle());

        displayText();
        displaySpeakerIcons();
        displayNewIcon();
    }

    private void displayText() {
        title.setText(getContent().getTitle());
        host.setText(getContent().getName());
        host.setVisibility(View.VISIBLE);
        time.setText(getContent().getFullTimeStamp(this));
        time.setVisibility(View.VISIBLE);
        location.setText(getContent().getLocation());
        description.setText(item.getDescription());
    }

    private void displaySpeakerIcons() {
        tool.setVisibility(getContent().isTool() ? View.VISIBLE : View.GONE);
        exploit.setVisibility(getContent().isExploit() ? View.VISIBLE : View.GONE);
        demo.setVisibility(getContent().isDemo() ? View.VISIBLE : View.GONE);
    }

    private void displayNewIcon() {
        isNew.setVisibility(getContent().isNew() ? View.VISIBLE : View.GONE);
    }

    private Default getContent() {
        return item;
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
        if (item.getStarred() == 0) {

            // add to starred database
            dbStars.execSQL("INSERT INTO data VALUES (" + item.getId() + ")");
            dbOfficial.execSQL("UPDATE data SET starred=" + 1 + " WHERE id=" + item.getId());

            // set up alarm
            if (!item.getBegin().equals("")) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(item.getDate().split("-")[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(item.getDate().split("-")[1]) - 1);
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
            Toast.makeText(this, R.string.schedule_removed, Toast.LENGTH_SHORT).show();
        }

        dbOfficial.close();
        dbStars.close();
    }
}
