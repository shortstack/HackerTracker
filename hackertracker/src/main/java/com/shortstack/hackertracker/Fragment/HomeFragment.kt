package com.shortstack.hackertracker.Fragment

import android.annotation.SuppressLint
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
import com.shortstack.hackertracker.Renderer.ActivityNavRenderer
import com.shortstack.hackertracker.Renderer.HomeHeaderRenderer
import com.shortstack.hackertracker.Renderer.ItemRenderer
import com.shortstack.hackertracker.Renderer.SubHeaderRenderer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_list.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    lateinit var adapter: RendererAdapter<Any>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rendererBuilder = RendererBuilder<Any>()
                .bind(TYPE_HEADER, HomeHeaderRenderer())
                .bind(String::class.java, SubHeaderRenderer())
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

    @SuppressLint("SimpleDateFormat")
    private fun addUpdatedCards() {

        val app = App.application
        app.databaseController.getRecent().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    // Updates title

                    val size = adapter.collection.size

                    var lastDate :String

                    val cal = Calendar.getInstance()
                    cal.time = Date(app.storage.lastRefresh)

                    val refresh = app.storage.lastRefresh
                    if (refresh == 0L ) {
                        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(app.storage.lastUpdated)
                        lastDate = "Last synced " + App.getRelativeDateStamp(date)
                    } else {
                        lastDate = "Last synced " + App.getRelativeDateStamp(Date(refresh))
                    }

                    adapter.add(getString(R.string.updates) + "\n" + lastDate.toLowerCase())

                    var recentDate = ""

                    for (item in it) {
                        if (item.updatedAt != recentDate) {
                            recentDate = item.updatedAt
                            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item.updatedAt)
                            adapter.add("Updated " + SimpleDateFormat("MMMM dd h:mm aa").format(date))
                        }

                        adapter.add(item)
                    }

                    adapter.notifyItemRangeInserted(size, adapter.collection.size - size )
                }



    }

    private fun addHelpNavigation() {
        adapter.addAndNotify(Navigation(getString(R.string.nav_help_title), getString(R.string.nav_help_body), InformationFragment::class.java))
    }

    private fun addHeader() {
        adapter.addAndNotify(RendererContent<Void>(null, TYPE_HEADER))
    }

    companion object {

        private val TYPE_HEADER = 0

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

}
