package com.shortstack.hackertracker.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;

import com.shortstack.hackertracker.Adapter.DefaultAdapter;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.ClearableEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Whitney Champion on 7/7/14.
 */
public class SearchFragment extends Fragment {

    private static View rootView;
    private Context context;
    private DefaultAdapter adapter;
    private ListView list;
    private ArrayList<Default> result;
    private ClearableEditText search;
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
        search = (ClearableEditText) rootView.findViewById(R.id.search);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString()!="") {
                    new getSearchResults().execute(s.toString());
                }
            }
        });
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard();
                }

            }
        });

        return rootView;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
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

                adapter = new DefaultAdapter(context, R.layout.row, items);

                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        }

    }

    public List<Default> searchDatabase(String string) {

        result = new ArrayList<Default>();

        if (string.equals("") || string.trim().isEmpty()) {
            return result;
        }

        SQLiteDatabase dbOfficial = HackerTrackerApplication.myOfficialDbHelper.getWritableDatabase();
        SQLiteDatabase db = HackerTrackerApplication.myDbHelper.getWritableDatabase();

        Cursor titleOfficial = dbOfficial.rawQuery("SELECT * FROM data WHERE (title LIKE ?) AND type NOT LIKE 'Vendor'", new String[]{"%" + string + "%"});
        Cursor bodyOfficial = dbOfficial.rawQuery("SELECT * FROM data WHERE (description LIKE ?) AND type NOT LIKE 'Vendor'", new String[] {"%"+string+"%"});
        Cursor nameOfficial = dbOfficial.rawQuery("SELECT * FROM data WHERE (who LIKE ?) AND type NOT LIKE 'Vendor'", new String[]{"%" + string + "%"});
        Cursor locationOfficial = dbOfficial.rawQuery("SELECT * FROM data WHERE (location LIKE ?) AND type NOT LIKE 'Vendor'", new String[]{"%" + string + "%"});
        Cursor title = db.rawQuery("SELECT * FROM data WHERE (title LIKE ?) AND type NOT LIKE 'Vendor'", new String[]{"%" + string + "%"});
        Cursor body = db.rawQuery("SELECT * FROM data WHERE (description LIKE ?) AND type NOT LIKE 'Vendor'", new String[] {"%"+string+"%"});
        Cursor name = db.rawQuery("SELECT * FROM data WHERE (who LIKE ?) AND type NOT LIKE 'Vendor'", new String[] {"%"+string+"%"});
        Cursor location = db.rawQuery("SELECT * FROM data WHERE (location LIKE ?) AND type NOT LIKE 'Vendor'", new String[] {"%"+string+"%"});

        // get search results from each query
        getResults(titleOfficial);
        getResults(bodyOfficial);
        getResults(nameOfficial);
        getResults(locationOfficial);
        getResults(title);
        getResults(body);
        getResults(name);
        getResults(location);

        // close database
        dbOfficial.close();
        db.close();

        // sort by start time
        Collections.sort(result, new ItemComparator());

        SortedSet<Default> items = new TreeSet<Default>(new Comparator<Default>() {
            @Override
            public int compare(Default arg0, Default arg1) {
                return String.valueOf(arg0.getId()).compareTo(String.valueOf(arg1.getId()));
            }
        });

        Iterator<Default> iterator = result.iterator();
        while(iterator.hasNext()) {
            items.add(iterator.next());
        }
        result.clear();
        result.addAll(items);

        return result;
    }



    public class ItemComparator implements Comparator<Default> {

        private Default left;
        private Default right;

        public int compare(Default left, Default right) {
            this.left = left;
            this.right = right;
            return this.left.getBegin().compareTo(this.right.getBegin());
        }

    }

    private void getResults(Cursor cursor) {

        try{
            if (cursor.moveToFirst()){
                do{

                    Default item = new Default();

                    item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    item.setType(cursor.getString(cursor.getColumnIndex("type")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                    item.setName(cursor.getString(cursor.getColumnIndex("who")));
                    item.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    item.setEnd(cursor.getString(cursor.getColumnIndex("end")));
                    item.setBegin(cursor.getString(cursor.getColumnIndex("begin")));
                    item.setLocation(cursor.getString(cursor.getColumnIndex("location")));
                    item.setStarred(cursor.getInt(cursor.getColumnIndex("starred")));
                    item.setImage(cursor.getString(cursor.getColumnIndex("image")));
                    item.setLink(cursor.getString(cursor.getColumnIndex("link")));
                    item.setIsNew(cursor.getInt(cursor.getColumnIndex("is_new")));
                    item.setDemo(cursor.getInt(cursor.getColumnIndex("demo")));
                    item.setExploit(cursor.getInt(cursor.getColumnIndex("exploit")));
                    item.setTool(cursor.getInt(cursor.getColumnIndex("tool")));

                    result.add(item);

                }while(cursor.moveToNext());
            }
        }finally{
            cursor.close();
        }
    }


}



