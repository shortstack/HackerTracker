package com.shortstack.hackertracker.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Model.Item
import com.shortstack.hackertracker.Model.Navigation
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Renderer.*
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import java.text.SimpleDateFormat

class HomeFragment : Fragment(), HomeContract.View {

    private var presenter : HomeContract.Presenter? = null
    lateinit var adapter : RendererAdapter<Any>

    override fun setPresenter(presenter : HomeContract.Presenter) {
        this.presenter = presenter
    }

    override fun setProgressIndicator(active : Boolean) {
        loading_progress.visibility = if (active) View.VISIBLE else View.GONE
    }

    override fun showRecentUpdates(items : Array<Item>) {
        var recentDate = ""
        val size = adapter.collection.size


        for (item in items) {
            if (item.updatedAt != recentDate) {
                recentDate = item.updatedAt
                val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item.updatedAt)
                adapter.add("Updated " + SimpleDateFormat("MMMM dd h:mm aa").format(date))
            }

            adapter.add(item)
        }

        adapter.notifyItemRangeInserted(size, adapter.collection.size - size)
    }

    override fun showLoadingRecentUpdatesError() {
        Toast.makeText(context, "Could not fetch recent updates.", Toast.LENGTH_SHORT).show()
    }

    override fun showLastSyncTimestamp(timestamp : String) {
        adapter.add(timestamp)
    }

    override fun isActive() = isAdded


    override fun onCreateView(inflater : LayoutInflater?, container : ViewGroup?, savedInstanceState : Bundle?) : View? {
        return inflater!!.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view : View?, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rendererBuilder = RendererBuilder<Any>()
                .bind(TYPE_HEADER, HomeHeaderRenderer())
                .bind(String::class.java, SubHeaderRenderer())
                .bind(Item::class.java, ItemRenderer())
                .bind(Navigation::class.java, ActivityNavRenderer())
                .bind(TYPE_CHANGE_CON, ChangeConRenderer())

        val layout = LinearLayoutManager(context)
        list.layoutManager = layout

        adapter = RendererAdapter<kotlin.Any>(rendererBuilder)
        list.adapter = adapter

        // TODO: This should use DI.
        setPresenter(HomePresenter(context, this))
    }


    override fun onResume() {
        super.onResume()
//        presenter?.takeView(this)
    }

    override fun onDestroy() {
//        presenter?.dropView()
        super.onDestroy()
    }

    override fun addAdapterItem(item : Any ) {
        adapter.addAndNotify(item)
    }

    companion object {

        val TYPE_HEADER = 0
        val TYPE_CHANGE_CON = 1

        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }

}
