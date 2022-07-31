package com.advice.schedule.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.shortstack.hackertracker.databinding.HeaderHomeBinding

class HeaderView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private val binding = HeaderHomeBinding.inflate(LayoutInflater.from(context), this, true)

    fun setCountdown(time: Long) {
        binding.countdown.isVisible = time > 0
        binding.countdown.setCountdown(time)
    }
}