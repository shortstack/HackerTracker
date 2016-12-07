package com.shortstack.hackertracker.Event;

import com.orhanobut.logger.Logger;

public class FavoriteEvent {

    private final int mItem;

    public FavoriteEvent(int item) {
        Logger.d("Created event with " + item);
        mItem = item;
    }

    public int getItem() {
        return mItem;
    }
}
