package com.shortstack.hackertracker.Activity;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shortstack.hackertracker.Adapter.VendorAdapter;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public class VendorsActivity extends AppCompatActivity {

    public static Vendor[] vendorData;
    public VendorAdapter adapter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.viewpager)
    ViewPager viewPager;
    private ScreenSlidePagerAdapter mPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_vendors);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // get vendors
        List<Vendor> vendors = getVendors();
        vendorData = vendors.toArray(new Vendor[vendors.size()]);


        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());


        viewPager.setPadding(px, px / 2, px, px / 2);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(px / 2);

    }

    public List<Vendor> getVendors() {
        ArrayList<Vendor> result = new ArrayList<>();

        SQLiteDatabase db = App.vendorDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM data ORDER BY title", new String[]{});

        try {
            if (cursor.moveToFirst()) {
                do {

                    Vendor item = new Vendor();

                    item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                    item.setImage(cursor.getString(cursor.getColumnIndex("image")));
                    item.setLink(cursor.getString(cursor.getColumnIndex("link")));

                    result.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        db.close();

        return result;
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScreenSlidePageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return vendorData.length;
        }
    }

    public static class ScreenSlidePageFragment extends Fragment {

        protected static final String ARG_POS = "arg_pos";
        private int mPosition;

        @Bind(R.id.vendor_title)
        TextView title;

        @Bind(R.id.vendor_website)
        TextView website;

        @Bind(R.id.vendor_body)
        TextView body;


        public static ScreenSlidePageFragment newInstance(int position) {
            ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_POS, position);
            fragment.setArguments(args);

            return fragment;
        }


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mPosition = getArguments().getInt(ARG_POS);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.row_vendor, container, false);
            ButterKnife.bind(this, rootView);

            Vendor vendor = vendorData[mPosition];
            // if vendors in list, populate data
            if (vendor.getTitle() != null) {

                // set title
                title.setText(vendor.getTitle());

                // set website
                if (vendor.getLink() != null) {
                    website.setText(vendor.getLink());
                } else {
                    website.setVisibility(View.GONE);
                }

                // set body
                body.setText(vendor.getDescription());
            }

            return rootView;
        }
    }
}



