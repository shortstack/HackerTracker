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
public class tweetAdapter extends ArrayAdapter<twitter4j.Status> {

    Context context;
    int layoutResourceId;
    twitter4j.Status data[] = null;

    public tweetAdapter(Context context, int layoutResourceId, twitter4j.Status[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TweetHolder holder;
        View row = convertView;

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TweetHolder();
            holder.userName = (TextView) row.findViewById(R.id.userName);
            holder.tweet = (TextView) row.findViewById(R.id.tweet);
            row.setTag(holder);


        } else {
            holder = (TweetHolder)row.getTag();
        }

        final twitter4j.Status tweet = data[position];


        // if tweets in list, populate data
        if (tweet.getText() != null) {

            // set username
            holder.userName.setText(tweet.getUser().getScreenName());

            // set event title
            holder.tweet.setText(tweet.getText());





        } else {
            holder.userName.setText("No tweets found");
        }

        return row;
    }

    static class TweetHolder {
        TextView userName;
        TextView tweet;
    }
}