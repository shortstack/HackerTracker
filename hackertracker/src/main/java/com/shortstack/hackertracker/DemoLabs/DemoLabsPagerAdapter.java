package com.shortstack.hackertracker.DemoLabs;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;

public class DemoLabsPagerAdapter extends FragmentPagerAdapter {
  private Context ctxt=null;

  public DemoLabsPagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);
    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(1);
  }

  @Override
  public Fragment getItem(int position) {
    return(DemoLabsFragment.newInstance(3, position + 2));
  }

  @Override
  public String getPageTitle(int position) {
    return(HackerTrackerFragment.getTitle(ctxt, position + 2));
  }
}