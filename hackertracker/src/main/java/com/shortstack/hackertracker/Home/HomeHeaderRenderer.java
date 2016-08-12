package com.shortstack.hackertracker.Home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Fragment.InformationFragment;
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

    private void showInformationAlert(int res) {
        View view = LayoutInflater.from(getContext()).inflate(res, null);
        MaterialAlert.create(getContext()).setView(view).setBasicPositiveButton().show();
    }

    private void startInformationActivity( int res ) {
        Intent intent = new Intent(getContext(), InformationFragment.class);
        intent.putExtra("res", res);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.badge)
    public void onBadgeClick() {
        showInformationAlert(R.layout.fragment_badges);
    }

    @OnClick(R.id.wifi)
    public void onWifiClick() {
        showInformationAlert(R.layout.fragment_wifi);
    }

    @OnClick(R.id.map)
    public void onMapClick() {
        startInformationActivity(R.layout.fragment_maps);
    }

    @OnClick(R.id.vendors)
    public void onVendorsClick() {
        showInformationAlert(R.layout.fragment_vendors);
    }

    @OnClick(R.id.faqs)
    public void onFAQClick() {
        showInformationAlert(R.layout.fragment_faq);
    }

    @OnClick(R.id.workshop)
    public void onWorkshopClick() {
        showInformationAlert(R.layout.fragment_workshop_info);
    }

    @OnClick(R.id.radio)
    public void onRadioClick() {
        showInformationAlert(R.layout.fragment_radio);
    }

    // TODO: Bind all the onClick listeners.
}
