package com.shortstack.hackertracker.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    public ProgressDialog progress;

    public interface Callback {

        void run(Object result);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter);

        progress = new ProgressDialog(this);
        progress.setTitle("Searching Tweets");
        progress.setMessage("Please wait...");


        getTweets request = new getTweets(new Callback() {
           public void run(Object result) {


                if (!(((List<twitter4j.Status>) result).size() < 1)) {
                    tweets = (List<twitter4j.Status>) result;

                    tweetData = tweets.toArray(new twitter4j.Status[tweets.size()]);

                    adapter = new tweetAdapter(getApplicationContext(), R.layout.tweet, tweetData);

                    tweetListView = (ListView) findViewById(R.id.listView);

                    tweetListView.setAdapter(adapter);
                }

           }});
        request.execute();

    }



    public class getTweets extends AsyncTask<Void, Void, Object> {

        public Object tweetList;

        Callback callback;
        public getTweets(Callback callback){
            this.callback = callback;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            progress.show();
        }

        @Override
        protected Object doInBackground(Void... arg0) {

            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("ZU1e66hITLMNn5RtVdSeSA")
                    .setOAuthConsumerSecret("pvi8ZTGi6LgZdKjb04vSMXKa8Rb07V5fziyF8856PwY")
                    .setOAuthAccessToken("8094902-wyRdtNsRgjYLO1EUPhGuHPyh52wUCFXgaGv291Lrlr")
                    .setOAuthAccessTokenSecret("7SPrqk8Xe8rzYnZrCGrL7dCAe8vshmsfvNmGidDRM");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();
            try {
                Query query = new Query("#defcon");
                QueryResult result;
                result = twitter.search(query);
                tweetList = result.getTweets();
            } catch (TwitterException te) {
                te.printStackTrace();
                Log.v("tweet","Failed to search tweets: " + te.getMessage());
            }


            progress.dismiss();
            return tweetList;
        }

        protected void onPostExecute(Object result) {
            callback.run(result);
            progress.dismiss();
        }

    }


}