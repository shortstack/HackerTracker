package com.shortstack.hackertracker.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Event.FavoriteEvent;
import com.shortstack.hackertracker.Event.UpdateListContentsEvent;
import com.shortstack.hackertracker.Fragment.HackerTrackerFragment;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.Model.Filter;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.FilterView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GenericRowFragment extends HackerTrackerFragment {

    @Bind(R.id.list)
    public RecyclerView list;

    private GenericRowAdapter adapter;

    @Bind(R.id.empty)
    View empty;


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

    @Subscribe
    public void handleFavoriteEvent(FavoriteEvent event ) {
        adapter.notifyItemUpdated( event.getItem() );
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

        List<Default> events = getEvents(filter);
        List<Object> objects = addTimeDividers(events);

        adapter.addAll(objects);
        adapter.notifyDataSetChanged();

        //scrollToCurrentTime();
    }

    private void scrollToCurrentTime() {
        list.getLayoutManager().scrollToPosition(findCurrentPositionByTime());
    }

    private int findCurrentPositionByTime() {
        List<Default> collection = adapter.getCollection();
        Date currentDate = App.getApplication().getCurrentDate();

        for (int i = 0; i < collection.size(); i++) {
            Object object = collection.get(i);

            if( object instanceof Default ) {

                Date beginDateObject = ((Default) object).getBeginDateObject();
                if( beginDateObject.after(currentDate)) {
                    for (int i1 = i - 1; i1 >= 0; i1--) {
                        if( !(object instanceof String ) ) {
                            return i1;
                        }
                    }
                    return i;
                }
            }
        }

        return 0;
    }

    protected List<Default> getEvents( Filter filter ) {
        if( filter.getTypesSet().size() == 0 ) {
            empty.setVisibility(View.VISIBLE);
            return Collections.emptyList();
        }

        empty.setVisibility(View.GONE);
        List<Default> events;
        events = getItemByDate(filter.getTypesArray());
        return events;
    }

    private List<Object> addTimeDividers(List<Default> events) {
        ArrayList<Object> result = new ArrayList<>();

        if (events.size() == 0)
            return result;

        result.add(events.get(0).getDateStamp());
        result.add(events.get(0).getBeginDateObject());

        for (int i = 0; i < events.size() - 1; i++) {
            Default current = events.get(i);

            result.add(current);

            Default next = events.get(i + 1);
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