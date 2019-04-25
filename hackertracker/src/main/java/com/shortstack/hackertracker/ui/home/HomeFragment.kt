package com.shortstack.hackertracker.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.pedrogomez.renderers.RendererContent
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.Navigation
import com.shortstack.hackertracker.ui.home.renderers.ActivityNavRenderer
import com.shortstack.hackertracker.ui.home.renderers.HomeHeaderRenderer
import com.shortstack.hackertracker.ui.home.renderers.SubHeaderRenderer
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import com.shortstack.hackertracker.views.EventView
import com.shortstack.hackertracker.views.WifiHelperRenderer
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class HomeFragment : Fragment() {

    private lateinit var adapter: RendererAdapter<Any>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgressIndicator(true)


        adapter = RendererBuilder.create<Any>()
                .bind(TYPE_HEADER, HomeHeaderRenderer())
                .bind(String::class.java, SubHeaderRenderer())
                .bind(FirebaseEvent::class.java, EventRenderer(EventView.DISPLAY_MODE_FULL))
                .bind(Navigation::class.java, ActivityNavRenderer())
                .bind(TYPE_WIFI, WifiHelperRenderer())
                .build()



        list.adapter = adapter


        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        homeViewModel.recent.observe(this, Observer {
            setProgressIndicator(false)
            if (it != null) {
                adapter.clearAndNotify()
                adapter.addAndNotify(getHeader())
//                adapter.addAndNotify(getWifiHelper())
                showRecentUpdates(it)
            }
        })
    }


    private fun setProgressIndicator(active: Boolean) {
        loading_progress?.visibility = if (active) View.VISIBLE else View.GONE
    }

    @SuppressLint("SimpleDateFormat")
    private fun showRecentUpdates(items: List<FirebaseEvent>) {
        val size = adapter.collection.size

        adapter.addAll(items)

        adapter.notifyItemRangeInserted(size, adapter.collection.size - size)
    }


    private fun showLoadingRecentUpdatesError() {
        Toast.makeText(context, "Could not fetch recent updates.", Toast.LENGTH_SHORT).show()
    }

    private fun getHeader() = RendererContent<Void>(null, TYPE_HEADER)

    private fun getWifiHelper() = RendererContent<Void>(null, TYPE_WIFI)

    companion object {

        const val TYPE_HEADER = 0
        const val TYPE_CHANGE_CON = 1
        const val TYPE_WIFI = 2

        fun newInstance() = HomeFragment()

    }
}
