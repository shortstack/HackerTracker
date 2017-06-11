package com.shortstack.hackertracker.Renderer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Activity.TabHomeActivity;
import com.shortstack.hackertracker.Model.Navigation;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityNavRenderer extends Renderer<Navigation> implements View.OnClickListener {

    @BindView(R.id.header)
    TextView header;

    @BindView(R.id.description)
    TextView description;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_nav, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void hookListeners(View rootView) {
        rootView.setOnClickListener(this);
    }

    @Override
    public void render(List<Object> payloads) {
        header.setText(getContent().getTitle());
        description.setText(getContent().getDescription());
    }


    @Override
    public void onClick(View view) {
        // TODO Use the class to handle multiple location.
        ((TabHomeActivity)getContext()).loadFragment(TabHomeActivity.Companion.getNAV_INFORMATION());
    }
}
