package com.shortstack.hackertracker.BottomSheet

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import com.github.stkent.amplify.prompt.interfaces.IPromptPresenter
import com.github.stkent.amplify.tracking.interfaces.IEvent
import com.github.stkent.amplify.tracking.interfaces.IEventListener
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.bottom_sheet_review.view.*

class ReviewBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_review, null)
        dialog.setContentView(view)

        view.prompt_view!!.presenter.start()
        view.prompt_view!!.presenter.addPromptEventListener(object : IPromptPresenter {
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

            override fun generateStateBundle(): Bundle {
                return Bundle()
            }

            override fun restoreStateFromBundle(bundle: Bundle) {}

            override fun notifyEventTriggered(event: IEvent) {
                Logger.d("Review event triggered: " + event.trackingKey)
                if (event.trackingKey == "PROMPT_DISMISSED" || event.trackingKey == "THANKS_SHOWN") {
                    dismiss()
                }
            }
        })
    }

    companion object {

        fun newInstance(): ReviewBottomSheetDialogFragment {
            val args = Bundle()

            val fragment = ReviewBottomSheetDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
