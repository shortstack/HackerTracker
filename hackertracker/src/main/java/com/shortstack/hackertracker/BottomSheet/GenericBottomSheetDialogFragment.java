package com.shortstack.hackertracker.BottomSheet;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public abstract class GenericBottomSheetDialogFragment extends android.support.design.widget.BottomSheetDialogFragment {

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.empty)
    View empty;

    @BindView(R.id.link)
    View link;



    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), R.layout.bottom_sheet_generic, null);
        dialog.setContentView(view);
        ButterKnife.bind(this, view);

        title.setText(getTitle());

        boolean isDescriptionEmpty = TextUtils.isEmpty(getDescription());
        empty.setVisibility( isDescriptionEmpty ? View.VISIBLE : View.GONE );
        description.setText(getDescription());

        link.setVisibility( hasLink() ? View.VISIBLE : View.GONE );
    }

    protected abstract String getLink();

    protected abstract String getTitle();

    protected abstract String getDescription();

    protected abstract boolean hasLink();

    @OnClick(R.id.link)
    public void onLinkClick() {
        MaterialAlert.create(getContext())
                .setTitle(R.string.link_warning)
                .setMessage(String.format(getContext().getString(R.string.link_message), getLink().toLowerCase()))
                .setPositiveButton(R.string.open_link, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getLink()));
                        getContext().startActivity(intent);
                    }
                }).setBasicNegativeButton()
                .show();
    }
}
