package com.advice.schedule.ui.information.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.FAQAnswer
import com.shortstack.hackertracker.databinding.RowFaqAnswerBinding

class FAQAnswerViewHolder(private val binding: RowFaqAnswerBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(faq: FAQAnswer) = with(binding) {
        answer.text = faq.answer
    }

    companion object {
        fun inflate(parent: ViewGroup): FAQAnswerViewHolder {
            val binding = RowFaqAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FAQAnswerViewHolder(binding)
        }
    }
}
