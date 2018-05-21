package com.shortstack.hackertracker.ui.information

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.Constants
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DEFCONDatabaseController
import com.shortstack.hackertracker.models.FAQ
import com.shortstack.hackertracker.models.Information
import com.shortstack.hackertracker.ui.GenericHeaderRenderer
import com.shortstack.hackertracker.ui.information.renderers.FAQRenderer
import com.shortstack.hackertracker.ui.information.renderers.InformationRenderer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import javax.inject.Inject

class InformationFragment : Fragment() {

    lateinit var adapter: RendererAdapter<Any>

    @Inject
    lateinit var database: DEFCONDatabaseController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.application.myComponent.inject(this)

        list.layoutManager = LinearLayoutManager(context)

        loading_progress.visibility = View.VISIBLE

        adapter = RendererAdapter(RendererBuilder<Any>()
                .bind(FAQ::class.java, FAQRenderer())
                .bind(String::class.java, GenericHeaderRenderer())
                .bind(Information::class.java, InformationRenderer()))

        list.adapter = adapter



        getFAQ().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    loading_progress.visibility = View.GONE

                    if (database.databaseName == Constants.DEFCON_DATABASE_NAME) {
                        addInformationButtons()
                    }

                    adapter.addAllAndNotify(it)
                }
    }

    private fun addInformationButtons() {
        adapter.add(Information(context, R.array.location_information))
        adapter.add(Information(context, R.array.badge_information))
        adapter.add(Information(context, R.array.workshop_information))
        adapter.add(Information(context, R.array.wifi_information))
        adapter.add(Information(context, R.array.radio_information))
        adapter.notifyItemRangeInserted(0, adapter.collection.size)
    }

    private fun getFAQ(): Observable<List<FAQ>> {

        val items = if (database.databaseName == Constants.SHMOOCON_DATABASE_NAME) {
            resources.getStringArray(R.array.faq_questions_shmoo)
        } else {
            resources.getStringArray(R.array.faq_questions)
        }


        return Observable.create { subscriber ->
            subscriber.onNext(items.toList().windowed(size = 2, step = 2).map { FAQ(it[0], it[1]) })
            subscriber.onComplete()
        }
    }

    companion object {
        fun newInstance(): InformationFragment {
            return InformationFragment()
        }
    }
}
