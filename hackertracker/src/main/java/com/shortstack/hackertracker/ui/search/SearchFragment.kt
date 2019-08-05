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
import android.app.Activity
import android.view.inputmethod.InputMethodManager


class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

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
            hideKeyboard(context as MainActivity)
            (context as MainActivity).popBackStack()
        }

        search.onActionViewExpanded()
        search.setOnCloseListener {
            hideKeyboard(context as MainActivity)
            (context as MainActivity).popBackStack()
            return@setOnCloseListener true
        }
        
        search.setOnQueryTextListener(this)


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

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.query = newText
        viewModel.search(newText)
        return false
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus ?: View(activity)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}