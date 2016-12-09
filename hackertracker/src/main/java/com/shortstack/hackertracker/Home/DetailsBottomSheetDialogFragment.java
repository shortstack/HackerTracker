package com.shortstack.hackertracker.Home;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.ItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailsBottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment {


    public static final String ARG_OBJ = "VENDOR";


    @Bind(R.id.item)
    ItemView item;



    // Description

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.empty)
    View empty;

    @Bind(R.id.link)
    View link;


    public static DetailsBottomSheetDialogFragment newInstance(Default obj ) {
        DetailsBottomSheetDialogFragment fragment = new DetailsBottomSheetDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_OBJ, obj);
        fragment.setArguments(bundle);

        return fragment;
    }



    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.activity_details_tab, null);
        dialog.setContentView(view);
        ButterKnife.bind(this, view);

        Default obj = (Default) getArguments().getSerializable(ARG_OBJ);

        if( obj == null ) {
            Logger.e("Vendor is null. Can not render bottom sheet.");
            return;
        }


        item.setItem(obj);

        displayDescription(obj);
    }

    private void displayDescription(Default obj) {
        boolean hasDescription = obj.hasDescription();

        if (hasDescription)
            description.setText(obj.getDescription());
        empty.setVisibility(hasDescription ? View.GONE : View.VISIBLE);

        link.setVisibility( obj.hasUrl() ? View.VISIBLE : View.GONE );
    }
}
