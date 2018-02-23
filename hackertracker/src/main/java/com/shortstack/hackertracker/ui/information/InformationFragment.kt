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
import com.shortstack.hackertracker.models.FAQ
import com.shortstack.hackertracker.models.Information
import com.shortstack.hackertracker.ui.GenericHeaderRenderer
import com.shortstack.hackertracker.ui.information.renderers.FAQRenderer
import com.shortstack.hackertracker.ui.information.renderers.InformationRenderer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class InformationFragment : Fragment() {

    lateinit var adapter: RendererAdapter<Any>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayoutManager(context)
        list!!.layoutManager = layout

        val rendererBuilder = RendererBuilder<Any>()
                .bind(FAQ::class.java, FAQRenderer())
                .bind(String::class.java, GenericHeaderRenderer())
                .bind(Information::class.java, InformationRenderer())
        loading_progress.visibility = View.GONE;

        adapter = RendererAdapter<Any>(rendererBuilder)
        list!!.adapter = adapter

        if (App.application.databaseController.databaseName != Constants.SHMOOCON_DATABASE_NAME && App.application.databaseController.databaseName != Constants.HACKWEST_DATABASE_NAME) {
            addInformationButtons()
        }

        getFAQ().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
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
        var myItems = resources.getStringArray(R.array.faq_questions);

        if (App.application.databaseController.databaseName == Constants.SHMOOCON_DATABASE_NAME) {
            myItems = resources.getStringArray(R.array.faq_questions_shmoo);
        } else if (App.application.databaseController.databaseName == Constants.HACKWEST_DATABASE_NAME) {
            myItems = resources.getStringArray(R.array.faq_questions_hw);
        }

        val result = ArrayList<FAQ>()

        var i = 0
        while (i < myItems.size - 1) {
            result.add(FAQ(myItems[i], myItems[i + 1]))
            i += 2
        }

        return Observable.create {
            subscriber ->
            subscriber.onNext(result)
            subscriber.onComplete()
        }
    }

    companion object {
        fun newInstance(): InformationFragment {
            return InformationFragment()
        }
    }
}
