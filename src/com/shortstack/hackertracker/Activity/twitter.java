package com.shortstack.hackertracker.Activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import com.shortstack.hackertracker.Adapter.tweetAdapter;
import com.shortstack.hackertracker.Model.tweet;
import com.shortstack.hackertracker.R;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class twitter extends ListActivity {


    private ArrayList<tweet> tweets = new ArrayList<tweet>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new MyTask().execute();
    }


    private class MyTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(twitter.this,
                    "", "Loading. Please wait...", true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                HttpClient hc = new DefaultHttpClient();
                HttpGet get = new
                        HttpGet("http://search.twitter.com/search.json?q=#defcon");
                HttpResponse rp = hc.execute(get);
                if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    String result = EntityUtils.toString(rp.getEntity());
                    JSONObject root = new JSONObject(result);
                    JSONArray sessions = root.getJSONArray("results");
                    for (int i = 0; i < sessions.length(); i++) {
                        JSONObject session = sessions.getJSONObject(i);
                        tweet tweet = new tweet();
                        tweet.content = session.getString("text");
                        tweet.author = session.getString("from_user");
                        tweets.add(tweet);
                    }
                }
            } catch (Exception e) {
                Log.e("twitter", "Error loading JSON", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            setListAdapter(new tweetAdapter(
                    twitter.this, twitter.this, R.layout.tweet, tweets));
        }
    }


}