package com.shortstack.hackertracker.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.shortstack.hackertracker.R;

import butterknife.ButterKnife;

public class InformationFragment extends AppCompatActivity {

    private static final String ARG_LAYOUT_RES = "layout_res";
    public static final int DEFAULT_LAYOUT = R.layout.fragment_maps;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int res = DEFAULT_LAYOUT;
        if( getIntent().getExtras() != null ) {
            res = getIntent().getExtras().getInt(ARG_LAYOUT_RES, DEFAULT_LAYOUT);
        }

        setContentView(res);
        ButterKnife.bind(this);
    }
}
