package com.shortstack.hackertracker.ui.maps

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.*
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import kotlinx.android.synthetic.main.fragment_maps.*
import javax.inject.Inject

class MapsFragment : Fragment() {

    @Inject
    lateinit var analytics: AnalyticsController

    @Inject
    lateinit var database: DatabaseManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.application.myComponent.inject(this)

        val adapter = PagerAdapter(activity!!.supportFragmentManager)
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

    override fun onDestroyView() {
        (pager.adapter as PagerAdapter).destroy()
        super.onDestroyView()
    }

    class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        var maps: Array<Fragment>

        @Inject
        lateinit var database: DatabaseManager

        init {
            App.application.myComponent.inject(this)

            val name = database.databaseName

            maps = when (name) {
                Constants.SHMOOCON_DATABASE_NAME -> arrayOf(MapFragment.newInstance(SHMOOCON))
                Constants.TOORCON_DATABASE_NAME -> arrayOf(MapFragment.newInstance(TOORCON))
                Constants.HACKWEST_DATABASE_NAME -> arrayOf(MapFragment.newInstance(HACKWEST))
                Constants.LAYERONE_DATABASE_NAME -> arrayOf(MapFragment.newInstance(LAYERONE))
                Constants.BSIDESORL_DATABASE_NAME -> arrayOf(MapFragment.newInstance(BSIDESORL))
                else -> arrayOf(MapFragment.newInstance(MAP_DAY),
                        MapFragment.newInstance(MAP_NIGHT))
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

        private const val MAP_DAY = "dc-25-floorplan-v8-final-public.pdf"
        private const val MAP_NIGHT = "dc-25-floorplan-v7.6-night.pdf"
        private const val TOORCON = "toorcon-19-map.pdf"
        private const val SHMOOCON = "shmoocon-2018-map.pdf"
        private const val HACKWEST = "hackwest-map-small.pdf"
        private const val LAYERONE = "layerone_map.pdf"
        private const val BSIDESORL = "bsidesorl_map.pdf"

        fun newInstance(): Fragment {
            return MapsFragment()
        }
    }
}
