package com.shortstack.hackertracker.View;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Filter;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;

import butterknife.BindViews;
import butterknife.ButterKnife;

public class FilterView extends LinearLayout {

    @BindViews({R.id.speaker, R.id.skytalk, R.id.event, R.id.village, R.id.kids, R.id.contest, R.id.party, R.id.demo, R.id.workshop})
    AppCompatCheckBox[] types;

    public FilterView(Context context) {
        super(context);
        init();
    }

    public FilterView(Context context, Filter filter ) {
        super(context);
        init();
        setFilter( filter );
    }

    private void setFilter(Filter filter) {
        String[] keys = getContext().getResources().getStringArray(R.array.filter_types);
        String[] typesArray = filter.getTypesArray();
        for (int i = 0; i < typesArray.length; i++) {
            for (int i1 = 0; i1 < keys.length; i1++) {
                if( typesArray[i].equals(keys[i1])) {
                    types[i1].setChecked(true);
                }
            }
        }
    }

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.alert_filter, this);
        ButterKnife.bind(this);
    }

    public Filter save() {
        ArrayList<String> selected = new ArrayList<>();
        String[] keys = getContext().getResources().getStringArray(R.array.filter_types);

        for (int i = 0; i < types.length; i++) {
            CheckBox type = types[i];
            if( type.isChecked() ) {
                selected.add(keys[i]);
            }
        }

        String[] strings = selected.toArray(new String[selected.size()]);

        Filter filter = new Filter(strings);

        App.getStorage().saveFilter( filter );

        return filter;
    }
}
