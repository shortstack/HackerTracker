package com.shortstack.hackertracker.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.view_empty.view.*

/**
 * Created by Chris on 3/31/2018.
 */
class EmptyView(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

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
        title.text = null
        message.text = null
    }

    fun showNoResults(query: String? = null) {
        if (query != null) {
            title.text = context.getString(R.string.no_results_for, query)
            message.text = context.getString(R.string.no_results_message)
        } else {
            title.text = null
            message.text = null
        }
    }

    fun showError(msg: String?) {
        title.text = context.getString(R.string.error_title)
        message.text = msg
    }


}