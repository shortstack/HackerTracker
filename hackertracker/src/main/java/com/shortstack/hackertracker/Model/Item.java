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
import java.util.Random;

public class Item implements Serializable {

    static final int BOOKMARKED = 1;
    static final int UNBOOKMARKED = 0;

    private static final int TOOL = 1;
    private static final int EXPLOIT = 1;
    private static final int DEMO = 1;
    private static final int NEW = 1;

    private int id;
    public String type;
    private String date;
    private String title;
    @SerializedName("who")
    private String host;
    private String description;
    private String begin;
    private String end;
    public String location;
    private String link;


    // State
    private int isNew;
    @SerializedName("starred")
    private int isBookmarked;
    @SerializedName("demo")
    private int isDemo;
    @SerializedName("tool")
    private int isTool;
    @SerializedName("exploit")
    private int isExploit;


    // Generators


    public Item() {
        super();
        id = new Random().nextInt();
        title = "My event!";
        location = "Debug Menus";
        type = "Party";
        begin = "10:00";
        end = "10:50";
        date = "2017-07-07";

    }


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
        return host;
    }

    public String getBegin() {
        return begin;
    }

    public String getEnd() {
        return end;
    }

    public String getDate() {
        return date;
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
        return isTool == TOOL;
    }

    public boolean isExploit() {
        return isExploit == EXPLOIT;
    }

    public boolean isDemo() {
        return isDemo == DEMO;
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
        return new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
        String dateStr = getDate() + " " + getBegin();
        return getDateObject(dateStr);
    }

    Date getEndDateObject() {
        String dateStr = getDate() + " " + getEnd();
        return getDateObject(dateStr);
    }

    public boolean hasExpired() {
        Date date = App.getApplication().getTimeHelper().getCurrentDate();
        if (getEndDateObject() == null) {
            Logger.e(title + " -- End Date is null!");
            return false;
        }

        return date.after(getEndDateObject());
    }

    public boolean hasBegin() {
        Date date = App.getApplication().getTimeHelper().getCurrentDate();
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

        return App.getApplication().getTimeHelper().getRelativeDateStamp(date);
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


        if (id == 7) {
            Logger.d(values.toString());
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

}
