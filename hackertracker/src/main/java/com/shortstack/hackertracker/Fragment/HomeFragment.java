package com.shortstack.hackertracker.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.shortstack.hackertracker.Adapter.UpdateAdapter;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public class HomeFragment extends Fragment {

    private static View rootView;
    private Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static HomeFragment newInstance(int sectionNumber) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
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
        context = inflater.getContext();

        // get list items
        CharSequence[] myItems = getResources().getTextArray(R.array.updates);

        // configure the listview
        UpdateAdapter aa = new UpdateAdapter(context,R.layout.row_updates,myItems);
        ListView faq_list = (ListView) rootView.findViewById(R.id.updates);
        faq_list.setAdapter(aa);

        // logo onclick listener
        ImageView logo = (ImageView) rootView.findViewById(R.id.logo);
        logo.setOnClickListener(new LogoOnClickListener());

        return rootView;
    }

    public class LogoOnClickListener implements View.OnClickListener
    {

        int clicks = 0;

        @Override
        public void onClick(View v)
        {
            clicks += 1;
            if (clicks == 1) {
                Toast.makeText(context,getString(R.string.message01),Toast.LENGTH_SHORT).show();
            } else if (clicks == 2) {
                Toast.makeText(context,getString(R.string.message02),Toast.LENGTH_SHORT).show();
            } else if (clicks == 3) {
                Toast.makeText(context,getString(R.string.message03), Toast.LENGTH_SHORT).show();
            } else if (clicks == 4) {
                DialogUtil.darknetDialog(context, getResources().getString(R.string.code02)).show();
            }
        }

    }
}
