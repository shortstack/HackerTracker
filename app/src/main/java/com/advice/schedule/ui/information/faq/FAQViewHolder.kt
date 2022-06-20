package com.advice.schedule.ui.information.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.advice.schedule.models.local.FAQ
import com.advice.schedule.utilities.Analytics
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.RowFaqBinding
import org.koin.core.KoinComponent
import org.koin.core.inject

class FAQViewHolder(private val binding: RowFaqBinding) :
    RecyclerView.ViewHolder(binding.root), KoinComponent {

    // todo: extract
    private val analytics: Analytics by inject()

    fun render(faq: FAQ) {
        binding.answer.visibility = View.GONE

        binding.question.text = faq.question
        binding.answer.text = faq.answer

        binding.root.setOnClickListener {
            onFAQClick(faq)
        }
    }

    private fun onFAQClick(faq: FAQ) {
        val root = binding.container

        val isExpanded = faq.isExpanded

        faq.isExpanded = !faq.isExpanded

        if (!isExpanded) {
            val event = Analytics.CustomEvent(Analytics.FAQ_VIEW).also {
                it.putCustomAttribute("Question", faq.question)
            }
            analytics.logCustom(event)
        }


        val visibility = if (isExpanded) View.GONE else View.VISIBLE

        val constraintSet1 = ConstraintSet()
        constraintSet1.clone(root)

        constraintSet1.setVisibility(R.id.answer, visibility)

        val transition = ChangeBounds()
        transition.interpolator = AccelerateDecelerateInterpolator()


        TransitionManager.beginDelayedTransition(root, transition)
        constraintSet1.applyTo(root)
    }

    companion object {
        fun inflate(parent: ViewGroup): FAQViewHolder {
            val binding = RowFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return FAQViewHolder(binding)
        }
    }
}
