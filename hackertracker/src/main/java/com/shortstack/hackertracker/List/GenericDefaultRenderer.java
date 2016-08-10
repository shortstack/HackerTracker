package com.shortstack.hackertracker.List;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Activity.DetailsActivity;
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

    @Bind(R.id.where)
    TextView where;

    @Bind(R.id.demo)
    View demo;

    @Bind(R.id.exploit)
    View exploit;

    @Bind(R.id.tool)
    View tool;

    @Bind(R.id.isNew)
    View isNew;


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

        highlightText();
    }

    private void displayNewIcon() {
        isNew.setVisibility(getContent().isNew() ? View.VISIBLE : View.GONE);
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
        title.setText(getContent().getTitle());
        name.setText(getContent().getName());
        time.setText(getContent().getTimeStamp(getContext()));
        where.setText(getContent().getLocation());
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
