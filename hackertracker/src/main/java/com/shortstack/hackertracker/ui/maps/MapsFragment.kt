package com.shortstack.hackertracker.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.utilities.Analytics
import com.shortstack.hackertracker.models.firebase.FirebaseConferenceMap
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_maps.*
import org.koin.android.ext.android.inject

class MapsFragment : Fragment() {

    companion object {
        fun newInstance() = MapsFragment()
    }

    private val analytics: Analytics by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_layout.apply {
            tabGravity = com.google.android.material.tabs.TabLayout.GRAVITY_FILL
            setupWithViewPager(pager)
        }

        toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }


        val mapsViewModel = ViewModelProviders.of(this).get(MapsViewModel::class.java)
        mapsViewModel.maps.observe(this, Observer {
            when (it.size) {
                0 -> {
                    tab_layout.visibility = View.GONE
                    empty_view.visibility = View.VISIBLE
                }
                1 -> {
                    tab_layout.visibility = View.GONE
                    empty_view.visibility = View.GONE
                }
                else -> {
                    tab_layout.visibility = View.VISIBLE
                    empty_view.visibility = View.GONE
                }
            }

            val adapter = PagerAdapter(activity!!.supportFragmentManager, it)
            pager.adapter = adapter
        })

        analytics.logCustom(CustomEvent(Analytics.MAP_VIEW))
    }

    class PagerAdapter(fm: FragmentManager, private val maps: List<FirebaseConferenceMap>) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int) = MapFragment.newInstance(maps[position].file)

        override fun getPageTitle(position: Int) = maps[position].title

        override fun getCount() = maps.size
    }


}
