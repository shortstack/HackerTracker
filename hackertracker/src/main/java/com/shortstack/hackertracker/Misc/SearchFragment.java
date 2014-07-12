package com.shortstack.hackertracker.Misc;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.Adapter.DefaultAdapter;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Whitney Champion on 7/7/14.
 */
public class SearchFragment extends Fragment {

    private static View rootView;
    private Context context;
    private DefaultAdapter adapter;
    private ListView list;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_search, container, false);
        } catch (InflateException e) {
        }

        // get context
        context = inflater.getContext();

        // set up listview
        list = (ListView) rootView.findViewById(R.id.search_list);

        // edittext listener
        EditText search = (EditText) rootView.findViewById(R.id.search);
        search.requestFocus();
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    new getSearchResults().execute(s.toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        return rootView;
    }

    class getSearchResults extends AsyncTask<String, Void, List<Default>> {

        @Override
        protected List<Default> doInBackground(String... search) {
            List<Default> items = searchDatabase(search[0]);
            return items;
        }

        @Override
        protected void onPostExecute(List<Default> items) {
            if (items!=null) {
                if (items.size() > 0) {

                    adapter = new DefaultAdapter(context, R.layout.row, items);

                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            }
        }

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((HomeActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public List<Default> searchDatabase(String string) {

        ArrayList<Default> result = new ArrayList<Default>();
        SQLiteDatabase db = HackerTrackerApplication.myDbHelper.getWritableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM data WHERE (title LIKE ? OR body LIKE ? OR name LIKE ?) AND type <> 5", new String[] {"%"+string+"%"});

        try{
            if (myCursor.moveToFirst()){
                do{

                    Default item = new Default();

                    item.setId(myCursor.getInt(myCursor.getColumnIndex("id")));
                    item.setType(myCursor.getInt(myCursor.getColumnIndex("type")));
                    item.setTitle(myCursor.getString(myCursor.getColumnIndex("title")));
                    item.setBody(myCursor.getString(myCursor.getColumnIndex("body")));
                    item.setName(myCursor.getString(myCursor.getColumnIndex("name")));
                    item.setDate(myCursor.getInt(myCursor.getColumnIndex("date")));
                    item.setEndTime(myCursor.getString(myCursor.getColumnIndex("endTime")));
                    item.setStartTime(myCursor.getString(myCursor.getColumnIndex("startTime")));
                    item.setLocation(myCursor.getString(myCursor.getColumnIndex("location")));
                    item.setStarred(myCursor.getInt(myCursor.getColumnIndex("starred")));
                    item.setImage(myCursor.getString(myCursor.getColumnIndex("image")));
                    item.setForum(myCursor.getString(myCursor.getColumnIndex("forum")));

                    result.add(item);

                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }

        db.close();

        return result;
    }
}



