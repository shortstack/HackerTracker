package com.shortstack.hackertracker.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.R;

/**
 * Created by Whitney Champion on 8/3/16.
 */
public class RadioFragment extends Fragment {

    private static View rootView;
    private Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static RadioFragment newInstance(int sectionNumber) {
        RadioFragment fragment = new RadioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public RadioFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_radio, container, false);
        } catch (InflateException e) {
        }

        // get context
        context = inflater.getContext();

        return rootView;
    }

}



