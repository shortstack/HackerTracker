package com.shortstack.hackertracker.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shortstack.hackertracker.R;

public class InformationFragment extends Fragment {

    private static final String ARG_STRING_RES = "string_res";

    public static InformationFragment newInstance(int string) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STRING_RES, string);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView textView = (TextView) inflater.inflate(R.layout.fragment_information, container, false);
        textView.setText(getString(getArguments().getInt(ARG_STRING_RES)));
        return textView;
    }
}
