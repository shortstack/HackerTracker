package com.shortstack.hackertracker.Schedule;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;

public class SchedulePagerAdapter extends FragmentStatePagerAdapter {
    private Context ctxt=null;

    public SchedulePagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);
    this.ctxt=ctxt;
    }

    @Override
    public int getCount() {
    return(5);
    }

    @Override
    public Fragment getItem(int position) {
      Fragment mFragment = ScheduleFragment.newInstance(8, position-1);
      return(mFragment);
    }

    @Override
    public String getPageTitle(int position) {
    return(HackerTrackerFragment.getTitle(ctxt, position-1));
    }

}