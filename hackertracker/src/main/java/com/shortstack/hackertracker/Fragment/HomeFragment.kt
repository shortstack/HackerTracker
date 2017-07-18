package com.shortstack.hackertracker.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.pedrogomez.renderers.RendererContent
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.Navigation
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Renderer.*
import kotlinx.android.synthetic.main.fragment_list.*

class HomeFragment : Fragment() {

    var adapter: RendererAdapter<Any>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rendererBuilder = RendererBuilder<Any>()
                .bind(TYPE_HEADER, HomeHeaderRenderer())
                .bind(String::class.java, GenericHeaderRenderer())
                .bind(Array<String>::class.java, FAQRenderer())
                .bind(Item::class.java, ItemRenderer())
                .bind(Navigation::class.java, ActivityNavRenderer())

        val layout = LinearLayoutManager(context)
        list.layoutManager = layout

        adapter = RendererAdapter<kotlin.Any>(rendererBuilder)
        list.adapter = adapter

        addHeader()
        addHelpNavigation()
        addUpdatedCards()
    }

    private fun addUpdatedCards() {
        // Updates title
        adapter?.add(getString(R.string.updates))


        App.storage.recentUpdates.forEach {
            val id = Integer.valueOf(it)
            val scheduleItem = App.application.databaseController.getScheduleItemFromId(id)
            adapter?.add(scheduleItem)
        }
    }

    private fun addHelpNavigation() {
        adapter?.add(Navigation(getString(R.string.nav_help_title), getString(R.string.nav_help_body), InformationFragment::class.java))
    }

    private fun addHeader() {
        adapter?.add(RendererContent<Void>(null, TYPE_HEADER))
    }

    companion object {

        private val TYPE_HEADER = 0

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

}
