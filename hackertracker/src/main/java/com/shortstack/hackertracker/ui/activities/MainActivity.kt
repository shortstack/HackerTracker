package com.shortstack.hackertracker.ui.activities


import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.State
import androidx.work.WorkManager
import com.github.stkent.amplify.tracking.Amplify
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.*
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.Speaker
import com.shortstack.hackertracker.network.task.SyncWorker
import com.shortstack.hackertracker.ui.SearchFragment
import com.shortstack.hackertracker.ui.SettingsFragment
import com.shortstack.hackertracker.ui.events.EventFragment
import com.shortstack.hackertracker.ui.home.HomeFragment
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.ui.maps.MapsFragment
import com.shortstack.hackertracker.ui.schedule.ScheduleFragment
import com.shortstack.hackertracker.ui.speakers.SpeakerFragment
import com.shortstack.hackertracker.ui.vendors.VendorsFragment
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import com.shortstack.hackertracker.utils.TickTimer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.row_nav_view.*
import kotlinx.android.synthetic.main.view_filter.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {


    @Inject
    lateinit var storage: SharedPreferencesUtil

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var analytics: AnalyticsController

    @Inject
    lateinit var timer: TickTimer

    private lateinit var bottomSheet: BottomSheetBehavior<View>

    private val map = HashMap<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        App.application.component.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initNavDrawer()

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


        supportFragmentManager.addOnBackStackChangedListener(this)

        setMainFragment(R.id.nav_schedule, getString(R.string.schedule))
    }

    private fun scheduleSyncTask() {
        val scheduled = WorkManager.getInstance()?.getStatusesByTag(SyncWorker.TAG_SYNC)

        scheduled?.observe(this, Observer {
            if (it == null || !it.any { it.state == State.ENQUEUED || it.state == State.RUNNING }) {
                App.application.scheduleSyncTask()
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

    private lateinit var toggle: ActionBarDrawerToggle


    private fun initNavDrawer() {
        toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

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
                setMainFragment(item.itemId, item.title.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMainFragment(id: Int, title: String) {
        val visibility = if (id == R.id.nav_schedule) View.VISIBLE else View.INVISIBLE
        setFABVisibility(visibility)

        replaceFragment(getFragment(id), R.id.container)

        supportActionBar?.title = title
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.groupId == R.id.nav_cons) {
            val con = database.getConferences().firstOrNull { it.conference.id == item.itemId }
            if (con != null) database.changeConference(con)
        } else {
            setMainFragment(item.itemId, item.title.toString())
        }

        drawer_layout.closeDrawers()
        return true
    }

    private fun getFragment(id: Int): Fragment {
        if (map[id] == null) {
            map[id] = when (id) {
                R.id.nav_home -> HomeFragment.newInstance()
                R.id.nav_schedule -> ScheduleFragment.newInstance()
                R.id.nav_map -> MapsFragment.newInstance()
                R.id.nav_companies -> VendorsFragment.newInstance()
                R.id.nav_settings -> SettingsFragment.newInstance()
                R.id.search -> SearchFragment.newInstance()
                else -> InformationFragment.newInstance()
            }
        }
        return map[id]!!
    }

    fun navigate(event: DatabaseEvent?) {
        if (event == null)
            return

        replaceFragment(EventFragment.newInstance(event), R.id.container_above)
    }

    fun navigate(speaker: Speaker) {

        replaceFragment(SpeakerFragment.newInstance(speaker), R.id.container_above)
    }

    fun popBackStack() {
        supportFragmentManager.popBackStack()
    }


    override fun onBackStackChanged() {
        val fragments = supportFragmentManager.fragments
        val last = fragments.lastOrNull()

        if (last is EventFragment || last is SpeakerFragment) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            window?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

                    statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                }
            }
        }
    }
}
