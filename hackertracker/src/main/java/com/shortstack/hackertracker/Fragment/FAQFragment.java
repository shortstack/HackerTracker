package com.shortstack.hackertracker.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shortstack.hackertracker.Adapter.QuestionAdapter;
import com.shortstack.hackertracker.R;

/**
 * Created by Whitney Champion on 7/7/14.
 */
public class FAQFragment extends Fragment {

    private static View rootView;
    private Context context;
    private QuestionAdapter adapter;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static FAQFragment newInstance(int sectionNumber) {
        FAQFragment fragment = new FAQFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FAQFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_faq, container, false);
        } catch (InflateException e) {
        }

        // get context
        context = inflater.getContext();

        // get list items
        CharSequence[] myItems = getResources().getTextArray(R.array.faq_questions);

        // configure the listview
        adapter = new QuestionAdapter(context,R.layout.row_faq, myItems);
        ListView faq_list = (ListView) rootView.findViewById(R.id.faqs);
        faq_list.setAdapter(adapter);

        return rootView;
    }

}



