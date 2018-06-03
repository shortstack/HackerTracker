package com.shortstack.hackertracker.ui.vendors

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Vendor
import kotlinx.android.synthetic.main.fragment_recyclerview.*


class VendorsFragment : Fragment() {

    private var adapter: RendererAdapter<Vendor>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgressIndicator(true)

        initAdapter()

        val vendorsViewModel = ViewModelProviders.of(this).get(VendorsViewModel::class.java)
        vendorsViewModel.vendors.observe(this, Observer {
            setProgressIndicator(false)

            if (it != null) {
                showVendors(it)
            } else {
                showLoadingVendorsError()
            }
        })
    }

    private fun initAdapter() {
        adapter = RendererAdapter(RendererBuilder<Vendor>()
                .bind(Vendor::class.java, VendorRenderer()))

        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter
    }

    private fun setProgressIndicator(active: Boolean) {
        loading_progress.visibility = if (active) View.VISIBLE else View.GONE
    }

    private fun showVendors(vendors: List<Vendor>) {
        adapter?.clearAndNotify()
        adapter?.addAllAndNotify(vendors)
    }

    private fun showLoadingVendorsError() {
        Toast.makeText(context, "Could not load vendors.", Toast.LENGTH_SHORT).show()
    }

    companion object {

        fun newInstance() = VendorsFragment()

    }
}



