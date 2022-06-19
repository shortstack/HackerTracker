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
import com.shortstack.hackertracker.databinding.FragmentMapsBinding
import com.shortstack.hackertracker.models.firebase.FirebaseConferenceMap
import com.shortstack.hackertracker.models.local.Location
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utilities.Analytics
import org.koin.android.ext.android.inject

class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private val analytics: Analytics by inject()
    private var isFirstLoad: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabLayout.apply {
            tabGravity = com.google.android.material.tabs.TabLayout.GRAVITY_FILL
            setupWithViewPager(binding.pager)
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }


        val mapsViewModel =
            ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]
        mapsViewModel.maps.observe(viewLifecycleOwner, Observer {
            val maps = it.data ?: emptyList()

            when (maps.size) {
                0 -> {
                    binding.tabLayout.visibility = View.GONE
                    binding.emptyView.visibility = View.VISIBLE
                }
                1 -> {
                    binding.tabLayout.visibility = View.GONE
                    binding.emptyView.visibility = View.GONE
                }
                else -> {
                    binding.tabLayout.visibility = View.VISIBLE
                    binding.emptyView.visibility = View.GONE
                }
            }

            val adapter = PagerAdapter(requireActivity().supportFragmentManager, maps)
            binding.pager.adapter = adapter

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
                binding.pager.currentItem = position
        }
    }

    class PagerAdapter(fm: FragmentManager, private val maps: List<FirebaseConferenceMap>) :
        FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int) = MapFragment.newInstance(maps[position].file)

        override fun getPageTitle(position: Int) = maps[position].title

        override fun getCount() = maps.size
    }

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
}
