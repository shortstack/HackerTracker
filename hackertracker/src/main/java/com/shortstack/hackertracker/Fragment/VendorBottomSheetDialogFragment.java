package com.shortstack.hackertracker.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VendorBottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment {

    public static final String ARG_VENDOR = "VENDOR";

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.empty)
    View empty;

    @Bind(R.id.link)
    View link;


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

        Vendor vendor = getContent();

        if( vendor == null ) {
            Logger.e("Vendor is null. Can not render bottom sheet.");
            return;
        }

        title.setText(vendor.getTitle());

        boolean isDescriptionEmpty = TextUtils.isEmpty(vendor.getDescription());
        empty.setVisibility( isDescriptionEmpty ? View.VISIBLE : View.GONE );
        description.setText(vendor.getDescription());

        link.setVisibility( vendor.hasLink() ? View.VISIBLE : View.GONE );
    }

    private Vendor getContent() {
        return (Vendor) getArguments().getSerializable(ARG_VENDOR);
    }

    @OnClick(R.id.link)
    public void onLinkClick() {
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
