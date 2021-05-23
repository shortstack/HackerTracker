package com.shortstack.hackertracker.ui.activities


import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.stkent.amplify.tracking.Amplify
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ActivityMainBinding
import com.shortstack.hackertracker.models.local.Event
import com.shortstack.hackertracker.models.local.Location
import com.shortstack.hackertracker.models.local.Speaker
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.replaceFragment
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.events.EventFragment
import com.shortstack.hackertracker.ui.home.HomeFragment
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.ui.information.categories.CategoryFragment
import com.shortstack.hackertracker.ui.information.speakers.SpeakerFragment
import com.shortstack.hackertracker.ui.maps.MapsFragment
import com.shortstack.hackertracker.ui.schedule.ScheduleFragment
import com.shortstack.hackertracker.ui.search.SearchFragment
import com.shortstack.hackertracker.ui.settings.SettingsFragment
import com.shortstack.hackertracker.ui.themes.ThemesManager.Theme.*
import com.shortstack.hackertracker.utilities.Storage
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener,
    FragmentManager.OnBackStackChangedListener {

    private lateinit var binding: ActivityMainBinding

    private val storage: Storage by inject()

    private lateinit var viewModel: HackerTrackerViewModel

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val map = HashMap<Int, Fragment>()

    private var secondaryVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initNavDrawer()

        viewModel = ViewModelProvider(this)[HackerTrackerViewModel::class.java]
        viewModel.conference.observe(this, Observer {
            if (it != null) {
                binding.navView.getHeaderView(0).findViewById<TextView>(R.id.nav_title).text = it.data?.name
            }
        })

        if (savedInstanceState == null) {
            if (Amplify.getSharedInstance().shouldPrompt() && !BuildConfig.DEBUG) {
                val review = ReviewBottomSheet.newInstance()
                review.show(this.supportFragmentManager, review.tag)
            }


            setMainFragment(R.id.nav_home, getString(R.string.home), false)
        }


        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val target = intent?.getIntExtra("target", -1)
        if (target != null && target != -1) {
            navigate(target)
        }
    }

    override fun onResume() {
        super.onResume()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val value = TypedValue()
            theme.resolveAttribute(R.attr.dark_mode, value, true)
            if (value.string == "dark") {
                window.decorView.systemUiVisibility = 0
                window.statusBarColor = getThemeAccentColor(this)
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = getThemeAccentColor(this)
            }
        }
    }

    private fun getThemeAccentColor(context: Context, theme: Resources.Theme = context.theme): Int {
        val outValue = TypedValue()
        theme.resolveAttribute(android.R.attr.colorBackground, outValue, true)
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


    private fun initNavDrawer() {
        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()

        val style = when (storage.theme) {
            Dark -> R.style.AppTheme_Dark
            Light -> R.style.AppTheme
            SafeMode -> R.style.AppTheme_SafeMode
            Developer -> R.style.AppTheme_Developer
            null -> R.style.AppTheme_Dark
        }
        theme.applyStyle(style, true)

        return theme
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onBackPressed() {
        val drawerOpen = binding.drawerLayout.isDrawerOpen(GravityCompat.START)

        when {
            drawerOpen -> binding.drawerLayout.closeDrawers()
            storage.navDrawerOnBack && !drawerOpen && !secondaryVisible -> binding.drawerLayout.openDrawer(
                GravityCompat.START
            )
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
        replaceFragment(getFragment(id), R.id.container, backStack = addToBackStack)

        title?.let {
            supportActionBar?.title = it
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setMainFragment(item.itemId, item.title.toString(), false)
        binding.drawerLayout.closeDrawers()
        return true
    }

    private fun getFragment(id: Int): Fragment {
        // TODO: Remove, this is a hacky solution for caching issue with InformationFragment's children fragments.
        if (id == R.id.nav_information)
            return InformationFragment.newInstance()

        if (id == R.id.nav_map)
            return MapsFragment.newInstance()

        if (map[id] == null) {
            map[id] = when (id) {
                R.id.nav_home -> HomeFragment.newInstance()
                R.id.nav_schedule -> ScheduleFragment.newInstance()
                R.id.nav_map -> MapsFragment.newInstance()
                R.id.nav_settings -> SettingsFragment.newInstance()
                R.id.search -> SearchFragment.newInstance()
                else -> InformationFragment.newInstance()
            }
        }
        return map[id]!!
    }

    fun navigate(event: Event) {
        navigate(event.id)
    }

    fun navigate(id: Int) {
        replaceFragment(EventFragment.newInstance(id), R.id.container_above, hasAnimation = true)
    }

    fun navigate(speaker: Speaker?) {
        speaker ?: return
        replaceFragment(
            SpeakerFragment.newInstance(speaker),
            R.id.container_above,
            hasAnimation = true
        )
    }

    fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    override fun onBackStackChanged() {
        val fragments = supportFragmentManager.fragments
        val last = fragments.lastOrNull()

        if (last is EventFragment || last is SpeakerFragment) {
            secondaryVisible = true
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            binding.main.container.visibility = View.INVISIBLE
        } else {
            secondaryVisible = false
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            binding.main.container.visibility = View.VISIBLE
        }
    }

    fun openNavDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun showSearch() {
        setMainFragment(R.id.search, getString(R.string.search), true)
    }

    fun showMap(location: Location) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val fragment = MapsFragment.newInstance(location)
        map[R.id.nav_map] = fragment

        setMainFragment(R.id.nav_map, getString(R.string.map), true)
    }

    fun navigate(type: Type) {
        replaceFragment(
            CategoryFragment.newInstance((type)),
            R.id.container_above,
            hasAnimation = true
        )
    }
}
