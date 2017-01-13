package com.shortstack.hackertracker.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Analytics.AnalyticsController;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.UberView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends Fragment {

    public static final String ASSET_NAME = "map_defcon_small.pdf";

    @Bind(R.id.viewer)
    PDFView pdfViewer;

    @Bind(R.id.progress_container)
    View progress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_maps, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pdfViewer.fromAsset(ASSET_NAME).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                progress.setVisibility(View.GONE);
            }
        }).load();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_uber:
                App.getApplication().getAnalyticsController().tagCustomEvent(AnalyticsController.Analytics.UBER);
                MaterialAlert.create(getContext()).setTitle(R.string.uber).setView( new UberView(getContext())).show();
                return true;
        }

        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.maps, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.d("onDestroy maps");
        if( pdfViewer != null ) {
            Logger.d("Recycling pdf.");
            pdfViewer.recycle();
        }
    }

    public static Fragment newInstance() {
        return new MapsActivity();
    }
}
