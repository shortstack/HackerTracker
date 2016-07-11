package com.shortstack.hackertracker.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Created by Whitney Champion on 7/7/16.
 */

public class SpinnerAdapter extends ArrayAdapter<String> {

    public SpinnerAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);

    }

    @Override
    public int getCount() {

        int count = super.getCount();

        return count>0 ? count-1 : count ;

    }

}
