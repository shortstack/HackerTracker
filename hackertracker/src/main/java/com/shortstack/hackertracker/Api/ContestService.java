package com.shortstack.hackertracker.Api;

import android.content.Context;

import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;

/**
 * Created by Whitney Champion on 5/12/15.
 */
public interface ContestService {

    void findContestById(String contestId, Context context,
                         AsyncTaskCompleteListener listener) throws ApiException;

    void getAllContests(Context context,
                        AsyncTaskCompleteListener listener) throws ApiException;

}
