package com.shortstack.hackertracker.ui.activities

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.github.stkent.amplify.prompt.DefaultLayoutPromptView
import com.github.stkent.amplify.prompt.interfaces.IPromptPresenter
import com.github.stkent.amplify.tracking.interfaces.IEvent
import com.github.stkent.amplify.tracking.interfaces.IEventListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.R

class ReviewBottomSheet : BottomSheetDialogFragment(), IPromptPresenter {

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_review, null)
        dialog.setContentView(view)

        val promptView = view.findViewById<DefaultLayoutPromptView>(R.id.prompt_view)
        promptView.presenter.start()
        promptView.presenter.addPromptEventListener(this)
    }


    override fun addPromptEventListener(promptEventListener: IEventListener) {

    }

    override fun start() {

    }

    override fun reportUserOpinion(userOpinion: IPromptPresenter.UserOpinion) {
        // TODO: Add analytics for user feedback.
    }

    override fun reportUserFeedbackAction(userFeedbackAction: IPromptPresenter.UserFeedbackAction) {
        // TODO: Add analytics for user actions.
    }

    override fun generateStateBundle() = Bundle()

    override fun restoreStateFromBundle(bundle: Bundle) {}

    override fun notifyEventTriggered(event: IEvent) {
        Logger.d("Review event_unbookmarked triggered: " + event.trackingKey)
        if (event.trackingKey == "PROMPT_DISMISSED" || event.trackingKey == "THANKS_SHOWN") {
            dismiss()
        }
    }

    companion object {

        fun newInstance() = ReviewBottomSheet()

    }
}
