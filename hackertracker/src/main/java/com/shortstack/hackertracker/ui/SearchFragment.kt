package com.shortstack.hackertracker.ui

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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import javax.inject.Inject

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    private var adapter: RendererAdapter<Event>? = null

    @Inject
    lateinit var database: DatabaseManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.application.myComponent.inject(this)

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

        database.findItem("%$text%")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .debounce(300, TimeUnit.MILLISECONDS)
                .subscribe({
                    adapter.clearAndNotify()
                    adapter.addAllAndNotify(it)


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