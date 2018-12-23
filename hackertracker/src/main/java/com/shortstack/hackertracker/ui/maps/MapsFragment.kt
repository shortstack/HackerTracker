package com.shortstack.hackertracker.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.answers.CustomEvent
import com.google.android.material.tabs.TabLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.ConferenceMap
import kotlinx.android.synthetic.main.fragment_maps.*

class MapsFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_layout.apply {
            tabGravity = com.google.android.material.tabs.TabLayout.GRAVITY_FILL
            setupWithViewPager(pager)
        }


        val mapsViewModel = ViewModelProviders.of(this).get(MapsViewModel::class.java)
        mapsViewModel.maps.observe(this, Observer {
            val adapter = PagerAdapter(activity!!.supportFragmentManager, it)
            pager.adapter = adapter
        })

        AnalyticsController.logCustom(CustomEvent(AnalyticsController.MAP_VIEW))
    }

    class PagerAdapter(fm: androidx.fragment.app.FragmentManager, private val maps: List<ConferenceMap>) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int) = MapFragment.newInstance(maps[position].map_url)

        override fun getPageTitle(position: Int) = maps[position].map_title

        override fun getCount() = maps.size
    }

    companion object {


        fun newInstance() = MapsFragment()

    }
}
