package com.shortstack.hackertracker.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orhanobut.logger.Logger
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import com.shortstack.hackertracker.ui.search.SearchViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import javax.inject.Inject

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private var adapter: RendererAdapter<Event>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading_progress.visibility = View.GONE

        adapter = RendererAdapter(RendererBuilder<Any>()
                .bind(Event::class.java, EventRenderer()))
        list.adapter = adapter
    }

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String): Boolean {
        search(newText)
        return false
    }


    fun search(text: String) {
        val adapter = adapter ?: return

        Logger.d("Searching $text")

        if (text.isEmpty()) {
            adapter.clearAndNotify()
//            empty_view.visibility = View.VISIBLE
//            empty_view.showDefault()
            return
        }

        val searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        searchViewModel.getResult(text).observe(this, Observer {
            adapter.clearAndNotify()
            adapter.addAllAndNotify(it)

            when {
                it?.isNotEmpty() == true -> {
                    adapter.clearAndNotify()
                    adapter.addAllAndNotify(it)
                }
                it?.isEmpty() == true -> {
                    //                        empty_view.visibility = View.VISIBLE
                    //                        empty_view.showNoResults(text)
                    adapter.clearAndNotify()
                }
                else -> {
                    //                        empty_view.visibility = View.GONE
                    adapter.clearAndNotify()
                }
            }
        })
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}