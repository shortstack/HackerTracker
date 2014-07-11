package com.shortstack.hackertracker.Schedule;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Contests.ContestPagerFragment;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;

public class SchedulePagerFragment extends Fragment {

    static ViewPager pager;
    private static Context context;
    private static Activity activity;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static SchedulePagerFragment newInstance(int position) {
        SchedulePagerFragment frag=new SchedulePagerFragment();
        Bundle args=new Bundle();

        args.putInt(ARG_SECTION_NUMBER, position);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.pager, container, false);

        context = inflater.getContext();

        activity = getActivity();

        // if there are no starred items, show dialog
        if (getStars()<1 && !SharedPreferencesUtil.showSuggestions()) {
            DialogUtil.emptyScheduleDialog(context).show();
        }

        pager = (ViewPager) result.findViewById(R.id.pager);
        pager.setAdapter(buildAdapter());
        pager.setOffscreenPageLimit(5);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) result.findViewById(R.id.pager_title_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.green));
        pagerTabStrip.setDrawFullUnderline(false);

        return(result);
    }

    private PagerAdapter buildAdapter() {
        return(new SchedulePagerAdapter(getActivity(), getChildFragmentManager()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_clear) {
            DialogUtil.clearScheduleDialog(context).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void clearSchedule() {
        SQLiteDatabase dbStars = HackerTrackerApplication.myDbHelperStars.getWritableDatabase();
        SQLiteDatabase db = HackerTrackerApplication.myDbHelper.getWritableDatabase();

        // delete all data in starred database
        dbStars.execSQL("DELETE FROM data");

        // update all data in main database to not be starred
        db.execSQL("UPDATE data SET starred=0");

        // reload screen
        HomeActivity.refreshSchedule();

        Toast.makeText(context, R.string.schedule_cleared, Toast.LENGTH_SHORT).show();
        db.close();
        dbStars.close();
    }

    public int getStars() {
        SQLiteDatabase db = HackerTrackerApplication.myDbHelperStars.getWritableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM data", null);
        int count = myCursor.getCount();

        db.close();

        return count;
    }

}