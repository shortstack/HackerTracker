package com.shortstack.hackertracker.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.pedrogomez.renderers.RendererAdapter;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.List.GenericRowFragment;
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

import butterknife.Bind;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

public class GenericSearchFragment extends GenericRowFragment {

    @Bind(R.id.search)
    ClearableEditText search;

    private ArrayList<Default> result;

    public static GenericSearchFragment newInstance() {
        GenericSearchFragment frag = new GenericSearchFragment();
        return (frag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<Default> getEvents() {
        return Collections.emptyList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_search;
    }

    @OnTextChanged(R.id.search)
    public void onSearchTextChanged(CharSequence text) {
        new getSearchResults().execute(text.toString());
    }

    @OnFocusChange(R.id.search)
    public void onSearchFocusChanged(boolean isFocused) {
        if (!isFocused) {
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
            if (items != null) {
                RendererAdapter adapter = (RendererAdapter) list.getAdapter();
                adapter.getCollection().clear();
                adapter.getCollection().addAll(items);
                adapter.notifyDataSetChanged();
            }
        }
    }

    public List<Default> searchDatabase(String string) {

        result = new ArrayList<Default>();

        if (string.equals("") || string.trim().isEmpty()) {
            return result;
        }

        SQLiteDatabase dbOfficial = HackerTrackerApplication.dbHelper.getWritableDatabase();

        Cursor hint = dbOfficial.rawQuery("SELECT * FROM data WHERE (type LIKE ?)", new String[]{"%" + string + "%"});
        Cursor titleOfficial = dbOfficial.rawQuery("SELECT * FROM data WHERE (title LIKE ?) AND type NOT LIKE 'Vendor'", new String[]{"%" + string + "%"});
        Cursor bodyOfficial = dbOfficial.rawQuery("SELECT * FROM data WHERE (description LIKE ?) AND type NOT LIKE 'Vendor'", new String[]{"%" + string + "%"});
        Cursor nameOfficial = dbOfficial.rawQuery("SELECT * FROM data WHERE (who LIKE ?) AND type NOT LIKE 'Vendor'", new String[]{"%" + string + "%"});
        Cursor locationOfficial = dbOfficial.rawQuery("SELECT * FROM data WHERE (location LIKE ?) AND type NOT LIKE 'Vendor'", new String[]{"%" + string + "%"});

        // get search results from each query
        getResults(hint);
        getResults(titleOfficial);
        getResults(bodyOfficial);
        getResults(nameOfficial);
        getResults(locationOfficial);

        // close database
        dbOfficial.close();

        // sort by start time
        Collections.sort(result, new ItemComparator());

        SortedSet<Default> items = new TreeSet<Default>(new Comparator<Default>() {
            @Override
            public int compare(Default arg0, Default arg1) {
                return String.valueOf(arg0.getId()).compareTo(String.valueOf(arg1.getId()));
            }
        });

        Iterator<Default> iterator = result.iterator();
        while (iterator.hasNext()) {
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

        try {
            if (cursor.moveToFirst()) {
                do {

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

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }
}
