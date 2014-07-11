package com.shortstack.hackertracker.Contests;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shortstack.hackertracker.Misc.HackerTrackerFragment;

public class ContestPagerAdapter extends FragmentPagerAdapter {
  private Context ctxt=null;

  public ContestPagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);
    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(5);
  }

  @Override
  public Fragment getItem(int position) {
    return(ContestFragment.newInstance(3,position-1));
  }

  @Override
  public String getPageTitle(int position) {
    return(HackerTrackerFragment.getTitle(ctxt, position-1));
  }
}