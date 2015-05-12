package com.shortstack.hackertracker.Api.Impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.shortstack.hackertracker.Api.ApiException;
import com.shortstack.hackertracker.Api.ApiHelper;
import com.shortstack.hackertracker.Api.SpeakerService;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;
import com.shortstack.hackertracker.Model.Speaker;
import com.shortstack.hackertracker.Model.SpeakerList;
import com.shortstack.hackertracker.Utils.SharedPreferencesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Whitney Champion on 5/12/15.
 */
public class SpeakerServiceImpl implements SpeakerService {

    private static final String EXCEPTION_MESSAGE = "An error occurred.";

    @Override
    public void findSpeakerById(String userId, Context context,
                             AsyncTaskCompleteListener listener) throws ApiException {
        // create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();

        // try to make call
        String url = ApiHelper.FIND_SPEAKER + "?limit=1&id=" + userId;
        try {
            ApiHelper.get(url, context, SpeakerList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }

    @Override
    public void getAllSpeakers(Context context,
                                AsyncTaskCompleteListener listener) throws ApiException {
        // create headers
        Map<String, String> extraHeaderParameters = new HashMap<String, String>();

        // try to make call
        String url = ApiHelper.FIND_SPEAKER;
        try {
            ApiHelper.get(url, context, SpeakerList.class, listener, extraHeaderParameters);
        } catch (Exception e) {
            throw new ApiException(EXCEPTION_MESSAGE, null);
        }

    }
}
