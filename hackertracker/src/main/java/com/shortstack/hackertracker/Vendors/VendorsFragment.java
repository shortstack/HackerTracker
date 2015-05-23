package com.shortstack.hackertracker.Vendors;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.Adapter.VendorAdapter;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;

import java.util.List;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public class VendorsFragment extends HackerTrackerFragment {

    public Vendor[] vendorData;
    public VendorAdapter adapter;
    private ListView list;
    private static View rootView;
    private Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static VendorsFragment newInstance(int sectionNumber) {
        VendorsFragment fragment = new VendorsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = inflater.getContext();

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_vendors, container, false);
        } catch (InflateException e) {
        }

        list = (ListView) rootView.findViewById(R.id.list_vendors);

        String date = "1";

        // get vendors
        List<Default> vendors = getItemByDate(date, Constants.TYPE_VENDOR);
        if (vendors.size() > 0) {

            vendorData = vendors.toArray(new Vendor[vendors.size()]);

            adapter = new VendorAdapter(context, R.layout.row_vendor, vendorData);

            list.setAdapter(adapter);

        }

        return rootView;
    }

}



