package com.shortstack.hackertracker.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GenericTimeRenderer extends Renderer<Date> {

    @Bind(R.id.header)
    TextView header;

    @Bind(R.id.subheader)
    TextView subheader;


    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_header_time, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        Date content = getContent();
        Date currentDate = App.getApplication().getCurrentDate();


        header.setText(Default.getTimeStamp(getContext(), content));

        if (content.getDay() == currentDate.getDay() && content.after(currentDate)) {
            subheader.setVisibility(View.VISIBLE);

            String stamp = "";

            long hourDiff = getDateDiff(currentDate, content, TimeUnit.HOURS);
            if (hourDiff >= 1) {
                stamp = stamp.concat("in " + hourDiff + " hr" + ( hourDiff > 1 ? "s":""));
            } else {
                long dateDiff = getDateDiff(currentDate, content, TimeUnit.MINUTES);
                stamp = stamp.concat("in " + dateDiff + " min"+ ( dateDiff > 1 ? "s":""));
            }

            subheader.setText(stamp);
        } else {
            subheader.setVisibility(View.GONE);
        }


    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
