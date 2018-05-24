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
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import com.shortstack.hackertracker.view.ItemView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private var adapter: RendererAdapter<Event>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        search(newText)
        return false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layout = LinearLayoutManager(context)
        list.layoutManager = layout
        loading_progress.visibility = View.GONE

        val rendererBuilder = RendererBuilder<Any>()
                .bind(Event::class.java, EventRenderer(ItemView.DISPLAY_MODE_FULL))


        adapter = RendererAdapter(rendererBuilder)
        list.adapter = adapter
    }

    fun search(text: String) {
        Logger.d("Searching $text")

        if (adapter == null)
            return

        if (text.isEmpty()) {
            adapter?.clearAndNotify()
//            empty_view.visibility = View.VISIBLE
//            empty_view.showDefault()
            return
        }

        App.application.database.db.eventDao().findByText("%$text%")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe({
                    adapter?.clearAndNotify()
                    adapter?.addAllAndNotify(it)


                    if (it.isEmpty()) {
//                        empty_view.visibility = View.VISIBLE
//                        empty_view.showNoResults(text)
                    } else {
//                        empty_view.visibility = View.GONE
                    }

                    val timeMillis = System.currentTimeMillis()
                    Logger.d("Time to search: ${System.currentTimeMillis() - timeMillis}ms")
                }, {})
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}