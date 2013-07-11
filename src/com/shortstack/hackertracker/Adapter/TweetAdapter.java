package com.shortstack.hackertracker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shortstack.hackertracker.R;


/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/10/13
 * Time: 9:20 AM
 * Description:
 */
public class TweetAdapter extends ArrayAdapter<twitter4j.Status> {

    Context context;
    int layoutResourceId;
    twitter4j.Status data[] = null;

    public TweetAdapter(Context context, int layoutResourceId, twitter4j.Status[] data) {
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
            holder.tweetLayout = (LinearLayout) row.findViewById(R.id.tweetLayout);
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

            // set onclick listener for tweet
            holder.tweetLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("http://twitter.com/" + tweet.getUser().getScreenName() + "/status/" + tweet.getId()));
                    getContext().startActivity(intent);
                }
            });


        } else {
            holder.userName.setText("No tweets found");
        }

        return row;
    }

    static class TweetHolder {
        TextView userName;
        TextView tweet;
        LinearLayout tweetLayout;
    }
}