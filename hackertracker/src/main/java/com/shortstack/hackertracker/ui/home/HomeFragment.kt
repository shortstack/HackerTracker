package com.shortstack.hackertracker.ui.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.pedrogomez.renderers.RendererContent
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.models.Event
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.ui.home.renderers.ActivityNavRenderer
import com.shortstack.hackertracker.ui.home.renderers.ChangeConRenderer
import com.shortstack.hackertracker.models.Item
import com.shortstack.hackertracker.models.Navigation
import com.shortstack.hackertracker.ui.home.renderers.HomeHeaderRenderer
import com.shortstack.hackertracker.ui.home.renderers.SubHeaderRenderer
import com.shortstack.hackertracker.ui.schedule.renderers.EventRenderer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var adapter: RendererAdapter<Any>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rendererBuilder = RendererBuilder<Any>()
                .bind(TYPE_HEADER, HomeHeaderRenderer())
                .bind(String::class.java, SubHeaderRenderer())
                .bind(Event::class.java, EventRenderer())
                .bind(Navigation::class.java, ActivityNavRenderer())
                .bind(TYPE_CHANGE_CON, ChangeConRenderer())

        val layout = LinearLayoutManager(context)
        list.layoutManager = layout

        adapter = RendererAdapter(rendererBuilder)
        list.adapter = adapter


        fetchRecentUpdates()
    }

    private fun fetchRecentUpdates() {
        setProgressIndicator(true)

        App.application.db.eventDao().getRecentlyUpdated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setProgressIndicator(false)

                    // TODO: This could be a lot clearer.
                    addAdapterItem(getHeader())
                    addAdapterItem(getInformationNav())
                    addAdapterItem(getChangeConCard())

                    showLastSyncTimestamp(getLastSyncTimestamp())

                    showRecentUpdates(it)
                }, {
                    if (isAdded) {
                        setProgressIndicator(false)
                        showLoadingRecentUpdatesError()
                    }
                })
    }


    private fun setProgressIndicator(active: Boolean) {
        loading_progress.visibility = if (active) View.VISIBLE else View.GONE
    }

    private fun showRecentUpdates(items: List<Event>) {
        var recentDate = ""
        val size = adapter.collection.size


        for (item in items) {
//            if (item.updatedAt != recentDate) {
//                recentDate = item.updatedAt
//                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item.updatedAt)
//                adapter.add("Updated " + SimpleDateFormat("MMMM dd h:mm aa").format(date))
//            }

            adapter.add(item)
        }

        adapter.notifyItemRangeInserted(size, adapter.collection.size - size)
    }

    private fun showLoadingRecentUpdatesError() {
        Toast.makeText(context, "Could not fetch recent updates.", Toast.LENGTH_SHORT).show()
    }

    private fun showLastSyncTimestamp(timestamp: String) {
        adapter.add(timestamp)
    }

    private fun getHeader(): Any {
        return RendererContent<Void>(null, TYPE_HEADER)
    }

    private fun getChangeConCard(): Any {
        return RendererContent<Void>(null, TYPE_CHANGE_CON)
    }

    private fun getInformationNav(): Navigation? {
        val context = context ?: return null
        return Navigation(context.getString(R.string.nav_help_title), context.getString(R.string.nav_help_body), InformationFragment::class.java)
    }

    private fun getLastSyncTimestamp(): String {
        val storage = App.application.storage
        val lastDate: String

        val cal = Calendar.getInstance()
        cal.time = Date(storage.lastRefresh)

        val refresh = storage.lastRefresh
        if (refresh == 0L) {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(storage.lastUpdated)
            lastDate = "Last synced " + App.getRelativeDateStamp(date)
        } else {
            lastDate = "Last synced " + App.getRelativeDateStamp(Date(refresh))
        }

        return "Last updated\n" + lastDate.toLowerCase()
    }

    private fun addAdapterItem(item: Any?) {
        adapter.addAndNotify(item)
    }

    companion object {

        val TYPE_HEADER = 0
        val TYPE_CHANGE_CON = 1

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

}
