package com.shortstack.hackertracker.Renderer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row_faq.view.*

class FAQRenderer : Renderer<Array<String>>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_faq, parent, false)
    }

    override fun render(payloads: List<Any>) {
        rootView.question.text = content[POS_QUESTION]
        rootView.answer.text = content[POS_ANSWER]
    }

    companion object {
        val POS_QUESTION = 0
        val POS_ANSWER = 1
    }
}
