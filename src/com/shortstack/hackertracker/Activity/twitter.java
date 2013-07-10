package com.shortstack.hackertracker.Activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import com.shortstack.hackertracker.Adapter.tweetAdapter;
import com.shortstack.hackertracker.R;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class twitter extends Activity {

    public ListView tweetListView;
    public List<twitter4j.Status> tweets;
    public twitter4j.Status tweetData[];
    public tweetAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter);

        List<twitter4j.Status> result = null;
        try {
           result = new getTweets().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (!((result).size() < 1)) {
            tweets = result;

            tweetData = tweets.toArray(new twitter4j.Status[tweets.size()]);

            adapter = new tweetAdapter(getApplicationContext(), R.layout.tweet, tweetData);

            tweetListView = (ListView) findViewById(R.id.listView);

            tweetListView.setAdapter(adapter);
        }

    }




    public class getTweets extends AsyncTask<Void, Void, List<twitter4j.Status>> {

        public List<twitter4j.Status> tweetList;

        @Override
        protected List<twitter4j.Status> doInBackground(Void... arg0) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("ZU1e66hITLMNn5RtVdSeSA")
                    .setOAuthConsumerSecret("pvi8ZTGi6LgZdKjb04vSMXKa8Rb07V5fziyF8856PwY")
                    .setOAuthAccessToken("8094902-wyRdtNsRgjYLO1EUPhGuHPyh52wUCFXgaGv291Lrlr")
                    .setOAuthAccessTokenSecret("7SPrqk8Xe8rzYnZrCGrL7dCAe8vshmsfvNmGidDRM");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            try {
                Query query = new Query("defcon");
                QueryResult result;
                result = twitter.search(query);
                tweetList = result.getTweets();
            } catch (TwitterException te) {
                te.printStackTrace();
                Log.v("tweet","Failed to search tweets: " + te.getMessage());
            }

            return tweetList;
        }


    }


}