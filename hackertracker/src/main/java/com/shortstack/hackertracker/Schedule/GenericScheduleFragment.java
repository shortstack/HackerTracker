package com.shortstack.hackertracker.Schedule;

import com.shortstack.hackertracker.List.GenericRowFragment;
import com.shortstack.hackertracker.Model.Default;

import java.util.List;

public class GenericScheduleFragment extends GenericRowFragment {
    public static GenericScheduleFragment newInstance() {
        return (new GenericScheduleFragment());
    }

    @Override
    protected List<Default> getEvents() {
        return getStars();
    }
}
