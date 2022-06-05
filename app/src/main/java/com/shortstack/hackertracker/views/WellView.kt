package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ViewWellBinding

class WellView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private val binding = ViewWellBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.WellView, 0, 0)
        try {
            val hasAction = array.getBoolean(R.styleable.WellView_wellView_hasDismiss, false)
            binding.action.isVisible = hasAction
        } finally {
            array.recycle()
        }
    }

    fun setOnCloseListener(listener: () -> Unit) {
        binding.action.isVisible = true
        binding.action.setOnClickListener {
            (parent as ViewGroup).removeView(this)
            listener.invoke()
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (binding == null || binding.container == null) {
            super.addView(child, index, params)
        } else {
            binding.container.addView(child, index, params)
        }
    }
}