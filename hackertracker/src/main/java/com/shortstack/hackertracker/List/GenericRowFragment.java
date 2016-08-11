package com.shortstack.hackertracker.List;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GenericRowFragment extends HackerTrackerFragment {

    protected static final String ARG_TYPE = "type";

    @Bind(R.id.list)
    protected
    RecyclerView list;

    private String[] mTypes;


    public static GenericRowFragment newInstance(String... type) {
        GenericRowFragment frag = new GenericRowFragment();
        Bundle args = new Bundle();
        args.putStringArray(ARG_TYPE, type);
        frag.setArguments(args);

        return (frag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) mTypes = args.getStringArray(ARG_TYPE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getContentView(), container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        list.setLayoutManager(layout);

        List<Default> events = getEvents();
        List<Object> objects = addTimeDividers(events);

        GenericRowAdapter adapter = new GenericRowAdapter();
        adapter.addAll(objects);
        list.setAdapter(adapter);

        return rootView;
    }

    protected List<Default> getEvents() {
        List<Default> events;
        events = getItemByDate(mTypes);
        return events;
    }

    private List<Object> addTimeDividers(List<Default> events) {
        ArrayList<Object> result = new ArrayList<>();

        if (events.size() == 0)
            return result;

        result.add(events.get(0).getDateStamp());
        result.add(events.get(0).getBeginDateObject());

        for (int i = 0; i < events.size() - 1; i++) {
            Default current = events.get(i);

            result.add(current);

            Default next = events.get(i + 1);
            if (!current.getDate().equals(next.getDate())) {
                result.add(next.getDateStamp());
            }

            if (!current.getBegin().equals(next.getBegin())) {
                result.add(next.getBeginDateObject());
            }
        }

        result.add(events.get(events.size() - 1));

        return result;
    }

    protected int getContentView() {
        return R.layout.fragment_list;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule, menu);
    }
}