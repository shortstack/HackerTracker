package com.shortstack.hackertracker.Event;

public class FavoriteEvent {

    private final int mItem;

    public FavoriteEvent(int item) {
        mItem = item;
    }

    public int getItem() {
        return mItem;
    }
}
