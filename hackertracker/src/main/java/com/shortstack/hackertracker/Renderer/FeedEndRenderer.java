package com.shortstack.hackertracker.Renderer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererContent;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedEndRenderer extends Renderer<RendererContent<String>> {
    @BindView(R.id.row_text)
    TextView view;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_feed_end, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        view.setText(getContent().getItem());
    }
}
