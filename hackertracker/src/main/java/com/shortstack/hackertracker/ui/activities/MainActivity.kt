package com.shortstack.hackertracker.ui.activities

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.github.stkent.amplify.tracking.Amplify
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.Filter
import com.shortstack.hackertracker.replaceFragment
import com.shortstack.hackertracker.ui.ReviewBottomSheet
import com.shortstack.hackertracker.ui.SearchFragment
import com.shortstack.hackertracker.ui.SettingsFragment
import com.shortstack.hackertracker.ui.home.HomeFragment
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.ui.maps.MapsFragment
import com.shortstack.hackertracker.ui.schedule.EventBottomSheet
import com.shortstack.hackertracker.ui.schedule.ScheduleFragment
import com.shortstack.hackertracker.ui.vendors.VendorsFragment
import com.shortstack.hackertracker.utils.MaterialAlert
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*


open class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private var mFragmentIndex = DEFAULT_FRAGMENT_INDEX

    private val titles: Array<String> by lazy { resources.getStringArray(R.array.nav_item_activity_titles) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (!DatabaseController.exists(this, App.application.databaseController.databaseName)) {
//            startActivity(Intent(this, SplashActivity::class.java))
//            finish()
//            return
//        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initViewPager()

        filter.setOnClickListener { onFilterClick() }

        if (savedInstanceState == null) {
            mFragmentIndex = App.application.storage.viewPagerPosition
            forceMenuHighlighted()
            loadFragment()

            if (Amplify.getSharedInstance().shouldPrompt()) {
                val review = ReviewBottomSheet.newInstance()
                review.show(this.supportFragmentManager, review.tag)
            }
        }

        handleIntent(intent)


        setNavHeaderMargin()
    }

    private fun setNavHeaderMargin() {
        val params = nav_view.getHeaderView(0).imageView.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = getStatusBarHeight()
    }


    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(App.application.storage.databaseTheme, true)
        return theme
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null || intent.extras == null)
            return

        val target = intent.extras.getInt("target")

        if (target == 0)
            return

        App.application.database.db.eventDao().getEventById(id = target)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val fragment = EventBottomSheet.newInstance(it)
                    fragment.show(supportFragmentManager, fragment.tag)
                }
    }

    private fun forceMenuHighlighted() {
        val menu = nav_view!!.menu
        if (menu.size() > mFragmentIndex)
            menu.getItem(mFragmentIndex).isChecked = true
    }

    private fun loadFragment() {
//        if (supportFragmentManager.contains(fragmentTag)) {
//            drawer_layout!!.closeDrawers()
//            return
//        }

        replaceFragment(currentFragment, fragmentTitle, fragmentTag, R.id.frame)

        val app = App.application

        app.storage.viewPagerPosition = mFragmentIndex
        app.analyticsController.tagCustomEvent(fragmentEvent)

        updateFABVisibility()

        //Closing drawer on item click
        drawer_layout!!.closeDrawers()
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
        filter!!.visibility = View.VISIBLE
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

        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                val fragment = SearchFragment.newInstance()

                val searchView = item.actionView as SearchView
                searchView.setOnQueryTextListener(fragment)

                replaceFragment(fragment, fragmentTitle, fragmentTag, R.id.frame)

                item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                        loadFragment()
                        return true
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initViewPager() {

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout!!.setDrawerListener(toggle)
        toggle.syncState()

        nav_view!!.setNavigationItemSelectedListener(this)
//        if (App.application.databaseController.databaseName != Constants.DEFCON_DATABASE_NAME) {
//            nav_view.menu.getItem(2).setTitle(R.string.map)
//        }
//
//        if (App.application.databaseController.databaseName == Constants.TOORCON_DATABASE_NAME) {
//            nav_view.menu.removeItem(R.id.nav_information)
//        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_change_con) {
            onChangeCon()
            return true
        }

        setFragmentIndex(item)
        loadFragment()

        return true
    }

    fun changeCon() {
        onChangeCon()
    }

    private fun onChangeCon() {

        val cons = App.application.database.cons


        App.application.database.cons.conferenceDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({


                    val current = it.first { it.isSelected }

                    val filtered = it.filter { !it.isSelected }
                    val items = filtered.map { MaterialAlert.Item(it.title) }

                    MaterialAlert.create(this).setTitle(current.title).setItems(items,
                            DialogInterface.OnClickListener { dialogInterface, i ->

                                Single.fromCallable {
                                    val con = filtered[i]

                                    current.isSelected = false
                                    con.isSelected = true

                                    cons.conferenceDao().update(current)
                                    cons.conferenceDao().update(con)

                                    App.application.database.initConference(con)

                                }.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            dialogInterface.dismiss()
                                            Logger.e("Finished!")
                                        }, {
                                            Logger.e("Could not update the current database. " + it.message)

                                        })


                            }).show()


                }, {
                    Logger.e("Could not update the current database. " + it.message)
                })


        forceMenuHighlighted()
    }

    private fun setFragmentIndex(item: MenuItem) {
        mFragmentIndex = getFragmentIndex(item)
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

        throw IllegalStateException("Could not find fragment with id: ${item.itemId}.")
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

    protected fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
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
