package com.shortstack.hackertracker.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shortstack.hackertracker.R;

/**
 * Created by Whitney Champion on 7/7/14.
 */
public class LinksFragment extends Fragment {

    private static View rootView;
    private Context context;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static LinksFragment newInstance(int sectionNumber) {
        LinksFragment fragment = new LinksFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public LinksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_links, container, false);
        } catch (InflateException e) {
        }

        // get context
        context = inflater.getContext();

        // display links
        TextView links = (TextView) rootView.findViewById(R.id.links);
        TextView links_top = (TextView) rootView.findViewById(R.id.links_top);
        links.setText(getString(R.string.link_01) + getString(R.string.link_02) + getString(R.string.link_04) + getString(R.string.link_05) + getString(R.string.link_06) + getString(R.string.link_07));
        links_top.setText(Html.fromHtml(getString(R.string.link_03)));

        // make clickable
        links.setMovementMethod(LinkMovementMethod.getInstance());

        return rootView;
    }

}



