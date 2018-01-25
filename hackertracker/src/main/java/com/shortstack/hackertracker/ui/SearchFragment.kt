package com.shortstack.hackertracker.ui

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
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.Item
import com.shortstack.hackertracker.ui.schedule.renderers.ItemRenderer
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    var adapter: RendererAdapter<Item>? = null

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
        loading_progress.visibility = View.GONE;

        val rendererBuilder = RendererBuilder<Any>()
                .bind(Item::class.java, ItemRenderer())


        adapter = RendererAdapter<Item>(rendererBuilder)
        list.adapter = adapter
    }

    fun search(text: String) {
        Logger.d("Searching $text")

        if (adapter == null)
            return

        adapter!!.collection.clear()

        if (text.isEmpty()) {
            adapter!!.notifyDataSetChanged()
            return
        }

        val timeMillis = System.currentTimeMillis()
        adapter!!.addAll(App.application.databaseController.findByText(text))
        Logger.d("Time to search: ${System.currentTimeMillis() - timeMillis}ms" )
        adapter!!.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}