package com.shortstack.hackertracker.Utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.shortstack.hackertracker.BuildConfig;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeHelper {

    public long DEBUG_TIME_EXTRA = 0;


    private String today;
    private String tomorrow;

    private final Context mContext;

    public TimeHelper(Context context) {
        mContext = context;

        initStrings();
    }

    private void initStrings() {
        today = mContext.getString(R.string.today);
        tomorrow = mContext.getString(R.string.tomorrow);
    }

    public Date getCurrentDate() {
        // TODO: Uncomment when not forcing the time.
        if (BuildConfig.DEBUG) {
            Date date = new Date();
            date.setTime(Constants.DEBUG_FORCE_TIME_DATE + DEBUG_TIME_EXTRA);
            return date;
        }
        return new Date();
    }

    public Calendar getCurrentCalendar() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(getCurrentDate());
        return instance;
    }

    public boolean isDateToday( Date date ) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        Calendar cal2 = getCurrentCalendar();
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }


    public String getTodayString() {
        return today;
    }

    public String getTomorrowString() {
        return tomorrow;
    }

    public boolean isDateTomorrow(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        Calendar cal2 = getCurrentCalendar();
        cal2.roll(Calendar.DAY_OF_YEAR, true);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public String getRelativeDateStamp(Date date) {
        if (isDateToday(date))
            return getTodayString();

        if (isDateTomorrow(date))
            return getTomorrowString();

        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("EEEE");
        return format.format(date);
    }
}
