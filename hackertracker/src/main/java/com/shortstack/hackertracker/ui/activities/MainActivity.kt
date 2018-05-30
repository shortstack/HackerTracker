package com.shortstack.hackertracker.ui.activities


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.*
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.github.stkent.amplify.tracking.Amplify
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.getStatusBarHeight
import com.shortstack.hackertracker.ui.ReviewBottomSheet
import com.shortstack.hackertracker.ui.schedule.ScheduleItemBottomSheet
import com.shortstack.hackertracker.utils.SharedPreferencesUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.android.synthetic.main.row_nav_view.*
import javax.inject.Inject


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    @Inject
    lateinit var storage: SharedPreferencesUtil

    @Inject
    lateinit var database: DatabaseManager

    @Inject
    lateinit var analytics: AnalyticsController

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        App.application.myComponent.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setupNavigation()
        setNavHeaderMargin()

        filter.setOnClickListener { onFilterClick() }
//        close.setOnClickListener { onFilterClick() }

        if (savedInstanceState == null) {
            if (Amplify.getSharedInstance().shouldPrompt() && !BuildConfig.DEBUG) {
                val review = ReviewBottomSheet.newInstance()
                review.show(this.supportFragmentManager, review.tag)
            }
        }

        // TODO: Remove, this is only for debugging.
        Logger.d("Created MainActivity " + (System.currentTimeMillis() - App.application.timeToLaunch))

    }

    private fun setupNavigation() {
        navController = findNavController(R.id.mainNavigationFragment)
        setupActionBarWithNavController(navController, drawerLayout = drawer_layout)

        navController.addOnNavigatedListener { controller, destination ->
            val id = destination.id
            val nav_schedule = R.id.nav_schedule
            val visibility = if (id == nav_schedule) View.VISIBLE else View.INVISIBLE
            setFABVisibility(visibility)
        }

        initViewPager()
    }

    private fun initViewPager() {
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.mainNavigationFragment).navigateUp()

    private fun setNavHeaderMargin() {
        val params = nav_view.getHeaderView(0).imageView.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = getStatusBarHeight()
    }


    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(storage.databaseTheme, true)
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

        database.findItem(id = target)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ item ->
                    val fragment = ScheduleItemBottomSheet.newInstance(item)
                    fragment.show(supportFragmentManager, fragment.tag)
                }, {})

    }

    private fun onFilterClick() {
        toggleFAB(onClick = true)
    }

    private fun toggleFilters() {


//        val cx = filters.width / 2
//        val cy = filters.height / 2
//
//        val position = IntArray(2)
//
//        filter.getLocationOnScreen(position)
//
//        val (cx, cy) = position
//
//        val radius = Math.hypot(cx.toDouble(), cy.toDouble())
//
//        if (filters.visibility == View.INVISIBLE) {
//
//
//            val anim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                ViewAnimationUtils.createCircularReveal(filters, cx, cy, 0f, radius.toFloat())
//            } else {
//                null
//            }
//
//            filters.visibility = View.VISIBLE
//
//            anim?.start()
//        } else {
//
//            val anim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                ViewAnimationUtils.createCircularReveal(filters, cx, cy, radius.toFloat(), 0f)
//            } else {
//
//                filters.visibility = View.INVISIBLE
//                null
//            }
//
//            anim?.addListener(object : AnimatorListenerAdapter() {
//                override fun onAnimationEnd(animation: Animator?) {
//                    super.onAnimationEnd(animation)
//                    filters.visibility = View.INVISIBLE
//                    toggleFAB(onClick = false)
//                }
//            })
//
//            anim?.start()
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        return true
    }

    private fun setFABVisibility(visibility: Int, showFilters: Boolean = false) {
        if (!filter.isAttachedToWindow) {
            return
        }

        val cx = filter.width / 2
        val cy = filter.height / 2


        val radius = Math.hypot(cx.toDouble(), cy.toDouble())

        if (visibility == View.VISIBLE) {

            filter.visibility = View.VISIBLE

            val anim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewAnimationUtils.createCircularReveal(filter, cx, cy, 0f, radius.toFloat())
            } else {
                null
            }


            anim?.start()
        } else {

            val anim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewAnimationUtils.createCircularReveal(filter, cx, cy, radius.toFloat(), 0f)
            } else {

                filter.visibility = View.INVISIBLE
                null
            }

            anim?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    filter.visibility = View.INVISIBLE
                    if (showFilters) toggleFilters()
                }
            })

            anim?.start()
        }
    }

    private fun toggleFAB(onClick: Boolean = false) {


        val cx = filter.width / 2
        val cy = filter.height / 2


        val radius = Math.hypot(cx.toDouble(), cy.toDouble())

        if (filter.visibility == View.INVISIBLE) {


            val anim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewAnimationUtils.createCircularReveal(filter, cx, cy, 0f, radius.toFloat())
            } else {
                null
            }

            filter.visibility = View.VISIBLE

            anim?.start()
        } else {

            val anim = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewAnimationUtils.createCircularReveal(filter, cx, cy, radius.toFloat(), 0f)
            } else {

                filter.visibility = View.INVISIBLE
                null
            }

            anim?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    filter.visibility = View.INVISIBLE
                    if (onClick) toggleFilters()

                }
            })

            anim?.start()
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
        val current = navController.currentDestination.id
        if (item.itemId != current) {
            navController.navigate(item.itemId)
        }

        drawer_layout.closeDrawers()
        return true
    }
}
