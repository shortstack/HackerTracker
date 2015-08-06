package com.shortstack.hackertracker.Speakers;

import android.content.Context;
import android.os.Bundle;
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

public class SpeakerFragment extends HackerTrackerFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_DATE = "date";
    private View rootView;
    private DefaultAdapter adapter;
    private ListView list;
    private static int date;
    private Context context;
    private int mPage;
    private int mDate;

    public static SpeakerFragment newInstance(int position,int date) {
        SpeakerFragment frag=new SpeakerFragment();
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
          rootView = inflater.inflate(R.layout.fragment_speakers, container, false);
        } catch (InflateException e) {
        }

        date = mDate+1;

        list = (ListView) rootView.findViewById(R.id.list_speakers);

        // get speakers from database
        List<Default> speakers = getItemByDate(HackerTrackerFragment.getDate(date), Constants.TYPE_SPEAKER);
        if (speakers.size() > 0) {

            adapter = new DefaultAdapter(context, R.layout.row, speakers);

            list.setAdapter(adapter);

        }

        return rootView;
    }

}