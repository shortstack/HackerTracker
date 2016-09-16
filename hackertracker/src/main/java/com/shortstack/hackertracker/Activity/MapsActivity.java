package com.shortstack.hackertracker.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.shortstack.hackertracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity {

    public static final String ASSET_NAME = "map_defcon.pdf";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.viewer)
    PDFView pdfViewer;

    @Bind(R.id.progress_container)
    View progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        pdfViewer.fromAsset(ASSET_NAME).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
                progress.setVisibility(View.GONE);
            }
        }).load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( pdfViewer != null )
            pdfViewer.recycle();
    }
}
