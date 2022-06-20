package com.advice.schedule.ui.activities


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.stkent.amplify.tracking.Amplify
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.orhanobut.logger.Logger
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.Location
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.models.local.Type
import com.advice.schedule.replaceFragment
import com.advice.schedule.ui.PanelsFragment
import com.advice.schedule.ui.events.EventFragment
import com.advice.schedule.ui.information.InformationFragment
import com.advice.schedule.ui.information.categories.CategoryFragment
import com.advice.schedule.ui.information.speakers.SpeakerFragment
import com.advice.schedule.ui.maps.MapsFragment
import com.advice.schedule.ui.schedule.ScheduleFragment
import com.advice.schedule.ui.search.SearchFragment
import com.advice.schedule.ui.settings.SettingsFragment
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ActivityMainBinding
import org.koin.core.KoinComponent


class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener,
    FragmentManager.OnBackStackChangedListener, KoinComponent {

    private lateinit var binding: ActivityMainBinding

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val map = HashMap<Int, Fragment>()

    private var secondaryVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null) {
            if (Amplify.getSharedInstance().shouldPrompt() && !BuildConfig.DEBUG) {
                val review = ReviewBottomSheet.newInstance()
                review.show(this.supportFragmentManager, review.tag)
            }

            setMainFragment(R.id.nav_home, getString(R.string.home), false)

            showMap()
        }

        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val target = intent?.getLongExtra("target", -1L)
        if (target != null && target != -1L) {
            navigate(target)
        }
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
                R.id.nav_home -> return PanelsFragment()//HomeFragment.newInstance()
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

    fun navigate(id: Long) {
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
            CategoryFragment.newInstance(type),
            R.id.container_above,
            hasAnimation = true
        )
    }

    fun showInformation() {
        setMainFragment(R.id.nav_information, "", true)
    }

    fun showMap() {
        setMainFragment(R.id.nav_map, "", true)
    }

    fun showSettings() {
        setMainFragment(R.id.nav_settings, "", true)
    }

    fun showSchedule(location: Location) {
        replaceFragment(
            ScheduleFragment.newInstance(location),
            R.id.container_above,
            hasAnimation = true
        )
    }

    fun showSchedule(type: Type) {
        replaceFragment(
            ScheduleFragment.newInstance(type),
            R.id.container_above,
            hasAnimation = true
        )
    }

    fun showSchedule(speaker: Speaker) {
        replaceFragment(
            ScheduleFragment.newInstance(speaker),
            R.id.container_above,
            hasAnimation = true
        )
    }
}
