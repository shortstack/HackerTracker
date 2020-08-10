package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.view_empty.view.*

class EmptyView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.view_empty, this)
        getStyle(context, attrs)
    }

    private fun getStyle(context: Context?, attrs: AttributeSet?) {
        if (context == null) return

        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.EmptyView, 0, 0)

        try {
            if (array.hasValue(R.styleable.EmptyView_evIcon))
                icon.setImageResource(array.getResourceId(R.styleable.EmptyView_evIcon, R.drawable.skull_lg))

            if (array.hasValue(R.styleable.EmptyView_evTitle))
                title.text = array.getString(R.styleable.EmptyView_evTitle)

            if (array.hasValue(R.styleable.EmptyView_evMessage))
                message.text = array.getString(R.styleable.EmptyView_evMessage)
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
            setTitle(null)
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
        setText(title, text)
    }

    private fun setMessage(text: String?) {
        setText(message, text)
    }

    private fun setText(view: TextView, text: String?) {
        view.apply {
            this.text = text

            visibility = if (text?.isNotBlank() == true) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

}