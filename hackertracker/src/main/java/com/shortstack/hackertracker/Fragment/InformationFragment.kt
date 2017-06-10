package com.shortstack.hackertracker.Fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.Model.Information
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.Renderer.FAQRenderer
import com.shortstack.hackertracker.Renderer.GenericHeaderRenderer
import com.shortstack.hackertracker.Renderer.InformationRenderer
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class InformationFragment : Fragment() {

    var adapter: RendererAdapter<Any>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_recyclerview, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layout = LinearLayoutManager(context)
        list!!.layoutManager = layout

        val rendererBuilder = RendererBuilder<Any>()
                .bind(Array<String>::class.java, FAQRenderer())
                .bind(String::class.java, GenericHeaderRenderer())
                .bind(Information::class.java, InformationRenderer())

        adapter = RendererAdapter<Any>(rendererBuilder)
        list!!.adapter = adapter


        addInformationButtons()
        addFAQ()
    }

    private fun addInformationButtons() {
        adapter?.add(Information(context, R.array.location_information))
        adapter?.add(Information(context, R.array.badge_information))
        adapter?.add(Information(context, R.array.workshop_information))
        adapter?.add(Information(context, R.array.wifi_information))
        adapter?.add(Information(context, R.array.radio_information))
        // radio
        // workshop
        // location/time
        //
    }

    private fun addFAQ() {
        adapter?.add("FAQ")

        val myItems = resources.getStringArray(R.array.faq_questions)

        var i = 0
        while (i < myItems.size - 1) {
            val update = arrayOfNulls<String>(2)

            update[0] = myItems[i]
            update[1] = myItems[i + 1]

            adapter?.add(update)
            i += 2
        }
    }

    companion object {
        fun newInstance(): InformationFragment {
            return InformationFragment()
        }
    }
}
