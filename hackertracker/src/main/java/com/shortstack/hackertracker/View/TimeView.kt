package com.shortstack.hackertracker.View

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
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

    private var date: Date? = null

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
        updateSubheader()
    }

    fun setDate(date: Date) {
        this.date = date
        render()
    }

    fun render() {
        header.text = ItemViewModel.getTimeStamp(context, date)
        updateSubheader()
    }

    private fun updateSubheader() {
        val currentDate = App.getCurrentDate()

        if (date!!.isToday() && date!!.after(currentDate) ) {
            subheader.visibility = View.VISIBLE

            val hourDiff = currentDate.getDateDifference(date!!, TimeUnit.HOURS)
            if (hourDiff >= 1) {
                subheader.text = String.format(context.getString(R.string.msg_in_hours), hourDiff )
            } else {
                val dateDiff = currentDate.getDateDifference(date!!, TimeUnit.MINUTES)
                subheader.text = String.format(context.getString(R.string.msg_in_mins), dateDiff )
            }

        } else {
            subheader!!.visibility = View.GONE
        }
    }
}
