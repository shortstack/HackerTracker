package com.shortstack.hackertracker.Api.Impl;

import android.content.Context;

import com.shortstack.hackertracker.Api.ApiException;
import com.shortstack.hackertracker.Api.ApiHelper;
import com.shortstack.hackertracker.Api.SyncService;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;
import com.shortstack.hackertracker.Model.OfficialList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Whitney Champion on 5/12/15.
 */
public class SyncServiceImpl implements SyncService {

    private static final String EXCEPTION_MESSAGE = "An error occurred.";

    @Override
    public void syncDatabase(Context context,
                             AsyncTaskCompleteListener listener) throws ApiException {
        // create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();

        // try to make call
        String url = Constants.OFFICIAL_SCHEDULE;
        try {
            ApiHelper.get(url, context, OfficialList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }
}
