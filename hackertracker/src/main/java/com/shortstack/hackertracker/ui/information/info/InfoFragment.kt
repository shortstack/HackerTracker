package com.shortstack.hackertracker.ui.information.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.database.DatabaseManager
import kotlinx.android.synthetic.main.fragment_info.*
import org.koin.android.ext.android.inject
import org.koin.standalone.inject

class InfoFragment : Fragment() {

    companion object {
        fun newInstance() = InfoFragment()
    }

    private val database: DatabaseManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        database.conference.observe(this, Observer {
            conduct.setText(it.conduct)
        })


    }

}