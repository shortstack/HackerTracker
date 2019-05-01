package com.shortstack.hackertracker.ui.vendors

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.models.Vendor
import com.shortstack.hackertracker.ui.ListFragment


class VendorsFragment : ListFragment<Vendor>() {

    companion object {
        fun newInstance() = VendorsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel<VendorsViewModel>().vendors.observe(this, Observer {
            onResource(it)
        })
    }
}



