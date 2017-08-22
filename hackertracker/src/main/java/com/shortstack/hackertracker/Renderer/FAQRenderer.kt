package com.shortstack.hackertracker.Renderer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.Model.FAQ
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.row_faq.view.*

class FAQRenderer : Renderer<FAQ>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_faq, parent, false)
    }

    override fun render(payloads: List<Any>) {
        rootView.question.text = content.question
        rootView.answer.text = content.answer
    }
}
