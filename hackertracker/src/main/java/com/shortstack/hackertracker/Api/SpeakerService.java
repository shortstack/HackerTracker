package com.shortstack.hackertracker.Api;

import android.content.Context;

import com.shortstack.hackertracker.Listener.AsyncTaskCompleteListener;

/**
 * Created by Whitney Champion on 5/12/15.
 */
public interface SpeakerService {

    void findSpeakerById(String speakerId, Context context,
                      AsyncTaskCompleteListener listener) throws ApiException;

    void getAllSpeakers(Context context,
                         AsyncTaskCompleteListener listener) throws ApiException;

}
