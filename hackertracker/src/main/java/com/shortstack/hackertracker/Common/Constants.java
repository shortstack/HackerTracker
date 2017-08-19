package com.shortstack.hackertracker.Common;

import com.shortstack.hackertracker.BuildConfig;

public interface Constants {

    String DEFCON_DATABASE_NAME = "DEFCON";
    String TOORCON_DATABASE_NAME = "TOORCON";

    String API_URL_BASE = "https://info.defcon.org/json/";

    long DEBUG_FORCE_TIME_DATE = 1501355835000L;
    long TIMER_INTERVAL_FIVE_MIN = 300000;
    long TIMER_INTERVAL_DEBUG = 5000;
    long DEBUG_PAUSE_TIME_SKIP = TIMER_INTERVAL_FIVE_MIN * 30;

    long TIMER_INTERVAL = BuildConfig.DEBUG ? TIMER_INTERVAL_DEBUG : TIMER_INTERVAL_FIVE_MIN;

    int BOOKMARKED = 1;
    int UNBOOKMARKED = 0;

    int SYNC_MODE_AUTO = 0;
    int SYNC_MDOE_MANUAL = 1;

    String API_URL = "https://s3.amazonaws.com/defcon-api";
    String API_ERROR_MESSAGE = "\"errorMessage\":";
    String OFFICIAL_SCHEDULE = "/schedule-full.json";

    String FEEDBACK_EMAIL = "chrisporter0111@gmail.com";

    String UBER_CLIENT_ID = "q1eUCeC1ZenbGmQD2vb0MytALvboEHhD";

    String UBER_PARIS = "Paris";
    String UBER_BALLYS = "Bally's";
    String UBER_TUSCANY = "Tuscany Suites & Casino";
    String UBER_CAESARS = "Caesar's Palace";
    String UBER_MANDALAY = "Mandalay Bay";
    String UBER_BELLAGIO = "Bellagio";
    String UBER_PLANET_HOLLYWOOD = "Planet Hollywood";

    String UBER_DEFAULT = "Choose your destination";

    String UBER_ADDRESS_PARIS = "3655 S Las Vegas Blvd, Las Vegas, NV 89109";
    String UBER_ADDRESS_BALLYS = "3645 S Las Vegas Blvd, Las Vegas, NV 89109";
    String UBER_ADDRESS_TUSCANY = "255 E Flamingo Rd, Las Vegas, NV 89169";
    String UBER_ADDRESS_CAESARS = "3570 S Las Vegas Blvd, Las Vegas, NV 89109";
    String UBER_ADDRESS_MANDALAY = "3950 S Las Vegas Blvd, Las Vegas, NV 89119";
    String UBER_ADDRESS_BELLAGIO = "3600 S Las Vegas Blvd, Las Vegas, NV 89109";
    String UBER_ADDRESS_PLANET_HOLLYWOOD = "3667 S Las Vegas Blvd, Las Vegas, NV 89109";

    String[] UBER_LOCATIONS = {UBER_PARIS, UBER_BALLYS, UBER_TUSCANY, UBER_PLANET_HOLLYWOOD, UBER_CAESARS, UBER_MANDALAY, UBER_BELLAGIO, UBER_DEFAULT};
    String[] UBER_ADDRESSES = {UBER_ADDRESS_PARIS, UBER_ADDRESS_BALLYS, UBER_ADDRESS_TUSCANY, UBER_ADDRESS_PLANET_HOLLYWOOD, UBER_ADDRESS_CAESARS, UBER_ADDRESS_MANDALAY, UBER_ADDRESS_BELLAGIO, UBER_ADDRESS_PARIS};
}