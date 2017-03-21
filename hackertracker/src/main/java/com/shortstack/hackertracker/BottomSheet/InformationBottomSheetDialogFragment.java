package com.shortstack.hackertracker.BottomSheet;

import android.os.Bundle;

import com.shortstack.hackertracker.Model.Information;

public class InformationBottomSheetDialogFragment extends GenericBottomSheetDialogFragment {

    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_DESC = "DESCRIPTION";

    public static InformationBottomSheetDialogFragment newInstance(String title, String description ) {
        InformationBottomSheetDialogFragment fragment = new InformationBottomSheetDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        bundle.putString(ARG_DESC, description);

        fragment.setArguments(bundle);

        return fragment;
    }

    public static InformationBottomSheetDialogFragment newInstance(Information content) {
        return newInstance(content.getTitle(), content.getDescription());
    }


    @Override
    protected String getLink() {
        return null;
    }

    @Override
    protected String getTitle() {
        return getArguments().getString(ARG_TITLE);
    }

    @Override
    protected String getDescription() {
        return getArguments().getString(ARG_DESC);
    }

    @Override
    protected boolean hasLink() {
        return false;
    }


}
