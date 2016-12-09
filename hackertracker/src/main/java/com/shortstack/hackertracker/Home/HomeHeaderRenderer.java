package com.shortstack.hackertracker.Home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Activity.MapsActivity;
import com.shortstack.hackertracker.Activity.SettingsActivity;
import com.shortstack.hackertracker.Activity.VendorsActivity;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Fragment.FAQActivity;
import com.shortstack.hackertracker.Fragment.InformationFragment;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.UberView;

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
        MaterialAlert.create(getContext()).setView(view).show();
    }

    private void showTextAlert( int title, int message ) {
        MaterialAlert.create(getContext()).setTitle(title).setMessage(message).show();
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
        //startInformationActivity(R.layout.fragment_maps);
        Intent intent = new Intent(getContext(), MapsActivity.class);
        //intent.putExtra("res", res);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.vendors)
    public void onVendorsClick() {
        //showInformationAlert(R.layout.fragment_vendors);
        Intent intent = new Intent(getContext(), VendorsActivity.class);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.faqs)
    public void onFAQClick() {
        Intent intent = new Intent(getContext(), FAQActivity.class);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.workshop)
    public void onWorkshopClick() {
        showTextAlert(R.string.workshop_info_title, R.string.workshop_info_text);
    }

    @OnClick(R.id.radio)
    public void onRadioClick() {
        showTextAlert(R.string.radio_title, R.string.radio_text);
    }

    @OnClick(R.id.settings)
    public void onSettingsClick() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        getContext().startActivity(intent);
    }

    @OnClick(R.id.uber)
    public void onUberClick() {
        MaterialAlert.create(getContext()).setTitle(R.string.uber).setView( new UberView(getContext())).show();
    }
}
