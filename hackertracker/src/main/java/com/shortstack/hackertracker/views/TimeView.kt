package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.shortstack.hackertracker.*
import com.shortstack.hackertracker.models.EventViewModel
import com.shortstack.hackertracker.utils.TickTimer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimeView : LinearLayout {

    private var date: Date? = null

    @Inject
    lateinit var timer: TickTimer

    private var disposable: Disposable? = null


    init {
        App.application.component.inject(this)
    }


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
        disposable = timer.observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    updateSubheader()
                }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposable?.dispose()
        disposable = null
    }

    fun setDate(date: Date) {
        this.date = date
        render()
    }

    fun render() {
        header.text = EventViewModel.getTimeStamp(context, date)
        updateSubheader()
    }

    private fun updateSubheader() {
        val currentDate = Date().now()

        if (date!!.isToday() && date!!.after(currentDate)) {
            subheader.visibility = View.VISIBLE

            val hourDiff = currentDate.getDateDifference(date!!, TimeUnit.HOURS)
            if (hourDiff >= 1) {
                subheader.text = String.format(context.getString(R.string.msg_in_hours), hourDiff)
            } else {
                val dateDiff = currentDate.getDateDifference(date!!, TimeUnit.MINUTES)
                subheader.text = String.format(context.getString(R.string.msg_in_mins), dateDiff)
            }

        } else {
            subheader!!.visibility = View.GONE
        }
    }
}
