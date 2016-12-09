package com.shortstack.hackertracker.Home;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VendorBottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment {

    public static final String ARG_VENDOR = "VENDOR";

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.empty)
    View empty;


    public static VendorBottomSheetDialogFragment newInstance( Vendor vendor ) {
        VendorBottomSheetDialogFragment fragment = new VendorBottomSheetDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_VENDOR, vendor);
        fragment.setArguments(bundle);

        return fragment;
    }



    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_vendor, null);
        dialog.setContentView(view);
        ButterKnife.bind(this, view);

        Vendor vendor = (Vendor) getArguments().getSerializable(ARG_VENDOR);

        if( vendor == null ) {
            Logger.e("Vendor is null. Can not render bottom sheet.");
            return;
        }

        title.setText(vendor.getTitle());

        boolean isDescriptionEmpty = TextUtils.isEmpty(vendor.getDescription());
        empty.setVisibility( isDescriptionEmpty ? View.VISIBLE : View.GONE );
        description.setText(vendor.getDescription());
    }

}
