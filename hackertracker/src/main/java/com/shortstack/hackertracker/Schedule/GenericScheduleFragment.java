package com.shortstack.hackertracker.Schedule;

import android.view.View;

import com.shortstack.hackertracker.List.GenericRowFragment;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Model.Filter;
import com.shortstack.hackertracker.R;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;

public class GenericScheduleFragment extends GenericRowFragment {

    @Bind(R.id.empty)
    View empty;

    public static GenericScheduleFragment newInstance() {
        return (new GenericScheduleFragment());
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_schedule;
    }

    @Override
    protected List<Default> getEvents(Filter filter) {
        if( filter.getTypesSet().size() == 0 ) {
            empty.setVisibility(View.VISIBLE);
            return Collections.emptyList();
        }

        empty.setVisibility(View.GONE);
        return super.getEvents(filter);
    }
}
