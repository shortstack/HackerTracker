package com.shortstack.hackertracker.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shortstack.hackertracker.Adapter.UpdateAdapter;
import com.shortstack.hackertracker.R;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public class HomeFragment extends Fragment {

    private static View rootView;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
        } catch (InflateException e) {
            Log.e("HomeFragment", e.getMessage());
        }

        // get context

        // get list items
        String[] myItems = getResources().getStringArray(R.array.updates);

        // configure the listview
        UpdateAdapter aa = new UpdateAdapter(getContext(), R.layout.row_updates, myItems);
        ListView faq_list = (ListView) rootView.findViewById(R.id.updates);
        faq_list.setAdapter(aa);

        faq_list.addHeaderView(inflater.inflate(R.layout.fragment_home_header, container, false));

        return rootView;
    }

}
