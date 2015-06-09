package com.shortstack.hackertracker.Api.Impl;

import android.content.Context;

import com.shortstack.hackertracker.Api.ApiException;
import com.shortstack.hackertracker.Api.ApiHelper;
import com.shortstack.hackertracker.Api.ContestService;
import com.shortstack.hackertracker.Api.SpeakerService;
import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;
import com.shortstack.hackertracker.Model.ContestList;
import com.shortstack.hackertracker.Model.OfficialList;
import com.shortstack.hackertracker.Model.SpeakerList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Whitney Champion on 5/12/15.
 */
public class ContestServiceImpl implements ContestService {

    private static final String EXCEPTION_MESSAGE = "An error occurred.";

    @Override
    public void findContestById(String contestId, Context context,
                             AsyncTaskCompleteListener listener) throws ApiException {
        // create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();

        // try to make call
        String url = ApiHelper.FIND_CONTEST + "?id=" + contestId;
        try {
            ApiHelper.get(url, context, ContestList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void getAllContests(Context context,
                                AsyncTaskCompleteListener listener) throws ApiException {
        // create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();

        // try to make call
        String url = ApiHelper.FIND_CONTEST;
        try {
            ApiHelper.get(url, context, OfficialList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }
}
