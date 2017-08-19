package com.shortstack.hackertracker.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.Vendors
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Renderer.ItemRenderer
import com.shortstack.hackertracker.Renderer.VendorRenderer
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        search(newText)
        return false
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout = LinearLayoutManager(context)
        list.layoutManager = layout


        val rendererBuilder = RendererBuilder<Any>()
                .bind(Item::class.java, ItemRenderer())
                .bind(Vendors.Vendor::class.java, VendorRenderer())

        val adapter = RendererAdapter<Any>(rendererBuilder)
        list.adapter = adapter
    }

    fun search( text : String ) {
        getAdapter().collection.clear()

        if( text.isEmpty() ) {
            getAdapter().notifyDataSetChanged()
            return
        }


        val controller = App.application.databaseController



        for (item in controller.getItemByDate()) {
            if( item.title.contains(text, true)) {
                getAdapter().add(item)
            }
        }


        for (item in controller.vendors) {
            if( item.title.contains(text, true)) {
                getAdapter().add(item)
            }
        }

        getAdapter().notifyDataSetChanged()
    }

    private fun getAdapter() = list.adapter as RendererAdapter<Any>

    companion object {
        fun newInstance():SearchFragment {
            return SearchFragment()
        }
    }
}