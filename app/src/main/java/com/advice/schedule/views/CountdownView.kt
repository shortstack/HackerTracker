package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.shortstack.hackertracker.databinding.CountdownViewBinding
import java.text.DecimalFormat

class CountdownView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private val binding = CountdownViewBinding.inflate(LayoutInflater.from(context), this, true)

    private val format = DecimalFormat("00")

    fun setCountdown(time: Long) {
        val seconds: Long = time / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        binding.days.text = getText(days, "days")
        binding.hours.text = getText(hours % 24, "hours")
        binding.minutes.text = getText(minutes % 60, "minutes")
        binding.seconds.text = getText(seconds % 60, "seconds")
    }

    private fun getText(value: Long, unit: String): String {
        return "${format.format(value)} $unit"
    }
}