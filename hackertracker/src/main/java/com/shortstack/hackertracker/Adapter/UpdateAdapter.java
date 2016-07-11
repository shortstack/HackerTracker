package com.shortstack.hackertracker.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shortstack.hackertracker.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by whitneychampion on 7/20/14.
 */
public class UpdateAdapter extends ArrayAdapter<String> {

    Context context;
    int layoutResourceId;
    String[] data;

    public UpdateAdapter(Context context, int layoutResourceId, String[] data) {
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

        final String item = data[position];

        // if updates in list, populate data
        if (item != null) {

            // set text
            holder.update.setText(Html.fromHtml(item));

            // color/bold dates
            if (position % 2 == 0) {
                holder.update.setTextColor(ContextCompat.getColor(context,R.color.colorLightAccent));
                holder.update.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                holder.update.setTextColor(ContextCompat.getColor(context,R.color.white));
                holder.update.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }

            // get text from regex
            Linkify.TransformFilter mentionFilter = new Linkify.TransformFilter() {
                public final String transformUrl(final Matcher match, String url) {
                    return match.group(1);
                }
            };

            // find @mentions and link username to twitter
            Pattern pattern = Pattern.compile("@([A-Za-z0-9_-]+)");
            String scheme = "http://twitter.com/";
            Linkify.addLinks(holder.update, pattern, scheme, null, mentionFilter);

        }

        return row;
    }

    static class UpdateHolder {
        TextView update;
    }
}
