package com.shortstack.hackertracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class HackerTracker extends Activity
{

    public dbAdapter myDbHelper;

    public void setMainScreen() {

        // button listener for speakers

        Button btnSpeakers = (Button)findViewById(R.id.speakers);
        btnSpeakers.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        speakers.class));

            }
        });

        // button listener for entertainment

        Button btnEntertainment = (Button)findViewById(R.id.entertainment);
        btnEntertainment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        entertainment.class));

            }
        });

        // button listener for maps

        Button btnMaps = (Button)findViewById(R.id.maps);
        btnMaps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        maps.class));

            }
        });

        // button listener for twitter

        Button btnTwitter = (Button)findViewById(R.id.twitter);
        btnTwitter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        twitter.class));

            }
        });

        // button listener for contests

        Button btnContests = (Button)findViewById(R.id.contests);
        btnContests.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        contests.class));

            }
        });

        // button listener for faq

        Button btnFaq = (Button)findViewById(R.id.faq);
        btnFaq.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(HackerTracker.this,
                        faq.class));

            }
        });

        // set up database

        myDbHelper = new dbAdapter(this);

        try {

            myDbHelper.createDataBase();

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
            case R.id.help:
                AlertDialog.Builder help = new AlertDialog.Builder(HackerTracker
                        .this);
                help.setTitle("Hacker Tracker Help");
                help.setMessage("Help Info Goes Here");
                help.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                    } });
                help.show();
                break;
        }
        return true;
    }


}
