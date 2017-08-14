package com.shortstack.hackertracker.Model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Application.App;

import org.json.JSONArray;
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

    private int index;
    @SerializedName("entry_type")
    private String type;

    private String title;

    @SerializedName("who")
    private Speakers.Speaker[] host;

    private String description;

    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;

    private String location;
    private String link;
    private String dctvChannel;
    private String includes;

    @SerializedName("updated_at")
    private String updatedAt;


    // State
    //private int isNew;
    @SerializedName("bookmarked")
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

        try {
            String who = object.getString("who");
            object.remove("who");
            object.put("who", new JSONArray(who));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return gson.fromJson(object.toString(), Item.class);
    }

    public int getIndex() {
        return index;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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

    public String getUpdatedAt() {
        return updatedAt;
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

        if (end == null || date == null ) {
            Logger.e("Cannot check if item is expired. Something is null.");
            CrashlyticsCore core = Crashlytics.getInstance().core;
            if( core != null ) {
                core.log("Could not check if item is expired, some date is null.");
                core.log("Date null? == " + (date==null) + ", End null? == " + (end==null));
                core.logException(new NullPointerException());
            }
            return false;
        }

        return date.after(end);
    }

    public boolean hasBegin() {
        Date date = App.Companion.getCurrentDate();
        return date.after(getBeginDateObject());
    }

    private Calendar getCalendar() {
        if (TextUtils.isEmpty(getBegin()) || TextUtils.isEmpty(getDate())) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getBeginDateObject());
        
        return calendar;
    }

    public int getNotificationTime() {
        Calendar current = Calendar.getInstance();
        Calendar calendar = getCalendar();
        if (calendar == null) {
            return 0;
        }
        return (int)((calendar.getTimeInMillis() - current.getTimeInMillis()) / 1000);
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
                if (!key.equals("bookmarked"))
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

    public Speakers.Speaker[] getSpeakers() {
        return host;
    }

    @Override
    public String toString() {
        return "{ id: " + index + ", title: \"" + title + "\", location: \"" + location + "\", \"type: " + type + "\" }";
    }
}
