package com.shortstack.hackertracker.Schedule;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        pagerTabStrip.setDrawFullUnderline(false);

        return(result);
    }

    private PagerAdapter buildAdapter() {
        return(new SchedulePagerAdapter(getActivity(), getChildFragmentManager()));
    }

    public int getStars() {
        SQLiteDatabase db = HackerTrackerApplication.myDbHelperStars.getReadableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM data", null);
        int count = myCursor.getCount();

        db.close();

        return count;
    }

    public static Boolean backupDatabaseCSV() {
        SQLiteDatabase dbOfficial = HackerTrackerApplication.dbHelper.getReadableDatabase();
        SQLiteDatabase db = HackerTrackerApplication.vendorDbHelper.getReadableDatabase();

        Log.d("CSV", "backupDatabaseCSV");
        Boolean returnCode = false;
        int i = 0;
        String csvHeader = "";
        String csvValues = "";
        for (i = 0; i < Constants.COLUMN_NAMES.length; i++) {
            if (csvHeader.length() > 0) {
                csvHeader += ",";
            }
            csvHeader += "\"" + Constants.COLUMN_NAMES[i] + "\"";
        }

        csvHeader += "\n";
        Log.d("CSV", "header=" + csvHeader);
        try {
            File outFile = DialogUtil.getOutputMediaFile();
            FileWriter fileWriter = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fileWriter);
            Cursor cursorOfficial = dbOfficial.rawQuery("SELECT title,who,begin,end,date,location FROM data WHERE starred=1", null);
            Cursor cursor = db.rawQuery("SELECT title,who,begin,end,date,location FROM data WHERE starred=1", null);
            if (cursorOfficial != null) {
                out.write(csvHeader);
                while (cursorOfficial.moveToNext()) {
                    csvValues = "\""+cursorOfficial.getString(0).replace(",","")+"\",";
                    if (cursorOfficial.getString(1)!=null)
                        csvValues += cursorOfficial.getString(1).replace(",",";")+",";
                    else
                        csvValues += ",";
                    csvValues += cursorOfficial.getString(2)+",";
                    csvValues += cursorOfficial.getString(3)+",";
                    csvValues += cursorOfficial.getString(4)+",";
                    csvValues += cursorOfficial.getString(5)+",\n";
                    out.write(csvValues.replace("null",""));
                }
                cursorOfficial.close();
            }
            if (cursor != null) {
                if (!outFile.exists()) {
                    out.write(csvHeader);
                }
                while (cursor.moveToNext()) {
                    csvValues = "\""+cursor.getString(0).replace(",","")+"\",";
                    if (cursor.getString(1)!=null)
                        csvValues += cursor.getString(1).replace(",",";")+",";
                    else
                        csvValues += ",";
                    csvValues += cursor.getString(2)+",";
                    csvValues += cursor.getString(3)+",";
                    csvValues += cursor.getString(4)+",";
                    csvValues += cursor.getString(5)+",\n";
                    out.write(csvValues.replace("null",""));
                }
                cursor.close();
            }
            out.close();
            returnCode = true;
        } catch (IOException e) {
            returnCode = false;
            Log.d("CSV", "IOException: " + e.getMessage());
        }
        db.close();
        dbOfficial.close();
        return returnCode;
    }

}