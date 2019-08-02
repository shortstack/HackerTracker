package com.shortstack.hackertracker.ui.search

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.ui.search.SearchAdapter.State.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(), SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private val adapter = SearchAdapter()
    private val viewModel by lazy { ViewModelProviders.of(this).get(SearchViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loading_progress.visibility = View.GONE
        empty_view.showDefault()

        list.adapter = adapter

        toolbar.setNavigationOnClickListener {
            (context as MainActivity).openNavDrawer()
        }

        viewModel.results.observe(this, Observer {
            adapter.setList(it)

            when (adapter.state) {
                INIT -> empty_view.showDefault()
                RESULTS -> {
                    empty_view.hide()
                    list.layoutManager?.smoothScrollToPosition(list, RecyclerView.State(), 0)
                }
                EMPTY -> empty_view.showNoResults(adapter.query)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.findItem(R.id.search)?.apply {
            expandActionView()
            setOnActionExpandListener(this@SearchFragment)

            (actionView as SearchView).apply {
                setOnQueryTextListener(this@SearchFragment)
            }
        }

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.query = newText
        viewModel.search(newText)
        return false
    }

    override fun onMenuItemActionExpand(item: MenuItem?) = false

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        val activity = context as? MainActivity
        activity?.popBackStack()
        return true
    }
}