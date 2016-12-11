package com.shortstack.hackertracker.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.BuildConfig;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Event.FavoriteEvent;
import com.shortstack.hackertracker.Event.RefreshTimerEvent;
import com.shortstack.hackertracker.Event.UpdateListContentsEvent;
import com.shortstack.hackertracker.List.GenericRowAdapter;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GenericRowFragment extends HackerTrackerFragment {

    @Bind(R.id.list)
    public RecyclerView list;

    private GenericRowAdapter adapter;

    @Bind(R.id.empty)
    View empty;


    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
//            Logger.d(">>Refreshing<<");
            App.getApplication().DEBUG_TIME_EXTRA += Constants.TIMER_INTERVAL_FIVE_MIN;

            App.getApplication().postBusEvent( new RefreshTimerEvent() );
            if (adapter != null)
                adapter.notifyTimeChanged();

        }
    };
    private Timer mTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getApplication().registerBusListener(this);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        App.getApplication().unregisterBusListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        Date currentDate = App.getApplication().getCurrentDate();
        long time = currentDate.getTime();

        if (App.getStorage().shouldRefresh(time)) {
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
        if(BuildConfig.DEBUG) {
            App.getApplication().DEBUG_TIME_EXTRA += Constants.DEBUG_PAUSE_TIME_SKIP;
        }
        App.getStorage().setLastRefreshTimer(App.getApplication().getCurrentDate().getTime());
    }

    @Subscribe
    public void handleFavoriteEvent(FavoriteEvent event) {
        adapter.notifyItemUpdated(event.getItem());
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

        adapter = new GenericRowAdapter();
        list.setAdapter(adapter);

        refreshContents();

        return rootView;
    }

    private void refreshContents() {
        adapter.clear();

        Filter filter = App.getStorage().getFilter();

        List<Item> events = getEvents(filter);
        List<Object> objects = addTimeDividers(events);

        adapter.addAll(objects);
        adapter.notifyDataSetChanged();

        //scrollToCurrentTime();
    }

    private void scrollToCurrentTime() {
        list.getLayoutManager().scrollToPosition(findCurrentPositionByTime());
    }

    private int findCurrentPositionByTime() {
        List<Item> collection = adapter.getCollection();
        Date currentDate = App.getApplication().getCurrentDate();

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
        if (filter.getTypesSet().size() == 0) {
            empty.setVisibility(View.VISIBLE);
            return Collections.emptyList();
        }

        empty.setVisibility(View.GONE);
        List<Item> events;
        events = getItemByDate(filter.getTypesArray());
        return events;
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

    @OnClick(R.id.filter)
    public void showFilters() {
        Filter filter = App.getStorage().getFilter();

        final FilterView view = new FilterView(getContext(), filter);
        MaterialAlert.create(getContext()).setTitle("Filter").setView(view).setBasicNegativeButton().setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Filter filter = view.save();
                refreshContents();
            }
        }).show();
    }
}