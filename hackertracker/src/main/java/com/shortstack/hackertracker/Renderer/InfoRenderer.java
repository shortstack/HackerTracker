package com.shortstack.hackertracker.Renderer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InfoRenderer extends Renderer<String> {

    @Bind(R.id.header)
    TextView header;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_info, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        header.setText(getContent());
    }
}
