package com.shortstack.hackertracker.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.TimeView;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GenericTimeRenderer extends Renderer<Date> {

    @Bind(R.id.time_item)
    TimeView item;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_time_container, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        item.setDate(getContent());
    }
}
