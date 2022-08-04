package com.advice.schedule.ui.information.faq

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.FAQAnswer
import com.advice.schedule.models.local.FAQQuestion


class FAQAdapter(private val onExpandClickListener: (FAQQuestion) -> Unit) : ListAdapter<Any, RecyclerView.ViewHolder>(DIFF_UTILS) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FAQQuestion -> TYPE_QUESTION
            is FAQAnswer -> TYPE_ANSWER
            else -> error("Unknown item type: ${getItem(position).javaClass}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_QUESTION -> FAQQuestionViewHolder.inflate(parent)
            TYPE_ANSWER -> FAQAnswerViewHolder.inflate(parent)
            else -> error("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FAQQuestionViewHolder -> holder.render(getItem(position) as FAQQuestion, onExpandClickListener)
            is FAQAnswerViewHolder -> holder.render(getItem(position) as FAQAnswer)
        }
    }

    companion object {

        private const val TYPE_QUESTION = 0
        private const val TYPE_ANSWER = 1

        val DIFF_UTILS = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                if (oldItem is FAQAnswer && newItem is FAQAnswer)
                    return oldItem.id == newItem.id
                if (oldItem is FAQQuestion && newItem is FAQQuestion)
                    return oldItem.id == newItem.id
                return false
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                if (oldItem is FAQAnswer && newItem is FAQAnswer)
                    return oldItem.answer == newItem.answer && oldItem.isExpanded == newItem.isExpanded
                if (oldItem is FAQQuestion && newItem is FAQQuestion)
                    return oldItem.question == newItem.question //oldItem.isExpanded == newItem.isExpanded
                return false
            }
        }
    }
}