package com.shortstack.hackertracker.vendors

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Model.Vendor
import com.shortstack.hackertracker.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*


class VendorsFragment : Fragment() {

    override fun onCreateView(inflater : LayoutInflater?, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        return inflater!!.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view : View?, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout = LinearLayoutManager(context)
        list.layoutManager = layout

        val rendererBuilder = RendererBuilder<Any>()
                .bind(Vendor::class.java, VendorRenderer())

        val adapter = RendererAdapter<Any>(rendererBuilder)
        list.adapter = adapter

        getVendors()
    }

    private fun getVendors() {
        setProgressIndicator(true)

        App.application.databaseController.getVendors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setProgressIndicator(false)
                    showVendors(it.toTypedArray())
                }, {
                    if (isActive()) {
                        setProgressIndicator(false)
                        showLoadingVendorsError()
                    }

                })
    }


    private fun setProgressIndicator(active : Boolean) {
        loading_progress.visibility = if (active) View.VISIBLE else View.GONE
    }

    private fun showVendors(vendors : Array<Vendor>) {
        (list.adapter as RendererAdapter<*>).addAllAndNotify(vendors.toMutableList())
    }

    private fun showLoadingVendorsError() {
        Toast.makeText(context, "Could not load vendors.", Toast.LENGTH_SHORT).show()
    }

    private fun isActive() : Boolean {
        return isAdded
    }


    companion object {
        fun newInstance() : VendorsFragment {
            return VendorsFragment()
        }
    }
}



