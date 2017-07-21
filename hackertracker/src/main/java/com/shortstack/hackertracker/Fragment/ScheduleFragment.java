package com.shortstack.hackertracker.Fragment;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Event.RefreshTimerEvent;
import com.shortstack.hackertracker.Event.UpdateListContentsEvent;
import com.shortstack.hackertracker.List.ScheduleItemAdapter;
import com.shortstack.hackertracker.Model.Filter;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.FilterView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ScheduleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.list)
    public RecyclerView list;

    private ScheduleItemAdapter adapter;

    @BindView(R.id.empty)
    View empty;

    @BindView(R.id.tutorial)
    View tutorial;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipe;

    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }


    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            //Logger.d("Updating list!");
//            if( App.Companion.getApplication().DEBUG_TIME_EXTRA == Constants.TIMER_INTERVAL_FIVE_MIN ) {
//                NetworkController cont = App.Companion.getApplication().getNetworkController();
//                cont.syncInBackground();
//            }

            App.Companion.getApplication().postBusEvent(new RefreshTimerEvent());
            if (adapter != null) {
                adapter.notifyTimeChanged();
                updateFeedErrors();
            }

        }
    };
    private Timer mTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.Companion.getApplication().registerBusListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.Companion.getApplication().unregisterBusListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        Date currentDate = App.Companion.getCurrentDate();
        long time = currentDate.getTime();

        if (App.Companion.getStorage().shouldRefresh(time)) {
            mHandler.obtainMessage(1).sendToTarget();
        }

        time = time % Constants.TIMER_INTERVAL;


        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.obtainMessage(1).sendToTarget();
            }
        }, time, Constants.TIMER_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTimer.cancel();

        App.Companion.getStorage().setLastRefresh(App.Companion.getCurrentDate().getTime());
    }

    @Subscribe
    public void handleUpdateListContentsEvent(UpdateListContentsEvent event) {
        refreshContents();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(getContentView(), container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        list.setLayoutManager(layout);

        swipe.setOnRefreshListener(this);
        adapter = new ScheduleItemAdapter();
        list.setAdapter(adapter);

        refreshContents();
        
        return rootView;
    }

    private boolean hasFilters() {
        Filter filter = App.Companion.getStorage().getFilter();
        return !filter.getTypesSet().isEmpty();
    }

    private boolean hasScheduleItems() {
        return !adapter.getCollection().isEmpty();
    }

    private void updateFeedErrors() {

        tutorial.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);

//        if (!hasFilters()) {
//            tutorial.setVisibility(View.VISIBLE);
//        } else
            if (!hasScheduleItems()) {
            empty.setVisibility(View.VISIBLE);
        }
    }

    private void refreshContents() {
        Logger.d("Refreshing database contents.");

        adapter.clear();

        Filter filter = App.Companion.getStorage().getFilter();
        List<Item> events = getEvents(filter);

        List<Object> objects = addTimeDividers(events);
        adapter.addAll(objects);

        updateFeedErrors();


        adapter.notifyDataSetChanged();

        if (App.Companion.getStorage().showExpiredEvents())
            scrollToCurrentTime();
    }

    private void scrollToCurrentTime() {
        list.getLayoutManager().scrollToPosition(findCurrentPositionByTime());
    }

    private int findCurrentPositionByTime() {
        List<Item> collection = adapter.getCollection();
        Date currentDate = App.Companion.getCurrentDate();

        for (int i = 0; i < collection.size(); i++) {
            Object object = collection.get(i);

            if (object instanceof Item) {

                Date beginDateObject = ((Item) object).getBeginDateObject();
                if (beginDateObject.after(currentDate)) {
                    for (int i1 = i - 1; i1 >= 0; i1--) {
                        if (!(object instanceof String)) {
                            return i1;
                        }
                    }
                    return i;
                }
            }
        }

        return 0;
    }

    protected List<Item> getEvents(Filter filter) {
//        if( !hasFilters() ) {
//            return Collections.emptyList();
//        }

        try {
            List<Item> events;
            events = App.Companion.getApplication().getDatabaseController().getItemByDate(filter.getTypesArray());
            return events;
        } catch (SQLiteException ex) {
            Logger.e(ex, "Could not open the database.");
            return Collections.emptyList();
        }
    }

    private List<Object> addTimeDividers(List<Item> events) {
        ArrayList<Object> result = new ArrayList<>();

        if (events.size() == 0)
            return result;

        result.add(events.get(0).getDateStamp());
        result.add(events.get(0).getBeginDateObject());

        for (int i = 0; i < events.size() - 1; i++) {
            Item current = events.get(i);

            result.add(current);

            Item next = events.get(i + 1);
            if (!current.getDate().equals(next.getDate())) {
                result.add(next.getDateStamp());
            }

            if (!current.getBegin().equals(next.getBegin())) {
                result.add(next.getBeginDateObject());
            }
        }

        result.add(events.get(events.size() - 1));

        return result;
    }

    protected int getContentView() {
        return R.layout.fragment_schedule;
    }

    //@OnClick(R.id.filter)
    public void showFilters() {
        Filter filter = App.Companion.getStorage().getFilter();

        final FilterView view = new FilterView(getContext(), filter);
        MaterialAlert.create(getContext()).setTitle(getString(R.string.alert_filter_title)).setView(view).setBasicNegativeButton().setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Filter filter = view.save();
                App.Companion.getApplication().getAnalyticsController().tagFiltersEvent(filter);
                refreshContents();
            }
        }).show();
    }


    @Override
    public void onRefresh() {
        App.Companion.getApplication().getNetworkController().syncInForeground(getContext());
        swipe.setRefreshing(false);
    }
}