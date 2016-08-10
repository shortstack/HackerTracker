package com.shortstack.hackertracker.List;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class GenericRowFragment extends HackerTrackerFragment {

    protected static final String ARG_TYPE = "type";

    @Bind(R.id.list)
    RecyclerView list;

    private String mType;


    public static GenericRowFragment newInstance(String type) {
        GenericRowFragment frag = new GenericRowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        frag.setArguments(args);

        return (frag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mType = args.getString(ARG_TYPE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getContentView(), container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        list.setLayoutManager(layout);

        List<Default> events = getItemByDate(HackerTrackerFragment.getDate(2), mType);

        if (events.size() > 0) {
            GenericRowAdapter adapter = new GenericRowAdapter();
            adapter.addAll(events);
            list.setAdapter(adapter);
        }

        return rootView;
    }

    protected int getContentView() {
        return R.layout.fragment_list;
    }

}