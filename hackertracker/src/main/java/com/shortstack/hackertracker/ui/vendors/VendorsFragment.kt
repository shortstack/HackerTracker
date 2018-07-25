package com.shortstack.hackertracker.ui.vendors

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Status
import com.shortstack.hackertracker.models.Vendor
import com.shortstack.hackertracker.ui.ListFragment
import kotlinx.android.synthetic.main.fragment_recyclerview.*


class VendorsFragment : ListFragment<Vendor>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel<VendorsViewModel>().vendors.observe(this, Observer {
            onResource(it)
        })
    }

    override fun initAdapter(): RendererAdapter<Any> {
        return RendererBuilder.create<Any>()
                .bind(Vendor::class.java, VendorRenderer())
                .build()
    }

    companion object {
        fun newInstance() = VendorsFragment()
    }
}



