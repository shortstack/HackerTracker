package com.shortstack.hackertracker.BottomSheet;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.prompt.interfaces.IPromptPresenter;
import com.github.stkent.amplify.tracking.interfaces.IEvent;
import com.github.stkent.amplify.tracking.interfaces.IEventListener;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @BindView(R.id.prompt_view)
    DefaultLayoutPromptView promptView;

    public static ReviewBottomSheetDialogFragment newInstance() {
        Bundle args = new Bundle();

        ReviewBottomSheetDialogFragment fragment = new ReviewBottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_review, null);
        dialog.setContentView(view);
        ButterKnife.bind(this, view);
        promptView.getPresenter().start();
        promptView.getPresenter().addPromptEventListener(new IPromptPresenter() {
            @Override
            public void addPromptEventListener(@NonNull IEventListener promptEventListener) {

            }

            @Override
            public void start() {

            }

            @Override
            public void reportUserOpinion(@NonNull UserOpinion userOpinion) {
            }

            @Override
            public void reportUserFeedbackAction(@NonNull UserFeedbackAction userFeedbackAction) {
            }

            @NonNull
            @Override
            public Bundle generateStateBundle() {
                return null;
            }

            @Override
            public void restoreStateFromBundle(@NonNull Bundle bundle) {
            }

            @Override
            public void notifyEventTriggered(@NonNull IEvent event) {
                Logger.d("Review event triggered: " + event.getTrackingKey());
                if (event.getTrackingKey().equals("PROMPT_DISMISSED") || event.getTrackingKey().equals("THANKS_SHOWN")) {
                    dismiss();
                }
            }
        });
    }
}
