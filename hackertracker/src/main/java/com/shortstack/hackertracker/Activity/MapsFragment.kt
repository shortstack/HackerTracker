package com.shortstack.hackertracker.Activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.Alert.MaterialAlert
import com.shortstack.hackertracker.Analytics.AnalyticsController
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.View.UberView
import kotlinx.android.synthetic.main.activity_maps.*

class MapsFragment : Fragment() {

    val ASSET_NAME = "map_defcon_small.pdf"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.activity_maps, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewer!!.fromAsset(ASSET_NAME).onLoad { progress_container!!.visibility = View.GONE }.load()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_uber -> {
                App.getApplication().analyticsController.tagCustomEvent(AnalyticsController.Analytics.UBER)
                MaterialAlert.create(context).setTitle(R.string.uber).setView(UberView(context)).show()
                return true
            }
        }

        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.maps, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Logger.d("onDestroy maps")
        if (viewer != null) {
            Logger.d("Recycling pdf.")
            viewer.recycle()
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return MapsFragment()
        }
    }
}
