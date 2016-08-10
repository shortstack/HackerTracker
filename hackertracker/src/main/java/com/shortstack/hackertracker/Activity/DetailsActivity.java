package com.shortstack.hackertracker.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        LinearLayout whereLayout = (LinearLayout) findViewById(R.id.where_holder);
        ImageView demo = (ImageView) findViewById(R.id.demo);
        ImageView exploit = (ImageView) findViewById(R.id.exploit);
        ImageView tool = (ImageView) findViewById(R.id.tool);
        LinearLayout icons = (LinearLayout) findViewById(R.id.icons);


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
            whereLayout.setVisibility(View.GONE);
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

        }
        return super.onOptionsItemSelected(item);
    }
}
