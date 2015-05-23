package com.shortstack.hackertracker.Listener;

/**
 * Created by Whitney Champion on 3/29/14.
 */
public interface AsyncTaskCompleteListener<T> {

    void onTaskComplete(T result);
}