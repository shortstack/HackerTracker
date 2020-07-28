package com.shortstack.hackertracker.ui.information.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.local.FAQ
import com.shortstack.hackertracker.utilities.Analytics
import kotlinx.android.synthetic.main.row_faq.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class FAQViewHolder(val view: View) : RecyclerView.ViewHolder(view), KoinComponent {

    companion object {
        fun inflate(parent: ViewGroup): FAQViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_faq, parent, false)
            return FAQViewHolder(view)
        }
    }

    private val analytics: Analytics by inject()

    fun render(faq: FAQ) {
        view.answer.visibility = View.GONE

        view.question.text = faq.question
        view.answer.text = faq.answer

        view.setOnClickListener {
            onFAQClick(faq)
        }
    }

    private fun onFAQClick(faq: FAQ) {
        val root = view.container

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
}
