package com.shortstack.hackertracker.View

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import butterknife.ButterKnife
import com.shortstack.hackertracker.Application.App
import com.shortstack.hackertracker.Event.RefreshTimerEvent
import com.shortstack.hackertracker.Model.ItemViewModel
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.getDateDifference
import com.shortstack.hackertracker.isToday
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.row_header_time.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class TimeView : LinearLayout {

    private var mDate: Date? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        orientation = LinearLayout.VERTICAL
        inflate()
    }

    private fun inflate() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.row_header_time, null)
        ButterKnife.bind(this, view)

        addView(view)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        App.application.registerBusListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        App.application.unregisterBusListener(this)
    }

    @Subscribe
    fun onRefreshTimeEvent(event: RefreshTimerEvent) {
        val currentDate = App.getCurrentDate()
        updateSubheader(currentDate)
    }

    fun setDate(date: Date) {
        mDate = date
        render()
    }

    fun render() {
        val currentDate = App.getCurrentDate()

        header!!.text = ItemViewModel.getTimeStamp(context, mDate)

        updateSubheader(currentDate)
    }

    private fun updateSubheader(currentDate: Date) {

        if (mDate!!.isToday()) {
            subheader!!.visibility = View.VISIBLE

            var stamp = ""

            val hourDiff = currentDate.getDateDifference(mDate!!, TimeUnit.HOURS)
            if (hourDiff >= 1) {
                stamp += ("in " + hourDiff + " hr" + if (hourDiff > 1) "s" else "")
            } else {
                val dateDiff = currentDate.getDateDifference(mDate!!, TimeUnit.MINUTES)
                stamp += ("in " + dateDiff + " min" + if (dateDiff > 1) "s" else "")
            }

            subheader!!.text = stamp
        } else {
            subheader!!.visibility = View.GONE
        }
    }
}
