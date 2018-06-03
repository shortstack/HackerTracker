package com.shortstack.hackertracker.ui.information

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.FAQ
import com.shortstack.hackertracker.models.Information
import com.shortstack.hackertracker.ui.GenericHeaderRenderer
import com.shortstack.hackertracker.ui.information.renderers.FAQRenderer
import com.shortstack.hackertracker.ui.information.renderers.InformationRenderer
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import javax.inject.Inject

class InformationFragment : Fragment() {

    lateinit var adapter: RendererAdapter<Any>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading_progress.visibility = View.VISIBLE

        list.layoutManager = LinearLayoutManager(context)

        adapter = RendererAdapter(RendererBuilder<Any>()
                .bind(FAQ::class.java, FAQRenderer())
                .bind(String::class.java, GenericHeaderRenderer())
                .bind(Information::class.java, InformationRenderer()))

        list.adapter = adapter

        val informationViewModel = ViewModelProviders.of(this).get(InformationViewModel::class.java)
        informationViewModel.faq.observe(this, Observer {
            loading_progress.visibility = View.GONE

            adapter.clearAndNotify()

            if (it != null) {

//                if (database.getCurrentCon().title == Constants.DEFCON_DATABASE_NAME) {
//                    addInformationButtons()
//                }

                adapter.addAllAndNotify(it)
            }
        })
    }

    private fun addInformationButtons() {
        val context = context ?: return

        adapter.add(Information(context, R.array.location_information))
        adapter.add(Information(context, R.array.badge_information))
        adapter.add(Information(context, R.array.workshop_information))
        adapter.add(Information(context, R.array.wifi_information))
        adapter.add(Information(context, R.array.radio_information))
        adapter.notifyItemRangeInserted(0, adapter.collection.size)
    }

    companion object {

        fun newInstance() = InformationFragment()

    }
}
