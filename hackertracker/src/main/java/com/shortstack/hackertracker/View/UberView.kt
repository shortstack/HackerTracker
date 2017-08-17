package com.shortstack.hackertracker.View

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.FrameLayout
import com.shortstack.hackertracker.Adapter.SpinnerAdapter
import com.shortstack.hackertracker.Common.Constants
import com.shortstack.hackertracker.R
import com.uber.sdk.android.core.UberSdk
import com.uber.sdk.android.rides.RideParameters
import com.uber.sdk.core.auth.Scope
import com.uber.sdk.rides.client.SessionConfiguration
import kotlinx.android.synthetic.main.alert_uber.view.*
import java.util.*

class UberView(context: Context) : FrameLayout(context) {


    init {
        inflate()
        initUberSdk()
        initSpinner()
    }

    private fun inflate() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_uber, null)
        addView(view)
    }

    private fun initUberSdk() {
        val config = SessionConfiguration.Builder()
                .setClientId(Constants.UBER_CLIENT_ID)
                .setEnvironment(SessionConfiguration.Environment.PRODUCTION)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS))
                .build()
        UberSdk.initialize(config)
    }

    private fun initSpinner() {
        spinner.onItemSelectedListener = UberSelectListener()
        val adapter = SpinnerAdapter(context, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        adapter.addAll(*Constants.UBER_LOCATIONS)
        spinner.adapter = adapter
        spinner.setSelection(adapter.count)
    }

    inner class UberSelectListener : OnItemSelectedListener {


        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {

            val nickname = Constants.UBER_LOCATIONS[pos]
            val address = Constants.UBER_ADDRESSES[pos]

            // set up ride request button
            val rideParams = RideParameters.Builder()
                    .setDropoffLocation(0.0, 0.0, nickname, address)
                    .build()

            uber_request_button.setRideParameters(rideParams)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}
