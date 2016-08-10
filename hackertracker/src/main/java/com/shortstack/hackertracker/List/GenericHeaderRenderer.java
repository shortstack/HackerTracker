package com.shortstack.hackertracker.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.R;

import java.util.List;

public class GenericHeaderRenderer extends Renderer<String> {

    private TextView view;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        view = (TextView) inflater.inflate(R.layout.row_header, parent, false);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        view.setText(getContent());
    }
}
