package com.shortstack.hackertracker.ui.vendors

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.models.Vendor
import com.shortstack.hackertracker.ui.ListFragment


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



