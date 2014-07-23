package com.shortstack.hackertracker.Activity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Contests.ContestPagerFragment;
import com.shortstack.hackertracker.Events.EventPagerFragment;
import com.shortstack.hackertracker.Misc.FAQFragment;
import com.shortstack.hackertracker.Misc.HomeFragment;
import com.shortstack.hackertracker.Misc.LinksFragment;
import com.shortstack.hackertracker.Misc.MapsFragment;
import com.shortstack.hackertracker.Misc.NavigationDrawerFragment;
import com.shortstack.hackertracker.Misc.SearchFragment;
import com.shortstack.hackertracker.Misc.ShuttleFragment;
import com.shortstack.hackertracker.Parties.PartyPagerFragment;
import com.shortstack.hackertracker.Schedule.SchedulePagerFragment;
import com.shortstack.hackertracker.Utils.DialogUtil;
import com.shortstack.hackertracker.Vendors.VendorsFragment;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Speakers.SpeakerPagerFragment;

public class HomeActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static FragmentManager fragmentManager;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private boolean isSchedule = false;
    private boolean isSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        // update the main content by replacing fragments
        fragmentManager = getSupportFragmentManager();

        switch (position)
        {
            case 0:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance(1))
                        .addToBackStack("HomeFragment")
                        .commit();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, SpeakerPagerFragment.newInstance(2))
                        .addToBackStack("SpeakerPagerFragment")
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContestPagerFragment.newInstance(3))
                        .addToBackStack("ContestPagerFragment")
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, EventPagerFragment.newInstance(4))
                        .addToBackStack("EventPagerFragment")
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PartyPagerFragment.newInstance(5))
                        .addToBackStack("PartyPagerFragment")
                        .commit();
                break;
            case 5:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, VendorsFragment.newInstance(6))
                        .addToBackStack("VendorsFragment")
                        .commit();
                break;
            case 6:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, MapsFragment.newInstance(7))
                        .addToBackStack("MapsFragment")
                        .commit();
                break;
            case 7:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ShuttleFragment.newInstance(10))
                        .addToBackStack("ShuttleFragment")
                        .commit();
                break;
            case 8:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FAQFragment.newInstance(8))
                        .addToBackStack("FAQFragment")
                        .commit();
                break;
            case 9:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, LinksFragment.newInstance(11))
                        .addToBackStack("LinksFragment")
                        .commit();
                break;
        }
    }

    public static void refreshSchedule() {
        fragmentManager.beginTransaction()
                .replace(R.id.container, SchedulePagerFragment.newInstance(9))
                .addToBackStack("SchedulePagerFragment")
                .commit();
    }


    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.home);
                break;
            case 2:
                mTitle = getString(R.string.speakers);
                break;
            case 3:
                mTitle = getString(R.string.contests);
                break;
            case 4:
                mTitle = getString(R.string.events);
                break;
            case 5:
                mTitle = getString(R.string.parties);
                break;
            case 6:
                mTitle = getString(R.string.vendors);
                break;
            case 7:
                mTitle = getString(R.string.maps);
                break;
            case 8:
                mTitle = getString(R.string.faq);
                break;
            case 9:
                mTitle = getString(R.string.schedule);
                break;
            case 10:
                mTitle = getString(R.string.shuttle);
                break;
            case 11:
                mTitle = getString(R.string.links);
                break;
            case 12:
                mTitle = getString(R.string.search);

                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public void setTitle() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.home, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == R.id.action_schedule) {
            isSchedule = true;
            getActionBar().setTitle(getString(R.string.schedule));
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SchedulePagerFragment.newInstance(9))
                    .addToBackStack("SchedulePagerFragment")
                    .commit();
            toggleMenu();
            return true;
        } else if (item.getItemId() == R.id.action_search) {
            isSearch = true;
            getActionBar().setTitle(getString(R.string.search));
            fragmentManager.beginTransaction()
                    .replace(R.id.container, SearchFragment.newInstance(12))
                    .addToBackStack("SearchFragment")
                    .commit();
            toggleMenu();
            return true;
        } else if (id == R.id.action_share) {
            DialogUtil.shareScheduleDialog(this).show();
            return true;
        } else if (id == R.id.action_clear) {
            DialogUtil.clearScheduleDialog(this).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // if not responding to navigation bar, toggle menu items
        if (menu.findItem(R.id.action_schedule)!=null) {
            restoreActionBar();

            if (isSchedule) {
                menu.findItem(R.id.action_schedule).setVisible(false);
                menu.findItem(R.id.action_search).setVisible(true);
                menu.findItem(R.id.action_clear).setVisible(true);
                menu.findItem(R.id.action_share).setVisible(true);
                isSchedule = false;
            } else if (isSearch) {
                menu.findItem(R.id.action_search).setVisible(false);
                menu.findItem(R.id.action_clear).setVisible(false);
                menu.findItem(R.id.action_share).setVisible(false);
                isSearch = false;
            }
        }

        return super.onPrepareOptionsMenu(menu);
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
    public void onBackPressed(){

        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {

            FragmentName fragName = FragmentName.valueOf(fm.getBackStackEntryAt(fm.getBackStackEntryCount()-2).getName());
            switch (fragName) {
                case HomeFragment:
                    getActionBar().setTitle(getString(R.string.home));
                    break;
                case SpeakerPagerFragment:
                    getActionBar().setTitle(getString(R.string.speakers));
                    break;
                case ContestPagerFragment:
                    getActionBar().setTitle(getString(R.string.contests));
                    break;
                case EventPagerFragment:
                    getActionBar().setTitle(getString(R.string.events));
                    break;
                case PartyPagerFragment:
                    getActionBar().setTitle(getString(R.string.parties));
                    break;
                case VendorsFragment:
                    getActionBar().setTitle(getString(R.string.vendors));
                    break;
                case MapsFragment:
                    getActionBar().setTitle(getString(R.string.maps));
                    break;
                case LinksFragment:
                    getActionBar().setTitle(getString(R.string.links));
                    break;
                case FAQFragment:
                    getActionBar().setTitle(getString(R.string.faq));
                    break;
                case ShuttleFragment:
                    getActionBar().setTitle(getString(R.string.shuttle));
                    break;
                case SchedulePagerFragment:
                    getActionBar().setTitle(getString(R.string.schedule));
                    break;

            }
            fm.popBackStack();
        } else {
            finish();
        }

    }

    public static void clearSchedule(Context context) {
        SQLiteDatabase dbStars = HackerTrackerApplication.myDbHelperStars.getWritableDatabase();
        SQLiteDatabase db = HackerTrackerApplication.myDbHelper.getWritableDatabase();

        // delete all data in starred database
        dbStars.execSQL("DELETE FROM data");

        // update all data in main database to not be starred
        db.execSQL("UPDATE data SET starred=0");

        // reload screen
        HomeActivity.refreshSchedule();

        Toast.makeText(context, R.string.schedule_cleared, Toast.LENGTH_SHORT).show();
        db.close();
        dbStars.close();
    }

}
