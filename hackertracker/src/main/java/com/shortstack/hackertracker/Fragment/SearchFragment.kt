package com.shortstack.hackertracker.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.Logger
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

    var adapter: RendererAdapter<Any>? = null

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

        adapter = RendererAdapter<Any>(rendererBuilder)
        list.adapter = adapter
    }

    fun search(text: String) {
        if (adapter == null)
            return

        adapter!!.collection.clear()

        if (text.isEmpty()) {
            adapter!!.notifyDataSetChanged()
            return
        }


        val controller = App.application.databaseController


        val time = System.currentTimeMillis()


        for (item in controller.findByText(text)) {
            adapter!!.add(item)
        }

        Logger.d("Search time: " + (System.currentTimeMillis() - time) + "ms")


        adapter!!.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}