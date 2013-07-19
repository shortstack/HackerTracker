package com.shortstack.hackertracker.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import com.shortstack.hackertracker.Model.Contest;
import com.shortstack.hackertracker.Model.Star;
import com.shortstack.hackertracker.R;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/18/13
 * Time: 10:16 PM
 */
public class StarAdapter extends ArrayAdapter<Star> {

    public AlertDialog.Builder builder;
    public AlertDialog alertDialog;
    Context context;
    int layoutResourceId;
    Star data[] = null;

    public StarAdapter(Context context, int layoutResourceId, Star[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final StarHolder holder;
        View row = convertView;

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StarHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.location = (TextView) row.findViewById(R.id.location);
            holder.starLayout = (LinearLayout) row.findViewById(R.id.starLayout);
            row.setTag(holder);


        } else {
            holder = (StarHolder)row.getTag();
        }

        final Star star = data[position];



        // if tweets in list, populate data
        if (star.getTitle() != null) {

            // set title
            holder.title.setText(star.getTitle());

            // set location
            holder.location.setText(star.getLocation());

            // set onclicklistener for share button
            final View finalRow = row;
            final View.OnClickListener shareOnClickListener = new View.OnClickListener() {
                public void onClick(View v) {

                    // get contest details
                    String title = star.getTitle();
                    String body = star.getBody();
                    String speaker =  star.getSpeaker();
                    String startTime = star.getStartTime();
                    String endTime =  star.getEndTime();
                    String location = star.getLocation();
                    String forum = star.getForum();
                    Integer starred = star.getStarred();

                    // build layout
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.star_details,
                            (ViewGroup) finalRow.findViewById(R.id.layout_root));

                    // declare layout parts
                    TextView titleText = (TextView) layout.findViewById(R.id.title);
                    TextView timeText = (TextView) layout.findViewById(R.id.time);
                    TextView locationText = (TextView) layout.findViewById(R.id.location);
                    TextView forumText = (TextView) layout.findViewById(R.id.forum);
                    TextView speakerText = (TextView) layout.findViewById(R.id.speaker);
                    TextView bodyText = (TextView) layout.findViewById(R.id.body);
                    final TextView starIcon = (TextView) layout.findViewById(R.id.star);
                    Button closeButton = (Button) layout.findViewById(R.id.closeButton);

                    // enter values
                    if (title.contains("-")) {
                        titleText.setText(title.split("- ")[1]);
                    } else {
                        titleText.setText(title);
                    }
                    if (startTime!=null && endTime!=null) {
                        timeText.setText("Time: " + startTime + " - " + endTime);
                    } else {
                        timeText.setVisibility(View.GONE);
                    }
                    if (location!=null) {
                        locationText.setText("Location: " + location);
                    } else {
                        locationText.setVisibility(View.GONE);
                    }
                    if (speaker!=null) {
                        speakerText.setText(speaker);
                    } else {
                        speakerText.setVisibility(View.GONE);
                    }
                    if (forum!=null) {
                        forumText.setText("Forum: " + forum);
                    } else {
                        forumText.setVisibility(View.GONE);
                    }
                    bodyText.setText(body);

                    // set star
                    if (star.getStarred()==1) {
                        starIcon.setTextColor(Color.parseColor("#ffcc00"));
                    }

                    // onclicklistener for add to schedule
                    final View.OnClickListener starOnClickListener = new View.OnClickListener() {
                        public void onClick(View v) {

                            DatabaseAdapter myDbHelper = new DatabaseAdapter(getContext());
                            SQLiteDatabase dbStars = myDbHelper.getReadableDatabase();

                            if (starIcon.getCurrentTextColor() == -1) {
                                // add to stars database
                                dbStars.execSQL("INSERT INTO stars VALUES (null,"+star.getTitle()+"\",\""+star.getBody()+"\",\""+star.getStartTime()+"\",\""+star.getEndTime()+"\",\""+star.getDate()+"\",\""+star.getLocation()+"\",\""+star.getForum()+"\",\""+star.getSpeaker()+"\",1)");
                                // change star color
                                star.setStarred(1);
                                starIcon.setTextColor(Color.parseColor("#ffcc00"));
                                Toast.makeText(context,"Added to My Schedule",Toast.LENGTH_SHORT).show();
                            } else {
                                // remove from database
                                dbStars.execSQL("DELETE FROM stars WHERE title=\""+star.getTitle()+"\" AND startTime=\""+star.getStartTime()+"\" AND date=\""+star.getDate()+"\"");
                                // change star color
                                star.setStarred(0);
                                starIcon.setTextColor(Color.parseColor("#ffffff"));
                                Toast.makeText(context,"Removed from My Schedule",Toast.LENGTH_SHORT).show();
                            }

                            dbStars.close();
                        }
                    };
                    starIcon.setOnClickListener(starOnClickListener);

                    // set up & show alert dialog
                    builder = new AlertDialog.Builder( v.getRootView().getContext());
                    alertDialog = builder.create();
                    alertDialog.setView(layout, 0, 0, 0, 0);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    Window window = alertDialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);
                    alertDialog.show();


                }
            };
            holder.starLayout.setOnClickListener(shareOnClickListener);

            } else {
            holder.title.setText("You have no starred items");
        }


        return row;


    }


    static class StarHolder {
        TextView title;
        TextView location;
        LinearLayout starLayout;
    }


}