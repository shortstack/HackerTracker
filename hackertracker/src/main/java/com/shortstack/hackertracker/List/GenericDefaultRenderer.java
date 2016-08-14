package com.shortstack.hackertracker.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
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

    @Bind(R.id.container)
    View container;

    @Bind(R.id.category)
    View category;

    @Bind(R.id.bookmark)
    View bookmark;

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
        int count = getContent().getCategoryColorPosition();
        String[] allColors = getContext().getResources().getStringArray(R.array.colors);

        int color = Color.parseColor(allColors[count % allColors.length]);
        category.setBackgroundColor(color);
    }

    private void displayNewIcon() {
        //isNew.setVisibility(getContent().isNew() ? View.VISIBLE : View.GONE);
    }

    private boolean isOnSchedule() {
        return getContent().isStarred();
    }

    private void highlightText() {
        bookmark.setVisibility( isOnSchedule() ? View.VISIBLE : View.GONE );
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
        Activity context = (Activity) getContext();
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("item", Parcels.wrap(getContent()));

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(context, container, "category");

        context.startActivity(intent, options.toBundle());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getWindow().setExitTransition(null);
        }
    }
}
