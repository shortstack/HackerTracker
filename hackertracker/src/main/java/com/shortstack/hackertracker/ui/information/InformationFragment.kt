package com.shortstack.hackertracker.ui.information

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.RendererAdapter
import com.pedrogomez.renderers.RendererBuilder
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FAQ
import com.shortstack.hackertracker.models.Information
import com.shortstack.hackertracker.ui.GenericHeaderRenderer
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.information.renderers.FAQRenderer
import com.shortstack.hackertracker.ui.information.renderers.InformationRenderer
import kotlinx.android.synthetic.main.fragment_recyclerview.*

class InformationFragment : ListFragment<FAQ>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel<InformationViewModel>().faq.observe(this, Observer {
            onResource(it)
        })
    }

    override fun initAdapter(): RendererAdapter<Any> {
        return RendererAdapter(RendererBuilder<Any>()
                .bind(FAQ::class.java, FAQRenderer())
                .bind(String::class.java, GenericHeaderRenderer())
                .bind(Information::class.java, InformationRenderer()))
    }

    companion object {
        fun newInstance() = InformationFragment()
    }
}
