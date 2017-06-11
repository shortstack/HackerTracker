package com.shortstack.hackertracker.Activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Model.Company
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Renderer.VendorRenderer
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class VendorsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout = LinearLayoutManager(context)
        list!!.layoutManager = layout


        val rendererBuilder = RendererBuilder<Any>()
                .bind(Company::class.java, VendorRenderer())

        val adapter = RendererAdapter<Any>(rendererBuilder)
        list!!.adapter = adapter

        adapter.addAll(App.getApplication().databaseController.vendors)
    }

    companion object {
        fun newInstance(): VendorsFragment {
            return VendorsFragment()
        }
    }
}



