package com.shortstack.hackertracker.Speakers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;

public class SpeakerPagerAdapter extends FragmentPagerAdapter {
  private Context ctxt=null;

  public SpeakerPagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);
    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(4);
  }

  @Override
  public Fragment getItem(int position) {
    return(SpeakerFragment.newInstance(2,position));
  }

  @Override
  public String getPageTitle(int position) {
    return(HackerTrackerFragment.getTitle(ctxt, position));
  }

}