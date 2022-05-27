package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.shortstack.hackertracker.databinding.HeaderHomeBinding

class HeaderView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private val binding = HeaderHomeBinding.inflate(LayoutInflater.from(context), this, true)

    fun setCountdown(time: String?) {
        binding.countdown.text = time
    }


}