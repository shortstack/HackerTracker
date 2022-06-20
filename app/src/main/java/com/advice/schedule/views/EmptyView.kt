package com.advice.schedule.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ViewEmptyBinding

class EmptyView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val binding = ViewEmptyBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.EmptyView, 0, 0)

        try {
            if (array.hasValue(R.styleable.EmptyView_evTitle))
                binding.title.text = array.getString(R.styleable.EmptyView_evTitle)

            if (array.hasValue(R.styleable.EmptyView_evMessage))
                binding.message.text = array.getString(R.styleable.EmptyView_evMessage)
        } finally {
            array.recycle()
        }
    }


    fun showDefault() {
        show()

        setTitle(null)
        setMessage(null)
    }

    fun showNoResults(query: String? = null) {
        show()

        if (query != null) {
            setTitle(context.getString(R.string.no_results_for, query))
            setMessage(context.getString(R.string.no_results_message))
        } else {
            setTitle("404")
            setMessage(null)
        }
    }

    fun showError(msg: String?) {
        show()

        setTitle(context.getString(R.string.error_title))
        setMessage(msg)
    }

    fun hide() {
        visibility = View.GONE
    }

    fun show() {
        visibility = View.VISIBLE
    }

    private fun setTitle(text: String?) {
        binding.title.text = text
        binding.title.visibility = if (text != null) View.VISIBLE else View.GONE
    }

    private fun setMessage(text: String?) {
        binding.message.text = text
        binding.message.visibility = if (text != null) View.VISIBLE else View.GONE
    }
}