package com.shortstack.hackertracker.Workshops;

import android.os.Bundle;

import com.shortstack.hackertracker.List.GenericRowFragment;
import com.shortstack.hackertracker.R;

import butterknife.OnClick;

public class GenericWorkshopFragment extends GenericRowFragment {

    public static GenericWorkshopFragment newInstance(String type) {
        GenericWorkshopFragment frag = new GenericWorkshopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        frag.setArguments(args);

        return (frag);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_list_workship;
    }

    @OnClick(R.id.workshop_info)
    public void onInformationClick() {
        // open information
    }
}
