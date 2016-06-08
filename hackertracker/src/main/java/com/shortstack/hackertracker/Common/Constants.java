package com.shortstack.hackertracker.Common;

/**
 * Created by whitneychampion on 6/22/14.
 */
public interface Constants {

    String API_URL = "https://s3.amazonaws.com/defcon-api";
    String API_ERROR_MESSAGE = "\"errorMessage\":";
    String OFFICIAL_SCHEDULE = "/schedule-full.json";

    String TYPE_SPEAKER = "Official";
    String TYPE_CONTEST = "Contest";
    String TYPE_EVENT = "Event";
    String TYPE_PARTY = "Party";
    String TYPE_VENDOR = "Vendor";
    String TYPE_DEMOLAB = "DemoLabs";
    String TYPE_SKYTALKS = "Skytalks";
    String TYPE_MESSAGE = "Message";
    String TYPE_VILLAGE = "Villages";
    String TYPE_BOOK = "Book-Signing";
    String TYPE_UNOFFICIAL = "Un-Official";
    String TYPE_DCIB = "DCIB";
    String TYPE_STUPID = "Stupid";
    String TYPE_KIDS = "Kids";
    String TYPE_JOKE = "Joke";

    String LONG_DAY_0 = "2016-08-03";
    String LONG_DAY_1 = "2016-08-04";
    String LONG_DAY_2 = "2016-08-05";
    String LONG_DAY_3 = "2016-08-06";
    String LONG_DAY_4 = "2016-08-07";

    String DAY_0 = "Wednesday";
    String DAY_1 = "Thursday";
    String DAY_2 = "Friday";
    String DAY_3 = "Saturday";
    String DAY_4 = "Sunday";

    String FRAGMENT_SCHEDULE = "SchedulePagerFragment";
    String FRAGMENT_SPEAKERS = "SpeakerPagerFragment";
    String FRAGMENT_CONTESTS = "ContestPagerFragment";
    String FRAGMENT_EVENTS = "EventPagerFragment";
    String FRAGMENT_PARTIES = "PartyPagerFragment";
    String FRAGMENT_KIDS = "KidsPagerFragment";
    String FRAGMENT_SKYTALKS = "SkytalksPagerFragment";
    String FRAGMENT_BOOKS = "BookPagerFragment";
    String FRAGMENT_VILLAGES = "VillagePagerFragment";
    String FRAGMENT_VENDORS = "VendorsFragment";
    String FRAGMENT_DEMOLAB = "DemoLabsFragment";
    String FRAGMENT_HOME = "HomeFragment";
    String FRAGMENT_LINKS = "LinksFragment";
    String FRAGMENT_MAPS = "MapsFragment";
    String FRAGMENT_SEARCH = "SearchFragment";
    String FRAGMENT_SHUTTLE = "ShuttleFragment";
    String FRAGMENT_FAQ = "FAQFragment";
    String FRAGMENT_SETTINGS = "SettingsFragment";

    String[] COLUMN_NAMES = {"Title","Speaker Name","Start Time","End Time","Date","Location"};

}