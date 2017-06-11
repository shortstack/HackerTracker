package com.shortstack.hackertracker.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.github.stkent.amplify.tracking.Amplify;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Analytics.AnalyticsController;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.BottomSheet.ReviewBottomSheetDialogFragment;
import com.shortstack.hackertracker.Fragment.HomeFragment;
import com.shortstack.hackertracker.Fragment.InformationFragment;
import com.shortstack.hackertracker.Fragment.ScheduleFragment;
import com.shortstack.hackertracker.Fragment.SettingsFragment;
import com.shortstack.hackertracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int DEFAULT_FRAGMENT_INDEX = 1;
    public static final int NAV_HOME = 0;
    public static final int NAV_SCHEDULE = 1;
    public static final int NAV_MAP = 2;
    public static final int NAV_INFORMATION = 3;
    public static final int NAV_VENDORS = 4;
    public static final int NAV_SETTINGS = 5;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @Bind(R.id.tabs)
//    TabLayout tabLayout;
//    @Bind(R.id.viewpager)
//    ViewPager viewPager;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavView;

    @BindView(R.id.filter)
    FloatingActionButton fab;


    private int mFragmentIndex = DEFAULT_FRAGMENT_INDEX;

    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        mHandler = new Handler();


        initViewPager();


        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);




        if (savedInstanceState == null) {
            mFragmentIndex = App.getStorage().getViewPagerPosition();

            mFragmentIndex = 1;

            forceMenuHighlighted();
            loadFragment();
        }
    }

    private void forceMenuHighlighted() {
        mNavView.getMenu().getItem(mFragmentIndex).setChecked(true);
    }

    private void loadFragment() {
        // set toolbar title
        //setToolbarTitle();

        Logger.d("Setting fragment:" + mFragmentIndex);

        App.getStorage().setViewPagerPosition(mFragmentIndex);
        tagAnalytics();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(getFragmentTag()) != null) {
            mDrawerLayout.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getCurrentFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, getFragmentTag());
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        invalidateOptionsMenu();

        getSupportActionBar().setTitle(getFragmentTitle());


        updateFABVisibility();

        //Closing drawer on item click
        mDrawerLayout.closeDrawers();


        if (Amplify.getSharedInstance().shouldPrompt()) {
            ReviewBottomSheetDialogFragment review = ReviewBottomSheetDialogFragment.newInstance();
            review.show(this.getSupportFragmentManager(), review.getTag());
        }
    }

    private void tagAnalytics() {
        App.getApplication().getAnalyticsController().tagCustomEvent(getFragmentEvent());
    }

    private AnalyticsController.Analytics getFragmentEvent() {
        switch (mFragmentIndex) {
            default:
            case NAV_HOME:
                return AnalyticsController.Analytics.FRAGMENT_HOME;

            case NAV_SCHEDULE:
                return AnalyticsController.Analytics.FRAGMENT_SCHEDULE;

            case NAV_MAP:
                return AnalyticsController.Analytics.FRAGMENT_MAP;

            case NAV_INFORMATION:
                return AnalyticsController.Analytics.FRAGMENT_INFO;

            case NAV_VENDORS:
                return AnalyticsController.Analytics.FRAGMENT_COMPANIES;

            case NAV_SETTINGS:
                return AnalyticsController.Analytics.FRAGMENT_SETTINGS;
        }
    }

    private void updateFABVisibility() {
        fab.setVisibility(mFragmentIndex == 1 ? View.VISIBLE : View.GONE);
    }

    private Fragment getCurrentFragment() {
        switch (mFragmentIndex) {
            default:
            case NAV_HOME:
                return HomeFragment.Companion.newInstance();

            case NAV_SCHEDULE:
                return ScheduleFragment.newInstance();

            case NAV_MAP:
                return MapsFragment.Companion.newInstance();

            case NAV_INFORMATION:
                return InformationFragment.Companion.newInstance();

            case NAV_VENDORS:
                return VendorsFragment.Companion.newInstance();

            case NAV_SETTINGS:
                return SettingsFragment.Companion.newInstance();
        }
    }


    @OnClick(R.id.filter)
    public void onFilterClick() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof ScheduleFragment) {
                ((ScheduleFragment) fragment).showFilters();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if( mFragmentIndex == NAV_SETTINGS ) {
                mFragmentIndex = NAV_SCHEDULE;
                loadFragment();
                return;
            } else {
                mFragmentIndex = NAV_SETTINGS;
                loadFragment();
                return;
            }

//            if (mFragmentIndex != DEFAULT_FRAGMENT_INDEX) {
//                mFragmentIndex = DEFAULT_FRAGMENT_INDEX;
//                loadFragment();
//                return;
//            }
        }

        super.onBackPressed();
    }

    private void initViewPager() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mFragmentIndex = getFragmentIndex(item);

        Logger.d("Selected item! " + mFragmentIndex);

        loadFragment();
        return true;
    }

    private int getFragmentIndex(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                return NAV_HOME;
            case R.id.nav_schedule:
                return NAV_SCHEDULE;
            case R.id.nav_map:
                return NAV_MAP;
            case R.id.nav_information:
                return NAV_INFORMATION;

            case R.id.nav_companies:
                return NAV_VENDORS;
            case R.id.nav_settings:
                return NAV_SETTINGS;
        }

        throw new IllegalStateException("Fragment out of bounds. Could not locate id." + item.getItemId());
    }

    private String getFragmentTag() {
        return "home_fragment_" + mFragmentIndex;
    }

    private String getFragmentTitle() {
        return activityTitles[mFragmentIndex];
    }

    public void loadFragment(int fragment) {
        mFragmentIndex = fragment;
        forceMenuHighlighted();
        loadFragment();
    }
}
