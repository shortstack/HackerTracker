package com.shortstack.hackertracker.Contests;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.R;

public class ContestPagerFragment extends Fragment {

    static ViewPager pager;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static ContestPagerFragment newInstance(int position) {
        ContestPagerFragment frag=new ContestPagerFragment();
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

        pager=(ViewPager)result.findViewById(R.id.pager);
        pager.setAdapter(buildAdapter());
        pager.setOffscreenPageLimit(4);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) result.findViewById(R.id.pager_title_strip);
        pagerTabStrip.setDrawFullUnderline(false);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.black));

        return(result);
    }

    private PagerAdapter buildAdapter() {
     return(new ContestPagerAdapter(getActivity(), getChildFragmentManager()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}