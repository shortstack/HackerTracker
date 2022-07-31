package com.advice.schedule.ui.information.info.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.shortstack.hackertracker.databinding.ViewCodeOfConductBinding

class CodeOfConductView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val binding = ViewCodeOfConductBinding.inflate(LayoutInflater.from(context), this, true)

    fun setText(conduct: String?) {
        binding.content.text = conduct?.replace("\\n", "\n")
            ?.replace(" ------------------------------  ", "\n\n")
            ?.replace(" -", "\n -")
    }
}