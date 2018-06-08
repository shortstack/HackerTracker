package com.shortstack.hackertracker.ui.information.renderers

import android.support.transition.ChangeBounds
import android.support.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.firebase.jobdispatcher.Constraint
import com.pedrogomez.renderers.Renderer
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.models.FAQ
import kotlinx.android.synthetic.main.row_faq.view.*

class FAQRenderer : Renderer<FAQ>() {

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.row_faq, parent, false)
    }

    override fun hookListeners(rootView: View?) {
        rootView?.setOnClickListener {
            val root = rootView.container

            val isExpanded = rootView.answer.visibility == View.VISIBLE


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

    override fun render(payloads: List<Any>) {
        rootView?.answer?.visibility = View.GONE

        rootView.question.text = content.question
        rootView.answer.text = content.answer
    }
}
