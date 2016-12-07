package com.shortstack.hackertracker.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DescriptionDetailsFragment extends HackerTrackerFragment {


    private Default mItem;

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.empty)
    View empty;

    @Bind(R.id.link_container)
    View linkContainer;

    @Bind(R.id.link)
    TextView link;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_description, container, false);
        ButterKnife.bind(this, view);

        boolean hasDescription = mItem.hasDescription();

        if (hasDescription)
            description.setText(mItem.getDescription());
        empty.setVisibility(hasDescription ? View.GONE : View.VISIBLE);

        boolean hasUrl = mItem.hasUrl();
        if (hasUrl) {
            link.setText(mItem.getPrettyUrl());
        }
        linkContainer.setVisibility(hasUrl ? View.VISIBLE : View.GONE);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItem = (Default) getArguments().getSerializable("item");
    }

    public static Fragment newInstance(Default item) {
        DescriptionDetailsFragment fragment = new DescriptionDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        fragment.setArguments(bundle);

        return fragment;
    }

    @OnClick(R.id.link_container)
    public void onLinkClick() {
        MaterialAlert.create(getContext()).setTitle(R.string.link_warning).setMessage(String.format(getString(R.string.link_message), mItem.getLink().toLowerCase())).setPositiveButton(R.string.open_link, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mItem.getLink()));
                startActivity(intent);
            }
        })
                .setBasicNegativeButton().show();
    }
}
