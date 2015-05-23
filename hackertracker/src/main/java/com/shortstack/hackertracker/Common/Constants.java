package com.shortstack.hackertracker.Common;

/**
 * Created by whitneychampion on 6/22/14.
 */
public interface Constants {

    String API_URL = "http://short-stack.net/api";
    String API_ERROR_MESSAGE = "\"errorMessage\":";

    int TYPE_SPEAKER = 1;
    int TYPE_CONTEST = 2;
    int TYPE_EVENT = 3;
    int TYPE_PARTY = 4;
    int TYPE_VENDOR = 5;

    String DAY_0 = "Wed, Aug 5";
    String DAY_1 = "Thurs, Aug 6";
    String DAY_2 = "Fri, Aug 7";
    String DAY_3 = "Sat, Aug 8";
    String DAY_4 = "Sun, Aug 9";

    String[] COLUMN_NAMES = {"Title","Speaker Name","Start Time","End Time","Date","Location"};

}