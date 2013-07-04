package com.shortstack.hackertracker;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.*;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class twitter extends ListActivity {

    private List<HashMap<String,String>> mTweets = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter mAdapter;
    private boolean mKeepRunning = false;
    private String mSearchTerm = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter);

        mAdapter = new SimpleAdapter(this, mTweets, android.R.layout.simple_list_item_2, new String[] {"Tweet", "From"}, new int[] {android.R.id.text1, android.R.id.text2});
        ListView tweets = (ListView)findViewById(android.R.id.list);
        tweets.setAdapter(mAdapter);

    }


    public void startStop( View v ) {
        if( ((Button)v).getText().equals("Start") ) {
            mSearchTerm = ((EditText)findViewById(R.id.SearchText)).getText().toString();
            if( mSearchTerm.length() > 0 ) {
                new StreamTask().execute();
                mKeepRunning = true;

                ((Button)v).setText("Stop");
            }
            else {
                Toast.makeText(this, "Search text cannot be blank.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            mKeepRunning = false;
            ((Button)v).setText("Start");
        }
    }


    private class StreamTask extends AsyncTask<Integer, Integer, Integer> {

        private String mUrl = "https://stream.twitter.com/1.1/statuses/filter.json?track=";

        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                Credentials creds = new UsernamePasswordCredentials("dcandroidtwit", "pHI1$IuNL4ji&d1UWi1fIeBl");
                client.getCredentialsProvider().setCredentials( new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), creds);
                HttpGet request = new HttpGet();
                request.setURI(new URI("https://stream.twitter.com/1.1/statuses/filter.json?track=" + mSearchTerm));
                HttpResponse response = client.execute(request);
                InputStream in = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader( new InputStreamReader(in) );

                parseTweets(reader);

                in.close();

            }
            catch (Exception e) {
                Log.e("Twitter", "doInBackground_" + e.toString());
            }
            return new Integer(1);
        }


        private void parseTweets( BufferedReader reader ) {
            try {
                String line = "";
                do {
                    line = reader.readLine();
                    Log.d("Twitter", "Keep Running: " + mKeepRunning
                            + " Line: " + line);
                    JSONObject tweet = new JSONObject(line);
                    HashMap<String, String> tweetMap = new HashMap<String, String>();
                    if (tweet.has("text")) {
                        tweetMap.put("Tweet", tweet.getString("text"));
                        tweetMap.put("From", "@" + tweet.getJSONObject("user")
                                .getString("screen_name"));
                        mTweets.add(0, tweetMap);
                        if (mTweets.size() > 50) {
                            mTweets.remove(mTweets.size() - 1);
                        }
                        //mAdapter.notifyDataSetChanged();
                        publishProgress(1);
                    }
                } while (mKeepRunning && line.length() > 0);
            }
            catch (Exception e) {
                Log.e("error parsing","wtf");
            }
        }



        protected void onProgressUpdate(Integer... progress) {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Integer i) {

        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        Log.v("list item","clicked");
        String selection = l.getItemAtPosition(position).toString();
        int start = selection.indexOf("From=");
        String suffix = selection.substring(start + 6);
        String[] username = suffix.split("\\}");

        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://twitter.com/"+username[0]));
        startActivity(viewIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.main:
                startActivity(new Intent(twitter.this,
                        HackerTracker.class));
                break;
            case R.id.help:
                AlertDialog.Builder help = new AlertDialog.Builder(twitter.this);
                help.setTitle("Hacker Tracker Help");
                help.setMessage("Help Info Goes Here");
                help.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                    } });
                help.show();
                break;
        }
        return true;
    }

}