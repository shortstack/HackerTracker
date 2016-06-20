package com.shortstack.hackertracker.Villages;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shortstack.hackertracker.Adapter.DefaultAdapter;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import java.util.List;

public class VillageFragment extends HackerTrackerFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_DATE = "date";
    private View rootView;
    private DefaultAdapter adapter;
    private ListView list;
    private static int date;
    private Context context;
    private int mPage;
    private int mDate;

    public static VillageFragment newInstance(int position, int date) {
        VillageFragment frag=new VillageFragment();
        Bundle args=new Bundle();

        args.putInt(ARG_SECTION_NUMBER, position);
        args.putInt(ARG_DATE, date);
        frag.setArguments(args);

        return(frag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mPage = args.getInt(ARG_SECTION_NUMBER);
        mDate = args.getInt(ARG_DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {

        context = inflater.getContext();

        if (rootView != null) {
          ViewGroup parent = (ViewGroup) rootView.getParent();
          if (parent != null)
              parent.removeView(rootView);
        }
        try {
          rootView = inflater.inflate(R.layout.fragment_villages, container, false);
        } catch (InflateException e) {
            Log.e(context.getClass().getName(), e.getMessage());
        }

        date = mDate+1;

        list = (ListView) rootView.findViewById(R.id.list_villages);

        // get villages
        List<Default> villages = getItemByDate(HackerTrackerFragment.getDate(date), Constants.TYPE_VILLAGE);
        if (villages.size() > 0) {

            adapter = new DefaultAdapter(context, R.layout.row, villages);

            list.setAdapter(adapter);

        }

        return rootView;
    }

}