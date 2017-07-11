package com.shortstack.hackertracker.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Application.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Item implements Serializable {

    static final int BOOKMARKED = 1;
    static final int UNBOOKMARKED = 0;

    private static final int TOOL = 1;
    private static final int EXPLOIT = 1;
    private static final int DEMO = 1;
    private static final int NEW = 1;

    private int id;
    @SerializedName("entry_type")
    private String type;

    private String title;
    @SerializedName("who")
    private String hostString;
    private Speaker host;

    private String description;

    private String startDate;
    private String endDate;

    private String location;
    private String link;
    private String dctvChannel;
    private String includes;


    // State
    private int isNew;
    @SerializedName("starred")
    private int isBookmarked;


    public static Item CursorToItem(Gson gson, Cursor cursor) {
        JSONObject object = new JSONObject();

        int totalColumn = cursor.getColumnCount();

        for (int i = 0; i < totalColumn; i++) {
            try {
                object.put(cursor.getColumnName(i), cursor.getString(i));
            } catch (Exception e) {
                Logger.e(e, "Failed to convert Cursor into JSONObject.");
            }
        }

        return gson.fromJson(object.toString(), Item.class);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getHost() {
        return hostString;
    }

    public String getBegin() {
        return startDate;
    }

    public String getEnd() {
        return endDate;
    }

    public String getDate() {
        return startDate.substring(0, 11);
    }

    public String getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }

    public String getLink() {
        return link;
    }


    // State

    public boolean isTool() {
        return includes != null && includes.contains("Tool");
    }

    public boolean isExploit() {
        return includes != null && includes.contains("Exploit");
    }

    public boolean isDemo() {
        return includes != null && includes.contains("Demo");
    }

    public boolean isBookmarked() {
        return isBookmarked == BOOKMARKED;
    }

    public boolean isUnbookmarked() {
        return !isBookmarked();
    }

    public boolean isNew() {
        return isNew == NEW;
    }


    // Date
    private DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }


    private Date getDateObject(String dateStr) {
        DateFormat readFormat = getDateFormat();

        Date date = null;
        try {
            date = readFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public Date getBeginDateObject() {
        return getDateObject(startDate);
    }

    Date getEndDateObject() {
        return getDateObject(endDate);
    }

    public boolean hasExpired() {
        Date date = App.Companion.getCurrentDate();
        Date end = getEndDateObject();
        if (end == null) {
            Logger.e(title + " -- End Date is null!");
            return false;
        }

        return date.after(end);
    }

    public boolean hasBegin() {
        Date date = App.Companion.getCurrentDate();
        return date.after(getBeginDateObject());
    }

    private Calendar getCalendar() {
        if (TextUtils.isEmpty(getBegin()) || TextUtils.isEmpty(getDate()))
            return null;

        Calendar calendar = Calendar.getInstance();
        String[] split = getDate().split("-");

        calendar.set(Calendar.YEAR, Integer.parseInt(split[0]));
        calendar.set(Calendar.MONTH, Integer.parseInt(split[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[2]));

        split = getBegin().split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(split[1]));
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    public long getNotificationTimeInMillis() {
        Calendar calendar = getCalendar();
        if (calendar == null)
            return 0;
        return calendar.getTimeInMillis() - 1200000;
    }

    public String getDateStamp() {
        Date date = getBeginDateObject();
        return getDateStamp(date);
    }

    public static String getDateStamp(Date date) {
        if (date == null) return "";

        return App.Companion.getRelativeDateStamp(date);
    }

    //

    public ContentValues getContentValues(Gson gson) {
        ContentValues values = new ContentValues();

        String json = gson.toJson(this);
        try {
            JSONObject object = new JSONObject(json);

            Iterator<String> keys = object.keys();
            String key;
            while (keys.hasNext()) {
                key = keys.next();
                if (!key.equals("starred"))
                    values.put(key, object.getString(key));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return values;
    }


    // Actions / Modifiers

    private void setBookmarked() {
        isBookmarked = BOOKMARKED;
    }

    private void setUnbookmarked() {
        isBookmarked = UNBOOKMARKED;
    }

    public void toggleBookmark() {
        if (isBookmarked()) {
            setUnbookmarked();
        } else {
            setBookmarked();
        }
    }


    public void setLocation( String location ) {
        this.location = location;
    }

    public void setType( String type ) {
        this.type = type;
    }

}
