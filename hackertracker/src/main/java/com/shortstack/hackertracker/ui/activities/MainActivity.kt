package com.shortstack.hackertracker.ui.activities


import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.stkent.amplify.tracking.Amplify
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.replaceFragment
import com.shortstack.hackertracker.ui.SearchFragment
import com.shortstack.hackertracker.ui.events.EventFragment
import com.shortstack.hackertracker.ui.home.HomeFragment
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.ui.information.speakers.SpeakerFragment
import com.shortstack.hackertracker.ui.maps.MapsFragment
import com.shortstack.hackertracker.ui.schedule.ScheduleFragment
import com.shortstack.hackertracker.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.row_nav_view.*
import kotlinx.android.synthetic.main.view_filter.*


class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    companion object {

        var isDarkMode = false
    }


    private lateinit var bottomSheet: BottomSheetBehavior<View>

    private lateinit var viewModel: MainActivityViewModel

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val map = HashMap<Int, Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isDarkMode) {
            setTheme(R.style.AppTheme_Dark)

        } else {
            setTheme(R.style.AppTheme)
        }



        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        initNavDrawer()

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        viewModel.conference.observe(this, Observer {
            if (it != null) {
                nav_view.getHeaderView(0).nav_title.text = it.name
            }
        })

        viewModel.types.observe(this, Observer {

            val hasContest = it.firstOrNull { it.name == "Contest" } != null
            nav_view.menu.findItem(R.id.nav_contests).isVisible = hasContest

            val hasWorkshops = it.firstOrNull { it.name == "Workshop" } != null
            nav_view.menu.findItem(R.id.nav_workshops).isVisible = hasWorkshops

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


            setMainFragment(R.id.nav_schedule, getString(R.string.schedule), false)
        }


        supportFragmentManager.addOnBackStackChangedListener(this)


        ViewCompat.setTranslationZ(filters, 10f)
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getThemeAccentColor(this)
        }
    }

    private fun getThemeAccentColor(context: Context, theme: Resources.Theme = context.theme): Int {
        val colorAttr =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    android.R.attr.colorBackground
                } else {
                    //Get colorAccent defined for AppCompat
                    context.resources.getIdentifier("colorBackground", "attr", context.packageName)
                }
        val outValue = TypedValue()
        theme.resolveAttribute(colorAttr, outValue, true)
        return outValue.data
    }


    override fun onStart() {
        super.onStart()
        auth.signInAnonymously().addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Logger.d("Successfully signed in. ${it.result}")

            } else {
                Logger.e("Could not sign in.")
            }
        }
    }

    private lateinit var toggle: ActionBarDrawerToggle


    private fun initNavDrawer() {
//        toggle = ActionBarDrawerToggle(
//                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        drawer_layout.addDrawerListener(toggle)
//        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        if (isDarkMode) {
            theme.applyStyle(R.style.AppTheme_Dark, true)
        } else {
            theme.applyStyle(R.style.AppTheme, true)
        }

        return theme
    }

    private fun setFABVisibility(visibility: Int) {
        if (visibility == View.VISIBLE) {
            filter.show()
        } else {
            filter.hide()
        }
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
        val drawerOpen = drawer_layout.isDrawerOpen(GravityCompat.START)
        when {
            drawerOpen -> drawer_layout.closeDrawers()
            !drawerOpen -> drawer_layout.openDrawer(GravityCompat.START)
            bottomSheet.state != BottomSheetBehavior.STATE_HIDDEN -> hideFilters()
            else -> super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                setMainFragment(item.itemId, addToBackStack = true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMainFragment(id: Int, title: String? = null, addToBackStack: Boolean) {
        val visibility = if (id == R.id.nav_schedule) View.VISIBLE else View.INVISIBLE
        setFABVisibility(visibility)

        replaceFragment(getFragment(id), R.id.container, backStack = addToBackStack)

        title?.let {
            supportActionBar?.title = it
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.groupId == R.id.nav_cons) {
            viewModel.changeConference(item.itemId)
        } else {
            setMainFragment(item.itemId, item.title.toString(), false)
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
                R.id.nav_contests -> ScheduleFragment.newInstance(Type(40003, "Contests", "DEFCON27", "", true))
                R.id.nav_workshops -> ScheduleFragment.newInstance(Type(40001, "Workshops", "DEFCON27", "", true))
                R.id.nav_settings -> SettingsFragment.newInstance()
                R.id.search -> SearchFragment.newInstance()
                else -> InformationFragment.newInstance()
            }
        }
        return map[id]!!
    }

    fun navigate(event: Event) {
        replaceFragment(EventFragment.newInstance(event), R.id.container_above, hasAnimation = true)
    }

    fun navigate(speaker: Speaker?) {
        speaker ?: return
        replaceFragment(SpeakerFragment.newInstance(speaker), R.id.container_above, hasAnimation = true)
    }

    fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    override fun onBackStackChanged() {
        val fragments = supportFragmentManager.fragments
        val last = fragments.lastOrNull()

        if (last is EventFragment || last is SpeakerFragment) {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            container.visibility = View.INVISIBLE
            setFABVisibility(View.INVISIBLE)
        } else {
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            container.visibility = View.VISIBLE
            if (last is ScheduleFragment) {
                setFABVisibility(View.VISIBLE)
            } else {
                setFABVisibility(View.INVISIBLE)
            }
        }
    }
}
