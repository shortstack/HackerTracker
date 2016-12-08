package com.shortstack.hackertracker.Home;

import android.animation.LayoutTransition;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VendorRenderer extends Renderer<Vendor> implements View.OnClickListener {

    public static final int MAX_LINES = 4;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.expand)
    Button expand;

    @Bind(R.id.link)
    Button link;

    @Bind(R.id.empty)
    View empty;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_vendor, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void hookListeners(View rootView) {
        super.hookListeners(rootView);
        rootView.setOnClickListener(this);
    }

    @Override
    public void render(List<Object> payloads) {
        title.setText(getContent().getTitle());
        String text = getContent().getDescription();
        description.setText(text);

//        if (TextUtils.isEmpty(text)) {
//            empty.setVisibility(View.VISIBLE);
//            description.setText("");
//            expand.setVisibility(View.GONE);
//
////        } else if (text.length() < 340) {
////            empty.setVisibility(View.GONE);
////            expand.setVisibility(View.GONE);
////            description.setMaxLines(Integer.MAX_VALUE);
//        }
//        else {
//            empty.setVisibility(View.GONE);
//            expand.setText(getContext().getString(R.string.expand));
//            this.description.setMaxLines(MAX_LINES);
//        }

        text = getContent().getLink();
        link.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.expand)
    public void onExpandClick() {

        //LayoutTransition lt = new LayoutTransition();
        ((ViewGroup)getRootView()).setLayoutTransition(new LayoutTransition());

        //lt.disableTransitionType(LayoutTransition.DISAPPEARING);

        if (description.getMaxLines() == MAX_LINES) {
            expand.setText(getContext().getString(R.string.collapse));
            description.setMaxLines(Integer.MAX_VALUE);
        } else {
            expand.setText(getContext().getString(R.string.expand));
            description.setMaxLines(MAX_LINES);
        }




    }

    @OnClick(R.id.link)
    public void onLinkClick() {
        MaterialAlert.create(getContext()).setTitle(R.string.link_warning).setMessage(String.format(getContext().getString(R.string.link_message), getContent().getLink().toLowerCase())).setPositiveButton(R.string.open_link, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(getContent().getLink()));
                getContext().startActivity(intent);
            }
        })
                .setBasicNegativeButton().show();
    }

    @Override
    public void onClick(View view) {
        VendorBottomSheetDialogFragment bottomSheetDialogFragment =  VendorBottomSheetDialogFragment.newInstance(getContent());
        bottomSheetDialogFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }
}
