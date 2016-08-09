package com.shortstack.hackertracker.Kids;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.R;

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

        // get current date, scroll to that page
        HomeActivity.setDay(pager);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) result.findViewById(R.id.pager_title_strip);
        pagerTabStrip.setDrawFullUnderline(false);
        pagerTabStrip.setTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        return(result);
    }

    private PagerAdapter buildAdapter() {
        return(new KidsPagerAdapter(getActivity(), getChildFragmentManager()));
    }

}