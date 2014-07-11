package com.shortstack.hackertracker.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shortstack.hackertracker.R;

/**
 * Created by whitneychampion on 7/7/14.
 */
public class QuestionAdapter extends ArrayAdapter<CharSequence> {

    Context context;
    int layoutResourceId;
    CharSequence[] data;
    private int[] background = new int[] { 0,1 };

    public QuestionAdapter(Context context, int layoutResourceId, CharSequence[] data) {
        super(context,layoutResourceId,data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final QuestionHolder holder;
        View row = convertView;

        int backgroundSetting = position % background.length;

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new QuestionHolder();
            holder.question = (TextView) row.findViewById(R.id.question);
            holder.layout = (LinearLayout) row.findViewById(R.id.rootLayout);
            row.setTag(holder);

        } else {
            holder = (QuestionHolder)row.getTag();
        }

        final CharSequence item = data[position];

        // if questions in list, populate data
        if (item != null) {

            // set title
            holder.question.setText(item);

            // if it's a question, make it bold
            if (backgroundSetting==0) {
                holder.question.setTypeface(null, Typeface.BOLD);
                holder.question.setPadding(0,100,0,20);
                holder.layout.setBackgroundResource(R.drawable.border_bottom_black);
            } else {
                holder.question.setTypeface(null, Typeface.NORMAL);
                holder.question.setPadding(0,10,0,100);
                holder.layout.setBackgroundResource(R.drawable.border_bottom_green);
            }


        }

        return row;
    }

    static class QuestionHolder {
        TextView question;
        LinearLayout layout;
    }
}
