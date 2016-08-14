package com.shortstack.hackertracker.Activity;

import android.app.Notification;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Adapter.DatabaseAdapterStarred;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.List.GenericDefaultRenderer;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Admin on 8/8/2016.
 */
public class DetailsActivity extends FrameLayout {

    private final GenericDefaultRenderer mRenderer;
    private Default item;

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.speaker)
    TextView host;

    @Bind(R.id.time)
    TextView time;

    @Bind(R.id.location)
    TextView location;

    @Bind(R.id.description)
    TextView description;

    @Bind(R.id.demo)
    View demo;

    @Bind(R.id.exploit)
    View exploit;

    @Bind(R.id.tool)
    View tool;

    @Bind(R.id.category)
    View category;

    @Bind(R.id.container)
    View container;

    @Bind(R.id.action_bookmark)
    ImageView bookmark;

    public DetailsActivity(Context context, Default item, GenericDefaultRenderer genericDefaultRenderer) {
        super(context);
        init();
        this.item = item;
        display();

        mRenderer = genericDefaultRenderer;

    }

    private void init() {
        inflate(getContext(), R.layout.details_contents, this);
        ButterKnife.bind(this);
    }

    private void display() {
        displayText();
        displaySpeakerIcons();
        displayNewIcon();
        displayCategory();

        updateBookmark();
    }

    private void displayText() {
        title.setText(getContent().getTitle());

        if( getContent().hasHost() ) {
            host.setText(getContent().getName());
            host.setVisibility(View.VISIBLE);
        }
        time.setText(getContent().getFullTimeStamp(getContext()));
        time.setVisibility(View.VISIBLE);
        location.setText(getContent().getLocation());
        if( item.hasDescription() ) {
            description.setText(item.getDescription());
        } else {
            description.setVisibility(GONE);
        }


    }

    private void displaySpeakerIcons() {
        tool.setVisibility(getContent().isTool() ? View.VISIBLE : View.GONE);
        exploit.setVisibility(getContent().isExploit() ? View.VISIBLE : View.GONE);
        demo.setVisibility(getContent().isDemo() ? View.VISIBLE : View.GONE);
    }

    private void displayNewIcon() {
        //isNew.setVisibility(getContent().isNew() ? View.VISIBLE : View.GONE);
    }

    private void displayCategory() {
        int count = getContent().getCategoryColorPosition();
        String[] allColors = getResources().getStringArray(R.array.colors);

        int color = Color.parseColor(allColors[count % allColors.length]);
        category.setBackgroundColor(color);
    }

    private Default getContent() {
        return item;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.details, menu);
//
//        if (item.getLink() == null || item.getLink().equals("") || item.getLink().equals(" ")) {
//            menu.findItem(R.id.information).setVisible(false);
//        } else {
//            menu.findItem(R.id.information).setVisible(true);
//        }
//
//        return super.onCreateOptionsMenu(menu);
//    }


    @OnClick(R.id.action_bookmark)
    public void updateSchedule() {
        DatabaseAdapter myDbOfficialHelper = new DatabaseAdapter(getContext());
        DatabaseAdapterStarred myDbHelperStars = new DatabaseAdapterStarred(getContext());
        SQLiteDatabase dbOfficial = myDbOfficialHelper.getWritableDatabase();
        SQLiteDatabase dbStars = myDbHelperStars.getWritableDatabase();

        // if not starred, star it
        if (item.getStarred() == 0) {

            // add to starred database
            dbStars.execSQL("INSERT INTO data VALUES (" + item.getId() + ")");
            dbOfficial.execSQL("UPDATE data SET starred=" + 1 + " WHERE id=" + item.getId());

            // set up alarm
            if (!item.getBegin().equals("")) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(item.getDate().split("-")[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(item.getDate().split("-")[1]) - 1);
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(item.getDate().split("-")[2]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(item.getBegin().split(":")[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(item.getBegin().split(":")[1]));
                calendar.set(Calendar.SECOND, 0);
                long when = calendar.getTimeInMillis() - 1200000;

                Notification notification = HackerTrackerApplication.createNotification(getContext(), item);
                HackerTrackerApplication.scheduleNotification(notification, when, item.getId());
            }

            // change star
            item.setStarred(1);
            Toast.makeText(getContext(), R.string.schedule_added, Toast.LENGTH_SHORT).show();
        } else {

            // remove from starred database
            dbStars.delete("data", "id=" + item.getId(), null);
            dbOfficial.execSQL("UPDATE data SET starred=" + 0 + " WHERE id=" + item.getId());

            // remove alarm
            HackerTrackerApplication.cancelNotification(item.getId());

            // change star
            item.setStarred(0);
            Toast.makeText(getContext(), R.string.schedule_removed, Toast.LENGTH_SHORT).show();
        }

        mRenderer.updateBookmark();

        updateBookmark();

        dbOfficial.close();
        dbStars.close();
    }

    private void updateBookmark() {
        bookmark.setImageDrawable(getResources().getDrawable( item.isStarred() ? R.drawable.ic_bookmark_white_24dp : R.drawable.ic_bookmark_border_white_24dp));
    }
}
