package com.shortstack.hackertracker.ui.home

import android.annotation.SuppressLint
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
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.models.Item
import com.shortstack.hackertracker.models.Navigation
import com.shortstack.hackertracker.ui.home.renderers.ActivityNavRenderer
import com.shortstack.hackertracker.ui.home.renderers.ChangeConRenderer
import com.shortstack.hackertracker.ui.home.renderers.HomeHeaderRenderer
import com.shortstack.hackertracker.ui.home.renderers.SubHeaderRenderer
import com.shortstack.hackertracker.ui.information.InformationFragment
import com.shortstack.hackertracker.ui.schedule.renderers.ItemRenderer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import java.text.SimpleDateFormat
import javax.inject.Inject

class HomeFragment : Fragment() {

    private lateinit var adapter: RendererAdapter<Any>

    @Inject
    lateinit var dataabase: DEFCONDatabaseController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.application.myComponent.inject(this)

        val rendererBuilder = RendererBuilder<Any>()
                .bind(TYPE_HEADER, HomeHeaderRenderer())
                .bind(String::class.java, SubHeaderRenderer())
                .bind(Item::class.java, ItemRenderer())
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

        dataabase.getRecent()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setProgressIndicator(false)

                    adapter.addAndNotify(getHeader())
                    adapter.addAndNotify(getInformationNav())
                    adapter.addAndNotify(getChangeConCard())

                    showRecentUpdates(it.toTypedArray())
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

    @SuppressLint("SimpleDateFormat")
    private fun showRecentUpdates(items: Array<Item>) {
        val size = adapter.collection.size

        items.groupBy { it.updatedAt }.forEach {
            val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(it.key)
            adapter.add("Updated " + SimpleDateFormat("MMMM dd h:mm aa").format(date))
            adapter.addAll(it.value)
        }

        adapter.notifyItemRangeInserted(size, adapter.collection.size - size)
    }

    private fun showLoadingRecentUpdatesError() {
        Toast.makeText(context, "Could not fetch recent updates.", Toast.LENGTH_SHORT).show()
    }

    private fun getHeader() = RendererContent<Void>(null, TYPE_HEADER)

    private fun getChangeConCard() = RendererContent<Void>(null, TYPE_CHANGE_CON)

    private fun getInformationNav() = Navigation(context.getString(R.string.nav_help_title), context.getString(R.string.nav_help_body), InformationFragment::class.java)


    companion object {

        const val TYPE_HEADER = 0
        const val TYPE_CHANGE_CON = 1

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
