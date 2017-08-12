package com.shortstack.hackertracker.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import butterknife.ButterKnife
import com.github.stkent.amplify.tracking.Amplify
import com.shortstack.hackertracker.Analytics.AnalyticsController
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.BottomSheet.ReviewBottomSheetDialogFragment
import com.shortstack.hackertracker.BottomSheet.ScheduleItemBottomSheetDialogFragment
import com.shortstack.hackertracker.Database.DatabaseController
import com.shortstack.hackertracker.Fragment.*
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mFragmentIndex = DEFAULT_FRAGMENT_INDEX

    private val titles: Array<String> by lazy { resources.getStringArray(R.array.nav_item_activity_titles) }

    // flag to load home fragment when user presses back key
    private val shouldLoadHomeFragOnBackPress = true
    private var mHandler: Handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if( !DatabaseController.exists(this) ) {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
            return
        }



        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        initViewPager()

        filter.setOnClickListener { onFilterClick() }

        if (savedInstanceState == null) {
            mFragmentIndex = App.storage.viewPagerPosition
            forceMenuHighlighted()
            loadFragment()

            if (Amplify.getSharedInstance().shouldPrompt()) {
                val review = ReviewBottomSheetDialogFragment.newInstance()
                review.show(this.supportFragmentManager, review.tag)
            }
        }

        handleIntent(intent)
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent != null && intent.extras != null) {
            val target = intent.extras.getInt("target")

            val item = App.application.databaseController.getScheduleItemFromId(id = target)
            if (item != null) {

                val newInstance = ScheduleItemBottomSheetDialogFragment.newInstance(item)
                newInstance.show(supportFragmentManager, newInstance.tag)

            }
        }
    }

    private fun forceMenuHighlighted() {
        nav_view!!.menu.getItem(mFragmentIndex).isChecked = true
    }

    private fun loadFragment() {
        // set toolbar title
        //setToolbarTitle();

//        Logger.d("Setting fragment:" + mFragmentIndex)

        App.storage.viewPagerPosition = mFragmentIndex
        tagAnalytics()

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (supportFragmentManager.findFragmentByTag(fragmentTag) != null) {
            drawer_layout!!.closeDrawers()
            return
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        val mPendingRunnable = Runnable {
            // update the main content by replacing fragments
            val fragment = currentFragment
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                    android.R.anim.fade_out)
            fragmentTransaction.replace(R.id.frame, fragment, fragmentTag)
            fragmentTransaction.commitAllowingStateLoss()
        }

        // If mPendingRunnable is not null, then add to the message queue
        mHandler.post(mPendingRunnable)

        invalidateOptionsMenu()

        supportActionBar!!.title = fragmentTitle


        updateFABVisibility()

        //Closing drawer on item click
        drawer_layout!!.closeDrawers()



    }

    private fun tagAnalytics() {
        App.application.analyticsController.tagCustomEvent(fragmentEvent)
    }

    private val fragmentEvent: AnalyticsController.Analytics
        get() {
            return when (mFragmentIndex) {
                NAV_HOME -> AnalyticsController.Analytics.FRAGMENT_HOME

                NAV_SCHEDULE -> AnalyticsController.Analytics.FRAGMENT_SCHEDULE

                NAV_MAP -> AnalyticsController.Analytics.FRAGMENT_MAP

                NAV_INFORMATION -> AnalyticsController.Analytics.FRAGMENT_INFO

                NAV_VENDORS -> AnalyticsController.Analytics.FRAGMENT_COMPANIES

                NAV_SETTINGS -> AnalyticsController.Analytics.FRAGMENT_SETTINGS

                else -> throw IllegalStateException("Could not locale the correct fragment from index $mFragmentIndex.")
            }
        }

    private fun updateFABVisibility() {
        filter!!.visibility = if (mFragmentIndex == 1) View.VISIBLE else View.GONE
    }

    private val currentFragment: Fragment
        get() {
            return when (mFragmentIndex) {
                NAV_HOME -> HomeFragment.newInstance()

                NAV_SCHEDULE -> ScheduleFragment.newInstance()

                NAV_MAP -> MapsFragment.newInstance()

                NAV_INFORMATION -> InformationFragment.newInstance()

                NAV_VENDORS -> VendorsFragment.newInstance()

                NAV_SETTINGS -> SettingsFragment.newInstance()

                else -> throw IllegalStateException("Could not locate the correct fragment for index $mFragmentIndex.")
            }


        }


    fun onFilterClick() {
        for (fragment in supportFragmentManager.fragments) {
            if (fragment is ScheduleFragment) {
                fragment.showFilters()
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout!!.isDrawerOpen(GravityCompat.START)) {
            drawer_layout!!.closeDrawers()
            return
        }

        if (shouldLoadHomeFragOnBackPress) {
            if (mFragmentIndex != DEFAULT_FRAGMENT_INDEX) {
                mFragmentIndex = DEFAULT_FRAGMENT_INDEX
                loadFragment()
                return
            }
        }

        super.onBackPressed()
    }

    private fun initViewPager() {

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout!!.setDrawerListener(toggle)
        toggle.syncState()

        nav_view!!.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mFragmentIndex = getFragmentIndex(item)

//        Logger.d("Selected item! " + mFragmentIndex)

        loadFragment()
        return true
    }

    private fun getFragmentIndex(item: MenuItem): Int {

        when (item.itemId) {
            R.id.nav_home -> return NAV_HOME
            R.id.nav_schedule -> return NAV_SCHEDULE
            R.id.nav_map -> return NAV_MAP
            R.id.nav_information -> return NAV_INFORMATION

            R.id.nav_companies -> return NAV_VENDORS
            R.id.nav_settings -> return NAV_SETTINGS
        }

        throw IllegalStateException("Could not find fragment with id: $item.itemId.")
    }

    private val fragmentTag: String
        get() = "home_fragment_" + mFragmentIndex

    private val fragmentTitle: String
        get() = titles[mFragmentIndex]

    fun loadFragment(fragment: Int) {
        mFragmentIndex = fragment
        forceMenuHighlighted()
        loadFragment()
    }

    companion object {

        val DEFAULT_FRAGMENT_INDEX = 1
        val NAV_HOME = 0
        val NAV_SCHEDULE = 1
        val NAV_MAP = 2
        val NAV_INFORMATION = 3
        val NAV_VENDORS = 4
        val NAV_SETTINGS = 5
    }
}
