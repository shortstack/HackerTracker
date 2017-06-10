package com.shortstack.hackertracker.Fragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import com.shortstack.hackertracker.R

class InformationFragment : AppCompatActivity() {

    private val ARG_LAYOUT_RES = "layout_res"
    private val DEFAULT_LAYOUT = R.layout.alert_badges

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var res = DEFAULT_LAYOUT
        if (intent.extras != null) {
            res = intent.extras.getInt(ARG_LAYOUT_RES, DEFAULT_LAYOUT)
        }

        setContentView(res)
        ButterKnife.bind(this)
    }
}
