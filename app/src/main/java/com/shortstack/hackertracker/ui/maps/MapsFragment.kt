package com.shortstack.hackertracker.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.firebase.FirebaseConferenceMap
import com.shortstack.hackertracker.models.local.Location
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.Analytics
import kotlinx.android.synthetic.main.fragment_maps.*
import org.koin.android.ext.android.inject

class MapsFragment : Fragment() {

    companion object {

        private const val EXTRA_LOCATION = "location"

        fun newInstance(location: Location? = null): MapsFragment {
            val fragment = MapsFragment()

            if (location != null) {
                val bundle = Bundle()

                bundle.putParcelable(EXTRA_LOCATION, location)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    private val analytics: Analytics by inject()
    private var isFirstLoad: Boolean = true

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


        val mapsViewModel = ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]
        mapsViewModel.maps.observe(this, Observer {
            val maps = it.data ?: emptyList()

            when (maps.size) {
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

            val adapter = PagerAdapter(activity!!.supportFragmentManager, maps)
            pager.adapter = adapter

            if (isFirstLoad) {
                isFirstLoad = false

                showSelectedMap(maps)
            }

        })

        analytics.logCustom(Analytics.CustomEvent(Analytics.MAP_VIEW))
    }

    private fun showSelectedMap(it: List<FirebaseConferenceMap>) {
        val location = arguments?.getParcelable<Location>(EXTRA_LOCATION)
        if (location != null) {
            val position = it.indexOfFirst { it.title == location.hotel }
            if (position != -1)
                pager.currentItem = position
        }
    }

    class PagerAdapter(fm: FragmentManager, private val maps: List<FirebaseConferenceMap>) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int) = MapFragment.newInstance(maps[position].file)

        override fun getPageTitle(position: Int) = maps[position].title

        override fun getCount() = maps.size
    }


}
