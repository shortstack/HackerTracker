package com.shortstack.hackertracker.Model;

public class Navigation {

    private String mTitle;
    private String mDescription;
    private Class mActivity;

    public Navigation(String title, String description, Class activity) {
        mTitle = title;
        mDescription = description;
        mActivity = activity;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public Class getActivity() {
        return mActivity;
    }
}
