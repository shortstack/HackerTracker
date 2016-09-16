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
 * Created by Whitney Champion on 7/7/14.
 */
public class PartnersFragment extends Fragment {

    private static View rootView;
    private Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PartnersFragment newInstance(int sectionNumber) {
        PartnersFragment fragment = new PartnersFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PartnersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_partners, container, false);
        } catch (InflateException e) {
        }

        // get context
        context = inflater.getContext();

        return rootView;
    }

}



