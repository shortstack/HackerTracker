package com.shortstack.hackertracker.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeHeaderRenderer extends Renderer<Void> {
    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.fragment_home_header, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        // Do nothing.
    }

    @OnClick(R.id.badge)
    public void onBadgeClick() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_badges, null);
        MaterialAlert.create(getContext()).setView(view).setBasicPositiveButton().show();
    }

    // TODO: Bind all the onClick listeners.
}
