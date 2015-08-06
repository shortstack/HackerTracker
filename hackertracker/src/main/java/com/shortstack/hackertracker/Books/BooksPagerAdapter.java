package com.shortstack.hackertracker.Books;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;

public class BooksPagerAdapter extends FragmentPagerAdapter {
  private Context ctxt=null;

  public BooksPagerAdapter(Context ctxt, FragmentManager mgr) {
    super(mgr);
    this.ctxt=ctxt;
  }

  @Override
  public int getCount() {
    return(5);
  }

  @Override
  public Fragment getItem(int position) {
    return(BooksFragment.newInstance(3, position - 1));
  }

  @Override
  public String getPageTitle(int position) {
    return(HackerTrackerFragment.getTitle(ctxt, position-1));
  }
}