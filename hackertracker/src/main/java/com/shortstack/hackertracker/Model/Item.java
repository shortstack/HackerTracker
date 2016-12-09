package com.shortstack.hackertracker.Model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.R;

import org.parceler.Parcel;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:20 AM
 * Description:
 */

@Parcel
public class Item implements Serializable {

    private static final int EMPTY_CATEGORY = 0;

    public static final int BOOKMARKED = 1;
    public static final int UNBOOKMARKED = 0;

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
        if (!TextUtils.isEmpty(title) && title.endsWith("\n"))
            return title.substring(0, title.indexOf("\n"));
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
        int count = -1;

        if (TextUtils.isEmpty(getType()))
            return EMPTY_CATEGORY;

        switch (getType()) {
            case Constants.TYPE_WORKSHOP:
                count++;
            case Constants.TYPE_DEMOLAB:
                count++;
            case Constants.TYPE_PARTY:
                count++;
            case Constants.TYPE_CONTEST:
                count++;
            case Constants.TYPE_KIDS:
                count++;
            case Constants.TYPE_VILLAGE:
                count++;
            case Constants.TYPE_EVENT:
                count++;
            case Constants.TYPE_SKYTALKS:
                count++;
            case Constants.TYPE_SPEAKER:
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

    public Item() {

    }

    public Item(int id, String title, String type, String description, String location, String who, String end, String begin, String link, String image, int demo, int tool, int exploit, int starred, int is_new) {
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
        return getTool() != null && getTool() == 1;
    }

    public boolean isExploit() {
        return getExploit() != null && getExploit() == 1;
    }

    public boolean isDemo() {
        return getDemo() != null && getDemo() == 1;
    }

    public boolean isBookmarked() {
        return getStarred() != null && getStarred() == 1;
    }

    public boolean isUnbookmarked() {
        return !isBookmarked();
    }

    public void setBookmarked() {
        setStarred(BOOKMARKED);
    }

    public void setUnbookmarked() {
        setStarred(UNBOOKMARKED);
    }

    public String getTimeStamp(Context context) {
        // No start time, return TBA.
        if (TextUtils.isEmpty(getBegin()))
            return context.getResources().getString(R.string.tba);

        String time = "";

        if (App.getStorage().shouldShowMilitaryTime()) {
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

        if (App.getStorage().shouldShowMilitaryTime()) {
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

    public boolean hasExpired() {
        Date date = App.getApplication().getCurrentDate();
        return date.after(getEndDateObject());
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

    public String getDisplayTitle() {
        return/* "[" + getType() + "] " +*/ getTitle();
    }

    public boolean hasHost() {
        return !TextUtils.isEmpty(getName());
    }

    public boolean hasDescription() {
        return !TextUtils.isEmpty(getDescription());
    }

    public boolean hasUrl() {
        return !TextUtils.isEmpty(link);
    }

    public String getPrettyUrl() {
        String url = getLink().toLowerCase();

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

        if( url.length() < getLink().length() ) {
            url = url.concat("...");
        }


        return url;
    }

    public String getDetailsDescription(Context context) {
        String result = "";

        result = result.concat(getFullTimeStamp(context) + "\n");
        result = result.concat(getLocation() + "\n");
        result = result.concat(getType());


        return result;
    }
}
