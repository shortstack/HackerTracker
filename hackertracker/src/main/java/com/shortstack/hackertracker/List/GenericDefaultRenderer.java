package com.shortstack.hackertracker.List;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Activity.DetailsActivity;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GenericDefaultRenderer extends Renderer<Default> implements View.OnClickListener {

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.name)
    TextView name;

    @Bind(R.id.location)
    TextView location;

    @Bind(R.id.demo)
    View demo;

    @Bind(R.id.exploit)
    View exploit;

    @Bind(R.id.tool)
    View tool;

    //@Bind(R.id.isNew)
    //View isNew;


    //@Bind(R.id.category)
    //View category;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void hookListeners(View rootView) {
        rootView.setOnClickListener(this);
    }

    @Override
    public void render(List<Object> payloads) {
        displayText();
        displaySpeakerIcons();
        displayNewIcon();
        displayCategory();

        highlightText();
    }

    private void displayCategory() {
        String type = getContent().getType();
        int count = 0;
        switch (type){
            case Constants.TYPE_EVENT:
                count++;
            case Constants.TYPE_CONTEST:
                count++;
            case Constants.TYPE_SPEAKER:
                count++;
            case Constants.TYPE_KIDS:
                count++;
            case Constants.TYPE_PARTY:
                count++;
            case Constants.TYPE_SKYTALKS:
                count++;
            case Constants.TYPE_DEMOLAB:
                count++;
            case Constants.TYPE_WORKSHOP:
                count++;
        }


        String[] allColors = getContext().getResources().getStringArray(R.array.colors);

        ((FrameLayout)getRootView()).getChildAt(0).setBackgroundColor(Color.parseColor(allColors[count % allColors.length]));

        //category.setBackgroundColor(getContext().getResources().getColor(R.color.star));
    }

    private void displayNewIcon() {
        //isNew.setVisibility(getContent().isNew() ? View.VISIBLE : View.GONE);
    }

    private boolean isOnSchedule() {
        return getContent().isStarred();
    }

    private void highlightText() {
        int color = ContextCompat.getColor(getContext(), isOnSchedule() ? R.color.colorAccent : R.color.white);

        title.setTextColor(color);
        time.setTextColor(color);
    }

    private void displayText() {
        title.setText(getContent().getDisplayTitle());
        name.setText(getContent().getName());
        time.setText(getContent().getTimeStamp(getContext()));
        location.setText(getContent().getLocation());
    }

    private void displaySpeakerIcons() {
        tool.setVisibility(getContent().isTool() ? View.VISIBLE : View.GONE);
        exploit.setVisibility(getContent().isExploit() ? View.VISIBLE : View.GONE);
        demo.setVisibility(getContent().isDemo() ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("item", Parcels.wrap(getContent()));
        getContext().startActivity(intent);
    }
}
