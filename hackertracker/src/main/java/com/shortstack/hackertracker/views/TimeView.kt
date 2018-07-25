package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.shortstack.hackertracker.*
import com.shortstack.hackertracker.utils.TickTimer
import com.shortstack.hackertracker.utils.TimeUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.row_header_time.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private lateinit var date: Date

    @Inject
    lateinit var timer: TickTimer

    private var disposable: Disposable? = null

    init {
        inflate(context, R.layout.row_header_time, this)
        App.application.component.inject(this)

        orientation = LinearLayout.VERTICAL
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        disposable = timer.observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe { render() }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposable?.dispose()
        disposable = null
    }

    fun setContent(content: Date) {
        date = content
        render()
    }

    fun render() {
        header.text = TimeUtil.getTimeStamp(context, date)
        renderSubHeader()
    }

    private fun renderSubHeader() {
        val currentDate = Date().now()

        // Not today, or already started.
        if (!date.isToday() || !date.after(currentDate)) {
            subheader.visibility = View.GONE
            return
        }

        subheader.visibility = View.VISIBLE

        val inHours = currentDate.getDateDifference(date, TimeUnit.HOURS)
        if (inHours >= 1) {
            subheader.text = context.getString(R.string.msg_in_hours, inHours)
        } else {
            val inMinutes = currentDate.getDateDifference(date, TimeUnit.MINUTES)
            subheader.text = context.getString(R.string.msg_in_mins, inMinutes)
        }
    }
}
