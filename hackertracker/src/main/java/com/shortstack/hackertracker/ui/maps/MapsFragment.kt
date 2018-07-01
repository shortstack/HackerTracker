package com.shortstack.hackertracker.ui.maps

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import android.view.*
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.models.ConferenceMap
import kotlinx.android.synthetic.main.fragment_maps.*
import javax.inject.Inject

class MapsFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapsViewModel = ViewModelProviders.of(this).get(MapsViewModel::class.java)
        mapsViewModel.maps.observe(this, Observer {
            if (it != null) {
                val adapter = PagerAdapter(activity!!.supportFragmentManager, it)
                pager.adapter = adapter

                if (it.size > 1) {
                    tab_layout.apply {
                        tabGravity = com.google.android.material.tabs.TabLayout.GRAVITY_FILL
                        setupWithViewPager(pager)
                    }
                } else {
                    tab_layout.visibility = View.GONE
                }
            }
        })
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
