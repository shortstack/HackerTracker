package com.shortstack.hackertracker.ui.information.villages

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.Resource
import com.shortstack.hackertracker.models.firebase.FirebaseVendor
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.information.vendors.VendorsFragment


class VillagesFragment : ListFragment<FirebaseVendor>() {

    companion object {
        fun newInstance() = VillagesFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]

        viewModel.types.observe(viewLifecycleOwner, Observer {
            val resource = Resource(it.status, it.data?.sortedBy { it.name.toLowerCase() }, it.message)
            onResource(resource)
        })
    }
}



