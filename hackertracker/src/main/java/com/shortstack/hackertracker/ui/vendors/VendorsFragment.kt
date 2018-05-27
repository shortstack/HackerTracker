package com.shortstack.hackertracker.ui.vendors

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Event.ChangeConEvent
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Vendor
import com.squareup.otto.Subscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*


class VendorsFragment : Fragment() {

    private var adapter: RendererAdapter<Vendor>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rendererBuilder = RendererBuilder<Vendor>()
                .bind(Vendor::class.java, VendorRenderer())


        App.application.registerBusListener(this)
        adapter = RendererAdapter(rendererBuilder)

        list.layoutManager = LinearLayoutManager(context)
        list.adapter = adapter

        getVendors()
    }

    override fun onDestroyView() {
        App.application.unregisterBusListener(this)
        super.onDestroyView()
    }

    @Subscribe
    fun onChangeConEvent(event: ChangeConEvent) {
        getVendors()
    }

    private fun getVendors() {
        setProgressIndicator(true)

        App.application.database.getVendors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setProgressIndicator(false)
                    if (it.isEmpty()) {
//                        empty_view.visibility = View.VISIBLE
                    } else {
//                        empty_view.visibility = View.GONE
                        showVendors(it)
                    }
                }, {
                    setProgressIndicator(false)
                    showLoadingVendorsError()
                })
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

    private fun isActive(): Boolean {
        return isAdded
    }


    companion object {
        fun newInstance(): VendorsFragment {
            return VendorsFragment()
        }
    }
}



