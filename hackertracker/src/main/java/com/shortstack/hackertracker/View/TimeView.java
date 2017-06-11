package com.shortstack.hackertracker.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Event.RefreshTimerEvent;
import com.shortstack.hackertracker.Model.ItemViewModel;
import com.shortstack.hackertracker.R;
import com.squareup.otto.Subscribe;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimeView extends LinearLayout {

    @BindView(R.id.header)
    TextView header;

    @BindView(R.id.subheader)
    TextView subheader;

    private Date mDate;

    public TimeView(Context context) {
        super(context);
        init();
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        inflate();
    }

    private void inflate() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.row_header_time, null);
        ButterKnife.bind(this, view);

        addView(view);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if( App.Companion.getApplication() != null )
            App.Companion.getApplication().registerBusListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if( App.Companion.getApplication() != null )
            App.Companion.getApplication().unregisterBusListener(this);
    }

    @Subscribe
    public void onRefreshTimeEvent(RefreshTimerEvent event) {
        Date currentDate = App.Companion.getCurrentDate();
        updateSubheader(currentDate);
    }

    public void setDate(Date date) {
        mDate = date;

        render();
    }

    public void render() {
        Date currentDate = App.Companion.getCurrentDate();

        header.setText(ItemViewModel.getTimeStamp(getContext(), mDate));

        updateSubheader(currentDate);
    }

    private void updateSubheader(Date currentDate) {
        if (mDate.getDay() == currentDate.getDay() && mDate.after(currentDate)) {
            subheader.setVisibility(View.VISIBLE);

            String stamp = "";

            long hourDiff = getDateDiff(currentDate, mDate, TimeUnit.HOURS);
            if (hourDiff >= 1) {
                stamp = stamp.concat("in " + hourDiff + " hr" + (hourDiff > 1 ? "s" : ""));
            } else {
                long dateDiff = getDateDiff(currentDate, mDate, TimeUnit.MINUTES);
                stamp = stamp.concat("in " + dateDiff + " min" + (dateDiff > 1 ? "s" : ""));
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
