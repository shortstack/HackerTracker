package com.shortstack.hackertracker.ui.extras

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.firebase.FirebaseHacker
import com.shortstack.hackertracker.ui.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_hacker.*

class HackerFragment : Fragment() {

    companion object {

        private const val EXTRA_HACKER = "extra_hacker"

        fun newInstance(hacker: FirebaseHacker): HackerFragment {
            val fragment = HackerFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_HACKER, hacker)
            fragment.arguments = bundle


            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hacker, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val hacker = arguments?.getParcelable(EXTRA_HACKER) as FirebaseHacker
        username.text = hacker.username
        rank.text = "n00bz"


        close.setOnClickListener {
            (context as MainActivity).popBackStack()
        }
    }

}