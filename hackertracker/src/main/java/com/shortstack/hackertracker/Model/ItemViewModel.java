package com.shortstack.hackertracker.Model;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemViewModel {

    private static final int EMPTY_CATEGORY = 0;

    private Item mItem;

    public ItemViewModel(Item item) {
        mItem = item;
    }


    public String getTitle() {
        String title = mItem.getTitle();
        if (!TextUtils.isEmpty(title) && title.endsWith("\n"))
            return title.substring(0, title.indexOf("\n"));
        return title;
    }

    public String getDescription() {
        return mItem.getDescription().replace("\\\"", "\"");
    }

    public int getCategoryColorPosition() {
        if (TextUtils.isEmpty(mItem.getType()))
            return EMPTY_CATEGORY;

        List<Types.Type> types = App.application.getDatabaseController().getTypes();

        for (int i = 0; i < types.size(); i++) {
            if( mItem.getType().equals(types.get(i).getType()))
                return i;
        }

        return EMPTY_CATEGORY;
    }


    public String getTimeStamp(Context context) {
        // No start time, return TBA.
        if (TextUtils.isEmpty(mItem.getBegin()))
            return context.getResources().getString(R.string.tba);

        String time = "";

        if (App.application.getStorage().shouldShowMilitaryTime()) {
            time = mItem.getBegin();
        } else {
            Date date = mItem.getBeginDateObject();
            if (date != null) {
                DateFormat writeFormat = new SimpleDateFormat("h:mm aa");
                time = writeFormat.format(date);
            }
        }

        return time;
    }

    public static String getTimeStamp(Context context, Date date) {
        // No start time, return TBA.
        if (date == null)
            return context.getResources().getString(R.string.tba);

        String time;
        DateFormat writeFormat;

        if (App.application.getStorage().shouldShowMilitaryTime()) {
            writeFormat = new SimpleDateFormat("HH:mm");
        } else {
            writeFormat = new SimpleDateFormat("h:mm aa");
        }

        time = writeFormat.format(date);

        return time;
    }


    public float getProgress() {
        if (!mItem.hasBegin())
            return 0;

        Date beginDateObject = mItem.getBeginDateObject();
        Date endDateObject = mItem.getEndDateObject();
        Date currentDate = App.Companion.getCurrentDate();

        float length = (endDateObject.getTime() - beginDateObject.getTime()) / 1000 / 60;
        float p = (endDateObject.getTime() - currentDate.getTime()) / 1000 / 60;

        if (p == 0)
            return 1;

        float l = p / length;

        return Math.min(1.0f, 1 - l);
    }

    public String getFullTimeStamp(Context context) {
        Date begin = mItem.getBeginDateObject();
        Date end = mItem.getEndDateObject();

        return String.format(context.getString(R.string.timestamp_full), mItem.getDateStamp(), getTimeStamp(context, begin), getTimeStamp(context, end));
    }


    public String getDisplayTitle() {
        return /*(BuildConfig.DEBUG ? mItem.getIndex() + " " : "") +*/ mItem.getTitle();
    }

    public boolean hasDescription() {
        return !TextUtils.isEmpty(mItem.getDescription());
    }

    public boolean hasUrl() {
        return !TextUtils.isEmpty(mItem.getLink());
    }

    public String getPrettyUrl() {
        String url = mItem.getLink().toLowerCase();

        int index;


        if (url.startsWith("http://") || url.startsWith("https://")) {
            index = url.indexOf("//");
            url = url.substring(index + 2);
        }

        index = url.indexOf("www.");
        if (index > 0)
            url = url.substring(index);

        index = url.indexOf("/");
        if (index > 1) {

            Pattern p = Pattern.compile("[\\./?]");
            Matcher m = p.matcher(url.substring(index + 1));

            if (m.find()) {
                url = url.substring(0, index + m.start() + 1);
            }
        }

        if (url.length() < mItem.getLink().length()) {
            url = url.concat("...");
        }


        return url;
    }

    public String getDetailsDescription(Context context) {
        String result = "";

        result = result.concat(mItem.getTitle() + "\n");

        result = result.concat(getFullTimeStamp(context) + "\n");
        result = result.concat(mItem.getLocation() + "\n");
        //result = result.concat(getType());


        return result;
    }

    public String getLocation() {
        return mItem.getLocation();
    }

    public Item getItem() {
        return mItem;
    }

    public int getId() {
        return mItem.getIndex();
    }

    public int getToolsVisibility() {
        return mItem.isTool() ? View.VISIBLE : View.GONE;
    }

    public int getExploitVisibility() {
        return mItem.isExploit() ? View.VISIBLE : View.GONE;
    }

    public int getDemoVisibility() {
        return mItem.isDemo() ? View.VISIBLE : View.GONE;
    }

    public int getBookmarkVisibility() {
        return mItem.isBookmarked() ? View.VISIBLE : View.INVISIBLE;
    }

    public Speakers.Speaker[] getSpeakers() {
        return mItem.getSpeakers();
    }
}
