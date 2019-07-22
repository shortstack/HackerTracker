package com.shortstack.hackertracker.ui.information.vendors

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.models.firebase.FirebaseVendor
import com.shortstack.hackertracker.ui.ListFragment


class VendorsFragment : ListFragment<FirebaseVendor>() {

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



