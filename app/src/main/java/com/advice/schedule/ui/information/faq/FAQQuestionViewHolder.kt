package com.advice.schedule.ui.information.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.FAQQuestion
import com.shortstack.hackertracker.databinding.RowFaqQuestionBinding

class FAQQuestionViewHolder(private val binding: RowFaqQuestionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(faq: FAQQuestion, onExpandClickListener: (FAQQuestion) -> Unit) = with(binding) {
        question.text = faq.question

        expand.rotation = if (faq.isExpanded) {
            0f
        } else {
            90f
        }

        root.setOnClickListener {
            val target = if (faq.isExpanded) {
                90f
            } else {
                0f
            }
            expand.animate().rotation(target).start()
            onExpandClickListener.invoke(faq)
        }
    }

    companion object {
        fun inflate(parent: ViewGroup): FAQQuestionViewHolder {
            val binding = RowFaqQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FAQQuestionViewHolder(binding)
        }
    }
}
