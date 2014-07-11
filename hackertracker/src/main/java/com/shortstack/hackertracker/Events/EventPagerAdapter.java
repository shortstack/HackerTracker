package com.shortstack.hackertracker.Events;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class EventPagerAdapter extends FragmentPagerAdapter {
  private Context ctxt=null;

  public EventPagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);
    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(5);
  }

  @Override
  public Fragment getItem(int position) {
    return(EventFragment.newInstance(4, position-1));
  }

  @Override
  public String getPageTitle(int position) {
    return(EventFragment.getTitle(ctxt, position-1));
  }
}