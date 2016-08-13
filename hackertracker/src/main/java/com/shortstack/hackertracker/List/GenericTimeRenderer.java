package com.shortstack.hackertracker.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GenericTimeRenderer extends Renderer<Date> {

    private TextView view;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        view = (TextView) inflater.inflate(R.layout.row_header_time, parent, false);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        Date content = getContent();
        Date currentDate = HackerTrackerApplication.getApplication().getCurrentDate();


        String stamp = Default.getTimeStamp(getContext(), content);

        if( content.getDay() == currentDate.getDay() && content.after(currentDate) ) {
            long hourDiff = getDateDiff(currentDate, content, TimeUnit.HOURS);
            if( hourDiff >= 1 ) {
                stamp = stamp.concat(" [in " + hourDiff + "hrs]");
            } else {
                long dateDiff = getDateDiff(currentDate, content, TimeUnit.MINUTES);
                stamp = stamp.concat(" [in " + dateDiff + "mins]");
            }
        }


        view.setText(stamp);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
