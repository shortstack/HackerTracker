package com.shortstack.hackertracker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.shortstack.hackertracker.Model.tweet;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/10/13
 * Time: 9:20 AM
 * Description:
 */
public class tweetAdapter extends ArrayAdapter<tweet> {
    private ArrayList<tweet> tweets;
    private Activity activity;

    public tweetAdapter(Activity activity, Context context,
                            int textViewResourceId,
                            ArrayList<tweet> items) {
        super(context, textViewResourceId, items);
        this.tweets = items;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.tweet, null);
        }
        tweet o = tweets.get(position);
        TextView tt = (TextView) v.findViewById(R.id.toptext);
        TextView bt = (TextView) v.findViewById(R.id.bottomtext);
        tt.setText(o.content);
        bt.setText(o.author);
        return v;
    }
}