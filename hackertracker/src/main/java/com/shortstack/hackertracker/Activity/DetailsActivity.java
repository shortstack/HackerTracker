package com.shortstack.hackertracker.Activity;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity {



    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.location)
    TextView location;

    @Bind(R.id.category_text)
    TextView categoryText;

    @Bind(R.id.demo)
    View demo;

    @Bind(R.id.exploit)
    View exploit;

    @Bind(R.id.tool)
    View tool;

    @Bind(R.id.container)
    View container;

    @Bind(R.id.category)
    View category;



    // Description

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.empty)
    View empty;

    @Bind(R.id.link_container)
    View linkContainer;

    @Bind(R.id.link)
    TextView link;





    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private Default mItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_tab);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if( getIntent().getExtras() != null ) {
            mItem = Parcels.unwrap(getIntent().getExtras().getParcelable("item"));
        } else if ( savedInstanceState != null ) {
            mItem = savedInstanceState.getParcelable("item");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            container.setTransitionName("category");
        }

        setTitle("");

        if( getSupportActionBar() != null ) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        updateDescription();

        render();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);

        menu.findItem(R.id.bookmark).setIcon( isOnSchedule() ?  R.drawable.ic_bookmark_white_24dp : R.drawable.ic_bookmark_border_white_24dp );

        return true;
    }

    private void updateDescription() {
        boolean hasDescription = mItem.hasDescription();

        if (hasDescription)
            description.setText(mItem.getDescription());
        empty.setVisibility(hasDescription ? View.GONE : View.VISIBLE);

        boolean hasUrl = mItem.hasUrl();
        if (hasUrl) {
            link.setText(mItem.getPrettyUrl());
        }
        linkContainer.setVisibility(hasUrl ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.link_container)
    public void onLinkClick() {
        MaterialAlert.create(this).setTitle(R.string.link_warning).setMessage(String.format(getString(R.string.link_message), mItem.getLink().toLowerCase())).setPositiveButton(R.string.open_link, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(mItem.getLink()));
                startActivity(intent);
            }
        })
                .setBasicNegativeButton().show();
    }




    public void render() {
        displayText();
        displaySpeakerIcons();
        displayCategory();

        updateBookmark();
    }

    public Default getContent() {
        return mItem;
    }

    private void displayCategory() {
        int count = getContent().getCategoryColorPosition();

        String[] allColors = getResources().getStringArray(R.array.colors);
        String[] allLabels = getResources().getStringArray(R.array.filter_types);

        int position = count % allColors.length;

        int color = Color.parseColor(allColors[position]);
        category.setBackgroundColor(color);

        categoryText.setText(allLabels[position]);
    }

    private boolean isOnSchedule() {
        return getContent().isBookmarked();
    }

    public void updateBookmark() {
        //bookmark.setVisibility( isOnSchedule() ? View.VISIBLE : View.GONE );
        invalidateOptionsMenu();
    }

    private void displayText() {
        title.setText(getContent().getDisplayTitle());
        time.setText(getContent().getFullTimeStamp(this));
        location.setText(getContent().getLocation());
    }

    private void displaySpeakerIcons() {
        tool.setVisibility(getContent().isTool() ? View.VISIBLE : View.GONE);
        exploit.setVisibility(getContent().isExploit() ? View.VISIBLE : View.GONE);
        demo.setVisibility(getContent().isDemo() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return false;
            case R.id.share:
                showShareAlert();
                return true;

            case R.id.bookmark:
                updateSchedule();
                return true;
        }
    }

    private void showShareAlert() {
        MaterialAlert.create(this).setTitle(R.string.action_share).setMessage(R.string.share_message).setNegativeButton(R.string.link_in_app, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO: Generate an in-app link.

                String text = "This is my text to send.";

                StartShareActivity(text);
            }
        }).setPositiveButton(R.string.link_plain_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO: Generate plain text description.
                // Include:
                // Title, Description, Time, Location, Category Type
                String text = "This is my text to send.";

                StartShareActivity(text);
            }
        }).show();
    }

    private void StartShareActivity(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    public void updateSchedule() {


        // if not starred, star it
        if (mItem.isUnbookmarked()) {
            App.getApplication().getDatabaseController().bookmark(mItem);


            long notifyTime = mItem.getNotificationTimeInMillis();
            if( notifyTime > 0 ) {
                Notification notification = App.createNotification(this, mItem);
                App.scheduleNotification(notification, notifyTime, mItem.getId());
            }

            Toast.makeText(this, R.string.schedule_added, Toast.LENGTH_SHORT).show();
        } else {
            App.getApplication().getDatabaseController().unbookmark(mItem);


            // remove alarm
            App.cancelNotification(mItem.getId());

            Toast.makeText(this, R.string.schedule_removed, Toast.LENGTH_SHORT).show();
        }



        updateBookmark();

        Logger.d("Posting event.");
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
