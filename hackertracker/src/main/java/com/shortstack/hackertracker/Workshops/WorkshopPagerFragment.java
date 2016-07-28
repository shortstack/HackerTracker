package com.shortstack.hackertracker.Workshops;

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
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Font.HelveticaButton;
import com.shortstack.hackertracker.Fragment.BadgeFragment;
import com.shortstack.hackertracker.Fragment.WorkshopInfoFragment;
import com.shortstack.hackertracker.R;

public class WorkshopPagerFragment extends Fragment {

    static ViewPager pager;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static WorkshopPagerFragment newInstance(int position) {
        WorkshopPagerFragment frag = new WorkshopPagerFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, position);
        frag.setArguments(args);

        return (frag);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.pager_workshops, container, false);

        pager = (ViewPager) result.findViewById(R.id.pager);
        pager.setAdapter(buildAdapter());
        pager.setOffscreenPageLimit(5);

        // get current date, scroll to that page
        HomeActivity.setDay(pager);

        // add onclick action to workshop info button
        HelveticaButton workshopInfoButton = (HelveticaButton) result.findViewById(R.id.workshop_info);
        workshopInfoButton.setOnClickListener(new InfoButtonOnClickListener());

        PagerTabStrip pagerTabStrip = (PagerTabStrip) result.findViewById(R.id.pager_title_strip);
        pagerTabStrip.setDrawFullUnderline(false);
        pagerTabStrip.setTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        pagerTabStrip.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));

        return (result);
    }

    private PagerAdapter buildAdapter() {
        return (new WorkshopPagerAdapter(getActivity(), getChildFragmentManager()));
    }

    private class InfoButtonOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, WorkshopInfoFragment.newInstance(16))
                    .addToBackStack(Constants.FRAGMENT_WORKSHOP_INFO)
                    .commit();
            HomeActivity.setActionBarTitle(getString(R.string.workshop_info_title));

        }

    }

}