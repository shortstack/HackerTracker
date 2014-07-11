package com.shortstack.hackertracker.Listener;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public interface AsyncTaskCompleteListener<T> {

    public void onTaskComplete(T result);
}