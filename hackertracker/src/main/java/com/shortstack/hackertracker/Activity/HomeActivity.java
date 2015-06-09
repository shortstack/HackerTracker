package com.shortstack.hackertracker.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shortstack.hackertracker.Adapter.DatabaseAdapterOfficial;
import com.shortstack.hackertracker.Api.ApiException;
import com.shortstack.hackertracker.Api.Impl.SyncServiceImpl;
import com.shortstack.hackertracker.Api.SyncService;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Contests.ContestPagerFragment;
import com.shortstack.hackertracker.Events.EventPagerFragment;
import com.shortstack.hackertracker.Fragment.FragmentDrawer;
import com.shortstack.hackertracker.Kids.KidsPagerFragment;
import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;
import com.shortstack.hackertracker.Fragment.FAQFragment;
import com.shortstack.hackertracker.Fragment.HomeFragment;
import com.shortstack.hackertracker.Fragment.MapsFragment;
import com.shortstack.hackertracker.Fragment.SearchFragment;
import com.shortstack.hackertracker.Model.ApiBase;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Model.OfficialList;
import com.shortstack.hackertracker.Parties.PartyPagerFragment;
import com.shortstack.hackertracker.Schedule.SchedulePagerFragment;
import com.shortstack.hackertracker.Skytalks.SkytalksPagerFragment;
import com.shortstack.hackertracker.Utils.ApiResponseUtil;
import com.shortstack.hackertracker.Utils.DialogUtil;
import com.shortstack.hackertracker.Vendors.VendorsFragment;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Speakers.SpeakerPagerFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class HomeActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {

    public static FragmentManager fragmentManager;
    private FragmentDrawer drawerFragment;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //set up toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(R.string.home);
        mToolbar.setTitleTextAppearance(getApplicationContext(),R.style.titleText);

        fragmentManager = getSupportFragmentManager();

        // set up action bar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // set up nav drawer
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        // set home
        fragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment.newInstance(1))
                .addToBackStack("HomeFragment")
                .commit();
    }

    @Override
    public void onBackPressed(){

        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {

            FragmentName fragName = FragmentName.valueOf(fm.getBackStackEntryAt(fm.getBackStackEntryCount()-2).getName());
            switch (fragName) {
                case HomeFragment:
                    getSupportActionBar().setTitle(getString(R.string.home).toUpperCase());
                    break;
                case SpeakerPagerFragment:
                    getSupportActionBar().setTitle(getString(R.string.speakers).toUpperCase());
                    break;
                case ContestPagerFragment:
                    getSupportActionBar().setTitle(getString(R.string.contests).toUpperCase());
                    break;
                case EventPagerFragment:
                    getSupportActionBar().setTitle(getString(R.string.events).toUpperCase());
                    break;
                case PartyPagerFragment:
                    getSupportActionBar().setTitle(getString(R.string.parties).toUpperCase());
                    break;
                case VendorsFragment:
                    getSupportActionBar().setTitle(getString(R.string.vendors).toUpperCase());
                    break;
                case MapsFragment:
                    getSupportActionBar().setTitle(getString(R.string.maps).toUpperCase());
                    break;
                case LinksFragment:
                    getSupportActionBar().setTitle(getString(R.string.links).toUpperCase());
                    break;
                case FAQFragment:
                    getSupportActionBar().setTitle(getString(R.string.faq).toUpperCase());
                    break;
                case ShuttleFragment:
                    getSupportActionBar().setTitle(getString(R.string.shuttle).toUpperCase());
                    break;
                case SchedulePagerFragment:
                    getSupportActionBar().setTitle(getString(R.string.schedule).toUpperCase());
                    break;

            }
            fm.popBackStack();
        } else {
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_schedule) {
            getSupportActionBar().setTitle(getString(R.string.schedule).toUpperCase());
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SchedulePagerFragment.newInstance(9))
                    .addToBackStack("SchedulePagerFragment")
                    .commit();
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            getSupportActionBar().setTitle(getString(R.string.search).toUpperCase());
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SearchFragment.newInstance(12))
                    .addToBackStack("SearchFragment")
                    .commit();
            return true;
        } else if (id == R.id.action_share) {
            DialogUtil.shareScheduleDialog(this).show();
            return true;
        } else if (id == R.id.action_clear) {
            DialogUtil.clearScheduleDialog(this).show();
            return true;
        } else if (id == R.id.action_sync) {
            DialogUtil.syncSpeakersDialog(this).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private enum FragmentName {
        HomeFragment,
        SpeakerPagerFragment,
        EventPagerFragment,
        ContestPagerFragment,
        PartyPagerFragment,
        VendorsFragment,
        MapsFragment,
        ShuttleFragment,
        FAQFragment,
        LinksFragment,
        SchedulePagerFragment,
        SearchFragment;
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {

        // update the main content by replacing fragments
        fragmentManager = getSupportFragmentManager();

        switch (position)
        {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance(1))
                        .addToBackStack("HomeFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.home).toUpperCase());
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SpeakerPagerFragment.newInstance(2))
                        .addToBackStack("SpeakerPagerFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.speakers).toUpperCase());
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SkytalksPagerFragment.newInstance(3))
                        .addToBackStack("SkytalksPagerFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.skytalks).toUpperCase());
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContestPagerFragment.newInstance(4))
                        .addToBackStack("ContestPagerFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.contests).toUpperCase());
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, EventPagerFragment.newInstance(5))
                        .addToBackStack("EventPagerFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.events).toUpperCase());
                break;
            case 5:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PartyPagerFragment.newInstance(6))
                        .addToBackStack("PartyPagerFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.parties).toUpperCase());
                break;
            case 6:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, KidsPagerFragment.newInstance(7))
                        .addToBackStack("KidsPagerFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.kids).toUpperCase());
                break;
            case 7:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, VendorsFragment.newInstance(8))
                        .addToBackStack("VendorsFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.vendors).toUpperCase());
                break;
            case 8:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MapsFragment.newInstance(9))
                        .addToBackStack("MapsFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.maps).toUpperCase());
                break;
/*            case 7:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ShuttleFragment.newInstance(10))
                        .addToBackStack("ShuttleFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.shuttle).toUpperCase());
                break;*/
            case 9:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FAQFragment.newInstance(10))
                        .addToBackStack("FAQFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.faq).toUpperCase());
                break;
/*            case 9:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, LinksFragment.newInstance(11))
                        .addToBackStack("LinksFragment")
                        .commit();
                getSupportActionBar().setTitle(getString(R.string.links).toUpperCase());
                break;*/
        }
    }

    public static void clearSchedule(Context context) {
        SQLiteDatabase dbStars = HackerTrackerApplication.myDbHelperStars.getWritableDatabase();
        SQLiteDatabase dbOfficial = HackerTrackerApplication.myOfficialDbHelper.getWritableDatabase();
        SQLiteDatabase db = HackerTrackerApplication.myDbHelper.getWritableDatabase();

        // delete all data in starred database
        dbStars.execSQL("DELETE FROM data");

        // update all data in main database to not be starred
        dbOfficial.execSQL("UPDATE data SET starred=0");
        db.execSQL("UPDATE data SET starred=0");

        // reload screen
        HomeActivity.refreshSchedule();

        // show cleared message
        Toast.makeText(context, R.string.schedule_cleared, Toast.LENGTH_SHORT).show();

        // close database
        dbOfficial.close();
        db.close();
        dbStars.close();
    }

    public static void refreshSchedule() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SchedulePagerFragment.newInstance(9))
                .addToBackStack("SchedulePagerFragment")
                .commit();
    }

    public static void syncSchedule(Context context) {

        SyncService syncService = new SyncServiceImpl();

        try {
            syncService.syncDatabase(context, new SyncDatabaseListener(context));
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    private static class SyncDatabaseListener implements AsyncTaskCompleteListener<ApiBase> {

        Context context;

        SyncDatabaseListener(Context context) {
            this.context = context;
        }

        @Override
        public void onTaskComplete(ApiBase result) {

            // get schedule from API
            OfficialList schedule;
            try {
                schedule = (OfficialList) ApiResponseUtil.parseResponse(result, OfficialList.class);
            } catch (ApiException e) {
                AlertDialog dialog = DialogUtil.apiErrorDialog(context);
                dialog.show();
                return;
            }

            ArrayList<Default> officialArray = new ArrayList(Arrays.asList(schedule.getAll()));

            if (officialArray.size()!=0) {
                syncDatabase(officialArray, context);
            } else {
                // no results found, don't sync
                Toast.makeText(context,R.string.no_updates,Toast.LENGTH_SHORT).show();
            }
        }

    }

    private static void syncDatabase(ArrayList<Default> officialArray, Context context) {

        HashMap<String, String> queryValues;
        DatabaseAdapterOfficial controller = new DatabaseAdapterOfficial(context);

        // Create GSON object
        Gson gson = new GsonBuilder().create();

        // Extract JSON array from the response
        JsonArray arr = gson.toJsonTree(officialArray).getAsJsonArray();

        // If no of array elements is not zero
        if(arr.size() != 0){

            // Loop through each array element, get JSON object
            for (int i = 0; i < arr.size(); i++) {

                // Get JSON object
                JsonObject obj = (JsonObject) arr.get(i).getAsJsonObject();

                // DB QueryValues Object to insert into SQLite
                queryValues = new HashMap<String, String>();

                // Add userID extracted from Object
                queryValues.put("id", obj.get("id").getAsString());
                queryValues.put("title", obj.get("title").getAsString());
                queryValues.put("name", obj.get("who").getAsString());
                queryValues.put("begin", obj.get("begin").getAsString());
                queryValues.put("end", obj.get("end").getAsString());
                queryValues.put("date", obj.get("date").getAsString());
                queryValues.put("location", obj.get("location").getAsString());
                queryValues.put("description", obj.get("description").getAsString());
                queryValues.put("type", obj.get("type").getAsString());
                queryValues.put("link", obj.get("link").getAsString());
                queryValues.put("demo", obj.get("demo").getAsString());
                queryValues.put("tool", obj.get("tool").getAsString());
                queryValues.put("exploit", obj.get("exploit").getAsString());

                // Insert User into SQLite DB
                controller.updateDatabase(queryValues);

            }
        }
    }

}
