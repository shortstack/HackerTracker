package com.shortstack.hackertracker.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;

import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;

import org.parceler.Parcel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:20 AM
 * Description:
 */

@Parcel
public class Default implements Serializable {

    private int id;
    private String type;
    private String date;
    private String title;
    private String who;
    private String description;
    private String begin;
    private String end;
    private String location;
    private String link;
    private Integer starred;
    private String image;
    private Integer is_new;
    private Integer demo;
    private Integer tool;
    private Integer exploit;

    public Integer getStarred() {
        return starred;
    }

    public void setStarred(Integer starred) {
        this.starred = starred;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String who) {
        this.who = who;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description.replace("\\\"", "\"");
    }

    public String getName() {
        return who;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public int getCategoryColorPosition() {
        int count = 0;
        switch (getType()){
            case Constants.TYPE_EVENT:
                count++;
            case Constants.TYPE_CONTEST:
                count++;
            case Constants.TYPE_SPEAKER:
                count++;
            case Constants.TYPE_KIDS:
                count++;
            case Constants.TYPE_PARTY:
                count++;
            case Constants.TYPE_SKYTALKS:
                count++;
            case Constants.TYPE_DEMOLAB:
                count++;
            case Constants.TYPE_WORKSHOP:
                count++;
        }
        return count;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isNew() {
        return is_new != null && is_new == 1;
    }

    public void setIsNew(Integer is_new) {
        this.is_new = is_new;
    }

    public Integer getDemo() {
        return demo;
    }

    public void setDemo(Integer demo) {
        this.demo = demo;
    }

    public Integer getTool() {
        return tool;
    }

    public void setTool(Integer tool) {
        this.tool = tool;
    }

    public Integer getExploit() {
        return exploit;
    }

    public void setExploit(Integer exploit) {
        this.exploit = exploit;
    }

    public Default() {

    }

    public Default(int id, String title, String type, String description, String location, String who, String end, String begin, String link, String image, int demo, int tool, int exploit, int starred, int is_new) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.who = who;
        this.location = location;
        this.begin = begin;
        this.end = end;
        this.link = link;
        this.image = image;
        this.exploit = exploit;
        this.demo = demo;
        this.tool = tool;
        this.starred = starred;
        this.is_new = is_new;
    }

    public boolean isTool() {
        return getTool() == 1;
    }

    public boolean isExploit() {
        return getExploit() == 1;
    }

    public boolean isDemo() {
        return getDemo() == 1;
    }

    public boolean isStarred() {
        return getStarred() == 1;
    }

    public String getTimeStamp(Context context) {
        // No start time, return TBA.
        if (getBegin().equals(""))
            return context.getResources().getString(R.string.tba);

        String time = "";

        if (SharedPreferencesUtil.shouldShowMilitaryTime()) {
            time = getBegin();
        } else {
            Date date = getBeginDateObject();
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

        if (SharedPreferencesUtil.shouldShowMilitaryTime()) {
            writeFormat = new SimpleDateFormat("HH:mm");
        } else {
            writeFormat = new SimpleDateFormat("h:mm aa");
        }

        time = writeFormat.format(date);

        return time;
    }

    public String getDateStamp() {
        Date date = getBeginDateObject();
        return getDateStamp(date);
    }

    public static String getDateStamp(Date date) {
        String time = "";

        if (date != null) {
            @SuppressLint("SimpleDateFormat")
            DateFormat writeFormat = new SimpleDateFormat("EEEE");
            time = writeFormat.format(date);
        }

        return time;
    }


    public Date getBeginDateObject() {
        String dateStr = getDate() + " " + getBegin();
        return getDateObject(dateStr);
    }

    private Date getEndDateObject() {
        String dateStr = getDate() + " " + getEnd();
        return getDateObject(dateStr);
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

    @NonNull
    @SuppressLint("SimpleDateFormat")
    private DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public String getFullTimeStamp(Context context) {
        Date begin = getBeginDateObject();
        Date end = getEndDateObject();

        return String.format(context.getString(R.string.timestamp_full), getDateStamp(), getTimeStamp(context, begin), getTimeStamp(context, end));
    }

    public String getDisplayTitle() {
        return/* "[" + getType() + "] " +*/ getTitle();
    }

    public boolean hasHost() {
        return getName() != null && getName().length() > 0;
    }

    public boolean hasDescription() {
        return getDescription() != null && getDescription().length() > 0;
    }
}
