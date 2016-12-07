package com.shortstack.hackertracker.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainDetailsFragment extends HackerTrackerFragment {

    private Default mItem;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.location)
    TextView location;

    @Bind(R.id.category_text)
    TextView categoryText;

    @Bind(R.id.demo)
    View demo;

    @Bind(R.id.exploit)
    View exploit;

    @Bind(R.id.tool)
    View tool;

    @Bind(R.id.container)
    View container;

    @Bind(R.id.category)
    View category;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.details_main, container, false);
        ButterKnife.bind(this, view);

        render();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItem = (Default) getArguments().getSerializable("item");
    }

    public static Fragment newInstance(Default item) {
        MainDetailsFragment fragment = new MainDetailsFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        fragment.setArguments(bundle);

        return fragment;
    }

    public void render() {
        displayText();
        displaySpeakerIcons();
        displayCategory();

        updateBookmark();
    }

    public Default getContent() {
        return mItem;
    }

    private void displayCategory() {
        int count = getContent().getCategoryColorPosition();

        String[] allColors = getContext().getResources().getStringArray(R.array.colors);
        String[] allLabels = getContext().getResources().getStringArray(R.array.filter_types);

        int position = count % allColors.length;

        int color = Color.parseColor(allColors[position]);
        category.setBackgroundColor(color);

        categoryText.setText(allLabels[position]);
    }

    private boolean isOnSchedule() {
        return getContent().isBookmarked();
    }

    public void updateBookmark() {
        //bookmark.setVisibility( isOnSchedule() ? View.VISIBLE : View.GONE );
    }

    private void displayText() {
        title.setText(getContent().getDisplayTitle());
        name.setText(getContent().getName());
        time.setText(getContent().getFullTimeStamp(getContext()));
        location.setText(getContent().getLocation());
    }

    private void displaySpeakerIcons() {
        tool.setVisibility(getContent().isTool() ? View.VISIBLE : View.GONE);
        exploit.setVisibility(getContent().isExploit() ? View.VISIBLE : View.GONE);
        demo.setVisibility(getContent().isDemo() ? View.VISIBLE : View.GONE);
    }
}
