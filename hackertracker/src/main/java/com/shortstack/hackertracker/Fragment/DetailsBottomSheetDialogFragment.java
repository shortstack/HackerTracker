package com.shortstack.hackertracker.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Analytics.AnalyticsController;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.ItemView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsBottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment {


    public static final String ARG_OBJ = "VENDOR";


    @BindView(R.id.item)
    ItemView item;


    // Description

    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.empty)
    View empty;

    @BindView(R.id.link)
    View link;

    @BindView(R.id.star)
    ImageView star;

    @BindView(R.id.share)
    View share;


    public static DetailsBottomSheetDialogFragment newInstance(Item obj) {
        DetailsBottomSheetDialogFragment fragment = new DetailsBottomSheetDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_OBJ, obj);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_details, null);
        dialog.setContentView(view);
        ButterKnife.bind(this, view);

        Item obj = getContent();

        if (obj == null) {
            Logger.e("Company is null. Can not render bottom sheet.");
            return;
        }

        App.getApplication().getAnalyticsController().tagItemEvent(AnalyticsController.Analytics.EVENT_VIEW, getContent());

        item.setItem(obj);

        displayDescription(obj);
    }

    private Item getContent() {
        return (Item) getArguments().getSerializable(ARG_OBJ);
    }

    private void displayDescription(Item obj) {
        boolean hasDescription = obj.hasDescription();

        if (hasDescription)
            description.setText(obj.getDescription());
        empty.setVisibility(hasDescription ? View.GONE : View.VISIBLE);

        link.setVisibility(obj.hasUrl() ? View.VISIBLE : View.GONE);

        updateStarIcon();
    }

    private void updateStarIcon() {
        star.setImageDrawable(getResources().getDrawable(getContent().isBookmarked() ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_border_white_24dp));
    }

    @OnClick(R.id.star)
    public void onStarClick() {
        if( getContent().isBookmarked() ) {
            App.getApplication().getAnalyticsController().tagItemEvent(AnalyticsController.Analytics.EVENT_UNBOOKMARK, getContent());
        } else {
            App.getApplication().getAnalyticsController().tagItemEvent(AnalyticsController.Analytics.EVENT_BOOKMARK, getContent());
        }
        item.onBookmarkClick();
        updateStarIcon();
    }

    @OnClick(R.id.share)
    public void onShareClick() {
        App.getApplication().getAnalyticsController().tagItemEvent(AnalyticsController.Analytics.EVENT_SHARE, getContent());
        item.onShareClick();
    }

    @OnClick(R.id.link)
    public void onLinkClick() {
        App.getApplication().getAnalyticsController().tagItemEvent(AnalyticsController.Analytics.EVENT_LINK, getContent());

        MaterialAlert.create(getContext())
                .setTitle(R.string.link_warning)
                .setMessage(String.format(getContext().getString(R.string.link_message), getContent().getLink().toLowerCase()))
                .setPositiveButton(R.string.open_link, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getContent().getLink()));
                        getContext().startActivity(intent);
                    }
                }).setBasicNegativeButton()
                .show();
    }
}
