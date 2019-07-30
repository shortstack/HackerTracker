package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.getDateDifference
import com.shortstack.hackertracker.isToday
import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utils.TickTimer
import com.shortstack.hackertracker.utils.TimeUtil
import com.shortstack.hackertracker.utilities.now
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.row_header_time.view.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.*
import java.util.concurrent.TimeUnit

class TimeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), KoinComponent {

    private lateinit var date: Date

    private val timer: TickTimer by inject()

    private var disposable: Disposable? = null

    init {
        inflate(context, R.layout.row_header_time, this)
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
        val currentDate = MyClock().now()

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
