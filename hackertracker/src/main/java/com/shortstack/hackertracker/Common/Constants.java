package com.shortstack.hackertracker.Common;

/**
 * Created by whitneychampion on 6/22/14.
 */
public interface Constants {

    public static String API_URL = "http://dcib.melloman.net";
    public static String API_ERROR_MESSAGE = "\"errorMessage\":";

    public static int TYPE_SPEAKER = 1;
    public static int TYPE_CONTEST = 2;
    public static int TYPE_EVENT = 3;
    public static int TYPE_PARTY = 4;
    public static int TYPE_VENDOR = 5;

    public String DAY_0 = "Wed, Aug 6";
    public String DAY_1 = "Thurs, Aug 7";
    public String DAY_2 = "Fri, Aug 8";
    public String DAY_3 = "Sat, Aug 9";
    public String DAY_4 = "Sun, Aug 10";

    public String[] COLUMN_NAMES = {"Title","Speaker Name","Start Time","End Time","Date","Location"};

    String UTF8 = "utf-8";
}