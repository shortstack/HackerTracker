package com.shortstack.hackertracker.ui.information

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.models.firebase.FirebaseFAQ
import com.shortstack.hackertracker.ui.ListFragment

class InformationFragment : ListFragment<FirebaseFAQ>() {

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
