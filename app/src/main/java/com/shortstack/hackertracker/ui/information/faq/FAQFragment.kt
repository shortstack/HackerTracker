package com.shortstack.hackertracker.ui.information.faq

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.firebase.FirebaseFAQ
import com.shortstack.hackertracker.ui.HackerTrackerViewModel
import com.shortstack.hackertracker.ui.ListFragment
import com.shortstack.hackertracker.ui.activities.MainActivity

class FAQFragment : ListFragment<FirebaseFAQ>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel =
            ViewModelProvider(context as MainActivity)[HackerTrackerViewModel::class.java]
        viewModel.faq.observe(viewLifecycleOwner) {
            onResource(it)
        }
    }

    override fun getPageTitle(): String {
        return getString(R.string.faq)
    }

    companion object {
        fun newInstance() = FAQFragment()
    }
}
