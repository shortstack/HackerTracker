package com.shortstack.hackertracker.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shortstack.hackertracker.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by whitneychampion on 7/7/14.
 */
public class UpdateAdapter extends ArrayAdapter<CharSequence> {

    Context context;
    int layoutResourceId;
    CharSequence[] data;

    public UpdateAdapter(Context context, int layoutResourceId, CharSequence[] data) {
        super(context,layoutResourceId,data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UpdateHolder holder;
        View row = convertView;

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new UpdateHolder();
            holder.update = (TextView) row.findViewById(R.id.update);
            row.setTag(holder);

        } else {
            holder = (UpdateHolder)row.getTag();
        }

        final CharSequence item = data[position];

        // if questions in list, populate data
        if (item != null) {

            // set title
            holder.update.setText(Html.fromHtml(item.toString()));

        }

        return row;
    }

    static class UpdateHolder {
        TextView update;
    }
}
