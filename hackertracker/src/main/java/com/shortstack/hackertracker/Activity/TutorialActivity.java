package com.shortstack.hackertracker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.ViewPagerIndicatorDots;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TutorialActivity extends AppCompatActivity {

    public static final int SIZE = 4;
    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Bind(R.id.indicator)
    ViewPagerIndicatorDots indicator;

    @Bind(R.id.button)
    View button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ButterKnife.bind(this);

        viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
        indicator.setViewPager(viewPager);
    }

    @OnClick(R.id.button)
    public void onButtonClick() {
        startActivity(new Intent(this, TabHomeActivity.class));
        finish();
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
            return SIZE;
        }
    }

    public static class ScreenSlidePageFragment extends Fragment {

        protected static final String ARG_POS = "arg_pos";
        private int mPosition;

        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.description)
        TextView description;



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
                    R.layout.row_tutorial, container, false);
            ButterKnife.bind(this, rootView);


            switch (mPosition) {
                case 0:
                    title.setText("Welcome to\nDEFCON");
                    //description.setText("Here's the tl;dr.");
                    description.setText("");
                    break;
                case 1:
                    title.setText("Where/When");
                    //description.setText("DEFCON 25 will be held at the Paris Hotel in Las Vegas, from August 4th till August 9th.");
                    description.setText("Paris Hotel in Las Vegas.\n\nAugust 4th - August 9th.");
                    break;
                case 2:
                    title.setText("Badges");
                    //description.setText("Gaining admission to DEFCON costs $240 (Cash Only) and start selling on August 4th at 7:00 AM.");
                    description.setText("On sale August 4th at 7:00 AM.\n\n$240 - Cash ONLY\n\nGet ready for #LINECON");
                    break;
                case 3:
                    title.setText("Workshops");
                    //description.setText("Workshops are free first come, first served 4 hour classes. Sign up on August 4th at 10:00 AM.");
                    description.setText("Free 4 hour classes.\n\nFirst come, first served.\n\nSign up August 4th at 10:00 AM.");
                    break;
            }

            switch (mPosition){
                case 0:
                    image.setImageDrawable(getResources().getDrawable(R.drawable.skull_lg));
                    break;
                default:
                    image.setImageDrawable(null);
                    break;
            }


            title.setText(title.getText().toString().toUpperCase());

            title.setVisibility( title.getText().length() == 0 ? View.GONE : View.VISIBLE);
            description.setVisibility( description.getText().length() == 0 ? View.GONE : View.VISIBLE);

            return rootView;
        }
    }
}
