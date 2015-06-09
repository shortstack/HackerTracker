package com.shortstack.hackertracker.Kids;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class KidsPagerFragment extends Fragment {

    static ViewPager pager;
    private Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static KidsPagerFragment newInstance(int position) {
        KidsPagerFragment frag=new KidsPagerFragment();
        Bundle args=new Bundle();

        args.putInt(ARG_SECTION_NUMBER, position);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.pager, container, false);

        // get context
        context = inflater.getContext();

        pager = (ViewPager) result.findViewById(R.id.pager);
        pager.setAdapter(buildAdapter());
        pager.setOffscreenPageLimit(5);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) result.findViewById(R.id.pager_title_strip);
        pagerTabStrip.setDrawFullUnderline(false);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.black));

        return(result);
    }

    private PagerAdapter buildAdapter() {
        return(new KidsPagerAdapter(getActivity(), getChildFragmentManager()));
    }

}