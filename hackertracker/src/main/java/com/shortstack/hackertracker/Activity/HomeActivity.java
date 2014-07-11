package com.shortstack.hackertracker.Activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.shortstack.hackertracker.Contests.ContestPagerFragment;
import com.shortstack.hackertracker.Events.EventPagerFragment;
import com.shortstack.hackertracker.Misc.FAQFragment;
import com.shortstack.hackertracker.Misc.HomeFragment;
import com.shortstack.hackertracker.Misc.MapsFragment;
import com.shortstack.hackertracker.Misc.NavigationDrawerFragment;
import com.shortstack.hackertracker.Misc.ShuttleFragment;
import com.shortstack.hackertracker.Parties.PartyPagerFragment;
import com.shortstack.hackertracker.Schedule.SchedulePagerFragment;
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

        }
    }

    public void restoreActionBar() {
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("HomeActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("HomeActivity", "nothing on backstack, calling super");
            super.onBackPressed();
        }
    }

}
