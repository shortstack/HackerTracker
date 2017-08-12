package com.shortstack.hackertracker.View;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Database.DatabaseController;
import com.shortstack.hackertracker.Model.Filter;
import com.shortstack.hackertracker.Model.Types;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterView extends LinearLayout {

    AppCompatCheckBox[] checkboxes;

    @BindView(R.id.filter_left)
    LinearLayout left;

    @BindView(R.id.filter_right)
    LinearLayout right;

    public FilterView(Context context) {
        super(context);
        init();
    }

    public FilterView(Context context, Filter filter) {
        super(context);
        init();
        setFilter(filter);
    }

    private void setFilter(Filter filter) {
        String[] typesArray = filter.getTypesArray();

        if( typesArray.length == 0 ) {
            for (AppCompatCheckBox type : checkboxes) {
                type.setChecked(true);
            }
        }

        for (String aTypesArray : typesArray) {
            for (int i1 = 0; i1 < checkboxes.length; i1++) {
                if (aTypesArray.equals(checkboxes[i1].getText().toString())) {
                    checkboxes[i1].setChecked(true);
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

        DatabaseController controller = App.application.getDatabaseController();
        List<Types.Type> types = controller.getTypes();

        checkboxes = new AppCompatCheckBox[types.size()];

        int[] stringArray = getContext().getResources().getIntArray(R.array.colors);

        int states[][] = {{android.R.attr.state_checked}, {}};

        for (int i = 0; i < types.size(); i++) {
            Types.Type type = types.get(i);

            AppCompatCheckBox box = new AppCompatCheckBox(getContext());
            box.setText(type.getType());
            CompoundButtonCompat.setButtonTintList(box, new ColorStateList(states, new int[]{stringArray[i], stringArray[i]}));

            if (i < types.size() / 2)
                left.addView(box);
            else
                right.addView(box);

            checkboxes[i]= box;
        }


    }

    public Filter save() {
        ArrayList<String> selected = new ArrayList<>();

        for (int i = 0; i < checkboxes.length; i++) {
            CheckBox type = checkboxes[i];
            if (type.isChecked()) {
                selected.add(checkboxes[i].getText().toString());
            }
        }

        String[] strings = selected.toArray(new String[selected.size()]);

        Filter filter = new Filter(strings);

        App.Companion.getStorage().saveFilter(filter);

        return filter;
    }
}
