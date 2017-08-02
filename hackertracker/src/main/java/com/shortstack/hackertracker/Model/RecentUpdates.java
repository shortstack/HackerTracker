package com.shortstack.hackertracker.Model;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

public class RecentUpdates {

    private static final int MAX_RECENT = 30;

    private TreeSet<RecentUpdate> updates = new TreeSet<>();

    public void add(Item item ) {
        updates.add(new RecentUpdate(item));

        if(updates.size() > MAX_RECENT ) {

            // Removing the last (oldest) element.
            RecentUpdate target = null;

            for (RecentUpdate update : updates) {
                target = update;
            }

            updates.remove(target);
        }
    }

    public TreeSet<RecentUpdate> getUpdates() {
        return updates;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (RecentUpdate next : updates) {
            result.append(next.toString()).append("\n");
        }

        return result.toString();
    }

    public class RecentUpdate implements Comparable<RecentUpdate> {

        private String date;
        private int id;

        RecentUpdate(Item item) {
            date = item.getUpdatedAt();
            id = item.getIndex();
        }

        public String getDate() {
            return date;
        }

        public int getId() {
            return id;
        }

        @Override
        public int compareTo(@NonNull RecentUpdate o) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date left = simpleDateFormat.parse(date);
                Date right = simpleDateFormat.parse(o.date);


                int i = right.compareTo(left);
                if( i == 0 ) {
                    if( id == o.id ) return 0;

                    if( id < o.id )
                        return -1;
                    else
                        return 1;
                }

                return i;

            } catch (ParseException e) {
                e.printStackTrace();
            }

            int i = date.compareTo(o.date);

            if( i == 0 ) {
                if( id == o.id ) return 0;

                if( id < o.id )
                    return -1;
                else
                    return 1;
            }

            return i;
        }

        @Override
        public String toString() {
            return "{ date: \"" + date + "\", id: " + id + " }";
        }
    }
}
