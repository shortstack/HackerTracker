package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.getDateDifference
import com.shortstack.hackertracker.isToday
import com.shortstack.hackertracker.utilities.MyClock
import com.shortstack.hackertracker.utilities.TickTimer
import com.shortstack.hackertracker.utilities.TimeUtil
import com.shortstack.hackertracker.utilities.now
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.row_header_time.view.*
import org.koin.standalone.KoinComponent
import java.util.*

class TimeView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs), KoinComponent {

    private lateinit var date: Date

    init {
        inflate(context, R.layout.row_header_time, this)
        orientation = LinearLayout.VERTICAL
    }

    fun setContent(content: Date) {
        date = content
        render()
    }

    fun render() {
        header.text = TimeUtil.getTimeStamp(context, date)
    }

}
