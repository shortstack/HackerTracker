package com.shortstack.hackertracker.Parties;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PartyPagerAdapter extends FragmentPagerAdapter {
  private Context ctxt=null;

  public PartyPagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);
    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(5);
  }

  @Override
  public Fragment getItem(int position) {
    return(PartyFragment.newInstance(5, position-1));
  }

  @Override
  public String getPageTitle(int position) {
    return(PartyFragment.getTitle(ctxt, position-1));
  }
}