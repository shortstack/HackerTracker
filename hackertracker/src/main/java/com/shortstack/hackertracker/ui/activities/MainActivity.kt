package com.shortstack.hackertracker.ui.activities


import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.work.State
import androidx.work.WorkManager
import com.github.stkent.amplify.tracking.Amplify
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.network.task.SyncWorker
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import com.shortstack.hackertracker.utils.TickTimer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.row_nav_view.*
import kotlinx.android.synthetic.main.view_filter.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener {

    @Inject
    lateinit var storage: SharedPreferencesUtil

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var analytics: AnalyticsController

    @Inject
    lateinit var timer: TickTimer

    lateinit var navController: NavController

    private lateinit var bottomSheet: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        App.application.component.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setupNavigation()

        val mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        mainActivityViewModel.conference.observe(this, Observer {
            if (it != null) {
                nav_view.getHeaderView(0).nav_title.text = it.conference.name
            }
        })
        mainActivityViewModel.conferences.observe(this, Observer {

            nav_view.menu.removeGroup(R.id.nav_cons)
//
//            if (BuildConfig.DEBUG) {
//                it?.forEach {
//                    nav_view.menu.add(R.id.nav_cons, it.id, 0, it.name).apply {
//                        isChecked = it.isSelected
//                        icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_chevron_right_white_24dp)
//                    }
//                }
//            }
        })

        scheduleSyncTask()

        database.typesLiveData.observe(this, Observer {
            filters.setTypes(it)
        })

        bottomSheet = BottomSheetBehavior.from(filters)
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        filter.setOnClickListener { expandFilters() }
        close.setOnClickListener { hideFilters() }

        if (savedInstanceState == null) {
            if (Amplify.getSharedInstance().shouldPrompt() && !BuildConfig.DEBUG) {
                val review = ReviewBottomSheet.newInstance()
                review.show(this.supportFragmentManager, review.tag)
            }
        }
    }

    private fun scheduleSyncTask() {
        val scheduled = WorkManager.getInstance()?.getStatusesByTag(SyncWorker.TAG_SYNC)

        scheduled?.observe(this, Observer {
            if (it == null || !it.any { it.state == State.ENQUEUED || it.state == State.RUNNING }) {
                if (!storage.syncingDisabled) {
                    App.application.scheduleSyncTask()
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        timer.start()
    }

    override fun onPause() {
        timer.stop()
        super.onPause()
    }

    private fun setupNavigation() {
        initNavDrawer()


        navController = findNavController(R.id.my_nav_host_fragment)
        setupActionBarWithNavController(this, navController, drawer_layout)
//        NavigationUI.setupWithNavController(nav_view, navController)

        navController.addOnNavigatedListener { _, destination ->
            val visibility = if (destination.id == R.id.nav_schedule) View.VISIBLE else View.INVISIBLE
            setFABVisibility(visibility)
        }

    }


    private fun initNavDrawer() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(R.style.AppTheme, true)
        return theme
    }

    private fun setFABVisibility(visibility: Int) {
        filter.visibility = visibility
    }

    private fun expandFilters() {
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideFilters() {
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(Gravity.START) -> drawer_layout.closeDrawers()
            bottomSheet.state != BottomSheetBehavior.STATE_HIDDEN -> hideFilters()
            else -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                navController.navigate(R.id.nav_search)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.groupId == R.id.nav_cons) {
            val con = database.getConferences().firstOrNull { it.conference.id == item.itemId }
            if (con != null) database.changeConference(con)
        } else {
            val current = navController.currentDestination.id
            if (item.itemId != current) {
                navController.navigate(item.itemId)
            }
        }

        drawer_layout.closeDrawers()
        return true
    }
}
