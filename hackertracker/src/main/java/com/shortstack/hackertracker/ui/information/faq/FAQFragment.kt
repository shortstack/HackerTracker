package com.shortstack.hackertracker.ui.information.faq

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.models.firebase.FirebaseFAQ
import com.shortstack.hackertracker.ui.ListFragment

class FAQFragment : ListFragment<FirebaseFAQ>() {

    companion object {
        fun newInstance() = FAQFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getViewModel<FAQViewModel>().faq.observe(this, Observer {
            onResource(it)
        })
    }
}
