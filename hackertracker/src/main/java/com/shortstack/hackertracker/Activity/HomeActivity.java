package com.shortstack.hackertracker.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Api.ApiException;
import com.shortstack.hackertracker.Api.Impl.SyncServiceImpl;
import com.shortstack.hackertracker.Api.SyncService;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Books.BooksPagerFragment;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Contests.ContestPagerFragment;
import com.shortstack.hackertracker.DemoLabs.DemoLabsPagerFragment;
import com.shortstack.hackertracker.Events.EventPagerFragment;
import com.shortstack.hackertracker.Font.HelveticaTextView;
import com.shortstack.hackertracker.Fragment.FAQFragment;
import com.shortstack.hackertracker.Fragment.FragmentDrawer;
import com.shortstack.hackertracker.Fragment.HomeFragment;
import com.shortstack.hackertracker.Fragment.MapsFragment;
import com.shortstack.hackertracker.Fragment.SearchFragment;
import com.shortstack.hackertracker.Fragment.SettingsFragment;
import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;
import com.shortstack.hackertracker.Model.ApiBase;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Model.OfficialList;
import com.shortstack.hackertracker.Parties.PartyPagerFragment;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Schedule.SchedulePagerFragment;
import com.shortstack.hackertracker.Skytalks.SkytalksPagerFragment;
import com.shortstack.hackertracker.Speakers.SpeakerPagerFragment;
import com.shortstack.hackertracker.Utils.ApiResponseUtil;
import com.shortstack.hackertracker.Utils.DialogUtil;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;
import com.shortstack.hackertracker.Utils.UpdateTask;
import com.shortstack.hackertracker.Vendors.VendorsFragment;
import com.shortstack.hackertracker.Villages.VillagePagerFragment;
import com.shortstack.hackertracker.Workshops.WorkshopPagerFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    public static FragmentManager fragmentManager;
    public static ActionBar actionBar;
    private FragmentDrawer drawerFragment;
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    public static HelveticaTextView mTitle;
    private Context context;
    private static ProgressDialog updateCheckDialog;
    public static ProgressDialog syncScheduleDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // set up toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mTitle = (HelveticaTextView) mToolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(R.string.home);
        
        // get context
        context = HomeActivity.this;

        // get fragment manager
        fragmentManager = getSupportFragmentManager();

        // set up dialogs
        updateCheckDialog = DialogUtil.getProgressDialog(context, context.getResources().getString(R.string.checking_for_updates));
        syncScheduleDialog = DialogUtil.getProgressDialog(context, context.getResources().getString(R.string.syncing));

        // set up action bar
        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        // set up nav drawer
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, drawerLayout, mToolbar);
        drawerFragment.setDrawerListener(this);

        // set home
        fragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment.newInstance(1))
                .addToBackStack("HomeFragment")
                .commit();

        // export database (using to backup official database instead of having to manually import)
        // TODO: comment this out upon release
        //exportDB();
    }

    @Override
    public void onBackPressed(){

        // if drawer is open, close nav drawer
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else { // otherwise go to previous fragment

            FragmentManager fm = this.getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 1) {

                FragmentName fragName = FragmentName.valueOf(fm.getBackStackEntryAt(fm.getBackStackEntryCount()-2).getName());
                switch (fragName) {
                    case HomeFragment:
                        mTitle.setText(getString(R.string.home).toUpperCase());
                        break;
                    case SpeakerPagerFragment:
                        mTitle.setText(getString(R.string.speakers).toUpperCase());
                        break;
                    case ContestPagerFragment:
                        mTitle.setText(getString(R.string.contests).toUpperCase());
                        break;
                    case EventPagerFragment:
                        mTitle.setText(getString(R.string.events).toUpperCase());
                        break;
                    case PartyPagerFragment:
                        mTitle.setText(getString(R.string.parties).toUpperCase());
                        break;
                    case VendorsFragment:
                        mTitle.setText(getString(R.string.vendors).toUpperCase());
                        break;
                    case MapsFragment:
                        mTitle.setText(getString(R.string.maps).toUpperCase());
                        break;
                    case BooksPagerFragment:
                        mTitle.setText(getString(R.string.books).toUpperCase());
                        break;
                    case VillagePagerFragment:
                        mTitle.setText(getString(R.string.villages).toUpperCase());
                        break;
                    case LinksFragment:
                        mTitle.setText(getString(R.string.links).toUpperCase());
                        break;
                    case FAQFragment:
                        mTitle.setText(getString(R.string.faq).toUpperCase());
                        break;
                    case ShuttleFragment:
                        mTitle.setText(getString(R.string.shuttle).toUpperCase());
                        break;
                    case DemoLabsFragment:
                        mTitle.setText(getString(R.string.demolabs).toUpperCase());
                        break;
                    case KidsPagerFragment:
                        mTitle.setText(getString(R.string.kids).toUpperCase());
                        break;
                    case SkytalksPagerFragment:
                        mTitle.setText(getString(R.string.skytalks).toUpperCase());
                        break;
                    case SchedulePagerFragment:
                        mTitle.setText(getString(R.string.schedule).toUpperCase());
                        break;
                    case SearchFragment:
                        mTitle.setText(getString(R.string.search).toUpperCase());
                        break;
                    case SettingsFragment:
                        mTitle.setText(getString(R.string.settings).toUpperCase());
                        break;
                }
                fm.popBackStack();
            } else {
                finish();
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate context menu
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_search) {
            // open search fragment
            addToBackStack(R.string.search, Constants.FRAGMENT_SEARCH, SearchFragment.newInstance(12));
            return true;
        } else if (id == R.id.action_share) {
            // show share dialog
            DialogUtil.shareScheduleDialog(this).show();
            return true;
        } else if (id == R.id.action_sync) {
            // show online sync dialog
            //Toast.makeText(context,getResources().getString(R.string.sync_availability),Toast.LENGTH_SHORT).show();
            DialogUtil.syncSpeakersDialog(this).show();
            return true;
        } else if (id == R.id.action_settings) {
            // open settings fragment
            addToBackStack(R.string.settings, Constants.FRAGMENT_SETTINGS, new SettingsFragment());
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
        BooksPagerFragment,
        VillagePagerFragment,
        WorkshopPagerFragment,
        ShuttleFragment,
        SkytalksPagerFragment,
        KidsPagerFragment,
        FAQFragment,
        LinksFragment,
        DemoLabsFragment,
        SchedulePagerFragment,
        SearchFragment,
        SettingsFragment
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
                // home
                addToBackStack(R.string.home, Constants.FRAGMENT_HOME, HomeFragment.newInstance(1));
                break;
            case 1:
                // speakers
                addToBackStack(R.string.speakers, Constants.FRAGMENT_SPEAKERS, SpeakerPagerFragment.newInstance(2));
                break;
            case 2:
                // skytalks
                addToBackStack(R.string.skytalks, Constants.FRAGMENT_SKYTALKS, SkytalksPagerFragment.newInstance(3));
                break;
            case 3:
                // contests
                addToBackStack(R.string.contests, Constants.FRAGMENT_CONTESTS, ContestPagerFragment.newInstance(4));
                break;
            case 4:
                // events
                addToBackStack(R.string.events, Constants.FRAGMENT_EVENTS, EventPagerFragment.newInstance(5));
                break;
            case 5:
                // parties
                addToBackStack(R.string.parties, Constants.FRAGMENT_PARTIES, PartyPagerFragment.newInstance(6));
                break;
            case 6:
                // villages
                addToBackStack(R.string.villages, Constants.FRAGMENT_VILLAGES, VillagePagerFragment.newInstance(7));
                break;
            case 7:
                // workshops
                addToBackStack(R.string.workshops, Constants.FRAGMENT_WORKSHOPS, WorkshopPagerFragment.newInstance(8));
                break;
            case 8:
                // book signings
                addToBackStack(R.string.books, Constants.FRAGMENT_BOOKS, BooksPagerFragment.newInstance(9));
                break;
            case 9:
                // demo labs
                Toast.makeText(context,R.string.demolabs_message, Toast.LENGTH_SHORT).show();
                //addToBackStack(R.string.demolabs, Constants.FRAGMENT_DEMOLAB, DemoLabsPagerFragment.newInstance(10));
                break;
            /*case 9:
                // kids
                addToBackStack(R.string.kids, Constants.FRAGMENT_KIDS, KidsPagerFragment.newInstance(10));
                break;*/
            case 10:
                // vendors
                Toast.makeText(context,R.string.vendors_message, Toast.LENGTH_SHORT).show();
                //addToBackStack(R.string.vendors, Constants.FRAGMENT_VENDORS, VendorsFragment.newInstance(11));
                break;
            case 11:
                // maps
                addToBackStack(R.string.maps, Constants.FRAGMENT_MAPS, MapsFragment.newInstance(12));
                break;
            case 12:
                // faq
                addToBackStack(R.string.faq, Constants.FRAGMENT_FAQ, FAQFragment.newInstance(13));
                break;
            case 13:
                // settings
                addToBackStack(R.string.settings, Constants.FRAGMENT_SETTINGS, new SettingsFragment());
                break;
        }
    }

    // add new fragment to back stack
    private void addToBackStack(int title, String name, Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(name)
                .commit();
        mTitle.setText(getString(title).toUpperCase());
    }

    // scroll viewpager to current day
    public static void setDay(ViewPager pager) {

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        String weekDay = dayFormat.format(calendar.getTime());

        switch (weekDay){
            case Constants.DAY_0:
                pager.setCurrentItem(0);
                break;
            case Constants.DAY_1:
                pager.setCurrentItem(1);
                break;
            case Constants.DAY_2:
                pager.setCurrentItem(2);
                break;
            case Constants.DAY_3:
                pager.setCurrentItem(3);
                break;
            case Constants.DAY_4:
                pager.setCurrentItem(4);
                break;
            default:
                pager.setCurrentItem(0);
                break;
        }

    }

    // clear schedule from DB
    public static void clearSchedule(Context context) {
        SQLiteDatabase dbStars = HackerTrackerApplication.myDbHelperStars.getWritableDatabase();
        SQLiteDatabase db = HackerTrackerApplication.dbHelper.getWritableDatabase();

        // delete all data in starred database
        dbStars.execSQL("DELETE FROM data");

        // update all data in main database to not be starred
        db.execSQL("UPDATE data SET starred=0");

        // reload schedule
        refreshSchedule();

        // show cleared message
        Toast.makeText(context, R.string.schedule_cleared, Toast.LENGTH_SHORT).show();

        // close database
        db.close();
        dbStars.close();
    }

    // reload schedule if on schedule screen
    public static void refreshSchedule() {
        // if on schedule screen, reload fragment
        if (fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName().equals(Constants.FRAGMENT_SCHEDULE)) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SchedulePagerFragment.newInstance(11))
                    .addToBackStack(Constants.FRAGMENT_SCHEDULE)
                    .commit();
        }
    }

    // sync schedule with online json schedule
    public static void syncSchedule(Context context) {

        SyncService syncService = new SyncServiceImpl();

        updateCheckDialog.show();

        try {
            syncService.syncDatabase(context, new SyncDatabaseListener(context));
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    // listener for online sync
    private static class SyncDatabaseListener implements AsyncTaskCompleteListener<ApiBase> {

        Context context;
        Date lastUpdatedOnline;
        Date lastUpdatedDevice;

        SyncDatabaseListener(Context context) {
            this.context = context;
        }

        @Override
        public void onTaskComplete(ApiBase result) {

            // get schedule from API
            final OfficialList schedule;
            String updateDate;
            String updateTime;

            try {
                schedule = (OfficialList) ApiResponseUtil.parseResponse(result, OfficialList.class);
            } catch (ApiException e) {

                // dismiss checking dialog
                updateCheckDialog.dismiss();

                // show API error message
                AlertDialog dialog = DialogUtil.apiErrorDialog(context);
                dialog.show();
                return;
            }

            if (schedule.getUpdateDate()!=null && schedule.getUpdateTime()!=null) {

                updateTime = schedule.getUpdateTime();
                updateDate = schedule.getUpdateDate();

                String myFormat = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                // get string of last online update
                final String update = updateDate + " " + updateTime;

                // get string of last device update
                String strDate = SharedPreferencesUtil.getLastUpdated();

                // compare
                try {
                    lastUpdatedOnline = sdf.parse(update);

                    if (strDate!=null) {
                        lastUpdatedDevice = sdf.parse(strDate);
                    }

                    // if device has never been updated or if the
                    // device update is older than last updated online date, update

                    if (lastUpdatedDevice==null || lastUpdatedDevice.compareTo(lastUpdatedOnline) < 0) {

                        // dismiss checking dialog
                        updateCheckDialog.dismiss();

                        // update schedule
                        DialogUtil.updateDialog(schedule, update, context).show();

                    } else { // else, don't update

                        // dismiss checking dialog
                        updateCheckDialog.dismiss();

                        // schedule already up to date
                        Toast.makeText(context,R.string.up_to_date,Toast.LENGTH_SHORT).show();

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    // call async update task
    public static void performUpdate(final OfficialList schedule, final String update, final Context context) {

       AsyncTask task = new UpdateTask(schedule, update, context).execute();

    }

    // convert json to object to put into db
    public static void syncDatabase(ArrayList<Default> officialArray, Context context) {

        HashMap<String, String> queryValues;
        DatabaseAdapter controller = new DatabaseAdapter(context);

        // create gson object
        Gson gson = new GsonBuilder().create();

        // get json array from the response
        JsonArray arr = gson.toJsonTree(officialArray).getAsJsonArray();

        // if there are items
        if(arr.size() != 0){

            // loop through each array element, get json object
            for (int i = 0; i < arr.size(); i++) {

                // get json object
                JsonObject obj = arr.get(i).getAsJsonObject();

                // create queryvalues object to insert into SQLite
                queryValues = new HashMap<>();

                // add values to object
                queryValues.put("id", obj.get("id").getAsString());
                queryValues.put("title", obj.get("title").getAsString());
                queryValues.put("who", obj.get("who").getAsString());
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

                // insert object into database
                controller.updateDatabase(queryValues);

            }
        }

    }

    // export sqlite database to SD card
    private void exportDB() {

        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String currentDBPath= getResources().getString(R.string.db_path) + getResources().getString(R.string.db_name);
                String backupDBPath  = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.db_name);
                File currentDB = new File(currentDBPath);
                File backupDB = new File(backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

}
