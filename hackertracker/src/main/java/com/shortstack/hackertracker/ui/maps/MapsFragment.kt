package com.shortstack.hackertracker.ui.maps

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.*
import com.shortstack.hackertracker.utils.MaterialAlert
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.view.UberView
import kotlinx.android.synthetic.main.fragment_maps.*
import javax.inject.Inject

class MapsFragment : Fragment() {


    @Inject
    lateinit var analytics: AnalyticsController

    @Inject
    lateinit var database: DEFCONDatabaseController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.application.myComponent.inject(this)

        val adapter = PagerAdapter(activity.supportFragmentManager)
        pager.adapter = adapter

        if (database.databaseName == Constants.DEFCON_DATABASE_NAME) {
            tab_layout.apply {
                tabGravity = TabLayout.GRAVITY_FILL
                setupWithViewPager(pager)
            }
        } else {
            tab_layout.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_uber -> {
                analytics.tagCustomEvent(AnalyticsController.Analytics.UBER)
                MaterialAlert.create(context).setTitle(com.shortstack.hackertracker.R.string.uber).setView(UberView(context)).show()
                return true
            }
        }

        return false
    }

    override fun onDestroyView() {
        (pager.adapter as PagerAdapter).destroy()
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.maps, menu)
        if (database.databaseName != Constants.DEFCON_DATABASE_NAME) {
            menu?.removeItem(R.id.action_uber)
        }
    }

    class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        var maps: Array<Fragment>

        @Inject
        lateinit var database: DEFCONDatabaseController

        init {
            App.application.myComponent.inject(this)

            maps = when {
                database.databaseName == Constants.DEFCON_DATABASE_NAME ->
                    arrayOf(MapFragment.newInstance(MAP_DAY),
                            MapFragment.newInstance(MAP_NIGHT))
                database.databaseName == Constants.TOORCON_DATABASE_NAME ->
                    arrayOf(MapFragment.newInstance(TOORCON))
                else -> arrayOf(MapFragment.newInstance(SHMOOCON))
            }
        }

        override fun getItem(position: Int): Fragment {
            return maps[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return if (position == 0) {
                "DAY"
            } else {
                "NIGHT"
            }
        }

        override fun getCount(): Int {
            return maps.size
        }

        fun destroy() {
            maps.iterator().forEach { it.onDestroyView() }
        }

    }

    companion object {

        const val MAP_DAY = "dc-25-floorplan-v8-final-public.pdf"
        const val MAP_NIGHT = "dc-25-floorplan-v7.6-night.pdf"
        const val TOORCON = "toorcon-19-map.pdf"
        const val SHMOOCON = "shmoocon-2018-map.pdf"

        fun newInstance(): Fragment {
            return MapsFragment()
        }
    }
}
