package com.shortstack.hackertracker.ui.information

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.models.FAQ
import com.shortstack.hackertracker.ui.ListFragment

class InformationFragment : ListFragment<FAQ>() {

    companion object {
        fun newInstance() = InformationFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel<InformationViewModel>().faq.observe(this, Observer {
            onResource(it)
        })
    }
}
