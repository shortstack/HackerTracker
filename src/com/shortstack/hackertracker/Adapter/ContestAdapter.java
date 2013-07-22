package com.shortstack.hackertracker.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.*;
import android.widget.*;
import com.shortstack.hackertracker.Model.Contest;
import com.shortstack.hackertracker.Model.Star;
import com.shortstack.hackertracker.R;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:21 AM
 * Description:
 */
public class ContestAdapter extends ArrayAdapter<Contest> {

    public AlertDialog.Builder builder;
    public AlertDialog alertDialog;
    Context context;
    int layoutResourceId;
    Contest data[] = null;

    public ContestAdapter(Context context, int layoutResourceId, Contest[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ContestHolder holder;
        View row = convertView;

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ContestHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.location = (TextView) row.findViewById(R.id.location);
            holder.contestLayout = (LinearLayout) row.findViewById(R.id.contestLayout);
            row.setTag(holder);


        } else {
            holder = (ContestHolder)row.getTag();
        }

        final Contest contest = data[position];


        // if tweets in list, populate data
        if (contest.getTitle() != null) {

            // set title
            holder.title.setText(contest.getTitle());

            // set location
            holder.location.setText(contest.getLocation());

            // set onclicklistener for share button
            final View finalRow = row;
            final View.OnClickListener shareOnClickListener = new View.OnClickListener() {
                public void onClick(View v) {

                    // get contest details
                    String title = contest.getTitle();
                    String body = contest.getBody();
                    String startTime = contest.getStartTime();
                    String endTime =  contest.getEndTime();
                    String location = contest.getLocation();
                    String forum = contest.getForum();
                    Integer starred = contest.getStarred();

                    // build layout
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.contest_details,
                            (ViewGroup) finalRow.findViewById(R.id.layout_root));

                    // declare layout parts
                    TextView titleText = (TextView) layout.findViewById(R.id.title);
                    TextView timeText = (TextView) layout.findViewById(R.id.time);
                    TextView locationText = (TextView) layout.findViewById(R.id.location);
                    TextView forumText = (TextView) layout.findViewById(R.id.forum);
                    TextView bodyText = (TextView) layout.findViewById(R.id.body);
                    final TextView star = (TextView) layout.findViewById(R.id.star);
                    Button closeButton = (Button) layout.findViewById(R.id.closeButton);

                    // enter values
                    if (title.contains("-")) {
                        titleText.setText(title.split("- ")[1]);
                    } else {
                        titleText.setText(title);
                    }
                    if (!startTime.equals("") && !endTime.equals("")) {
                        timeText.setText("Time: " + startTime + " - " + endTime);
                    } else {
                        timeText.setVisibility(View.GONE);
                    }
                    if (!location.equals("")) {
                        location = location.replaceAll("\"","“");
                        locationText.setText("Location: " + location);
                    } else {
                        locationText.setVisibility(View.GONE);
                    }
                    if (!forum.equals("")) {
                        forumText.setText("Forum: " + forum);

                    } else {
                        forumText.setVisibility(View.GONE);
                    }
                    if (!body.equals("")) {
                        body = body.replaceAll("\"","“");
                    }
                    bodyText.setText(body);

                    // check if entry is already in starred database
                    DatabaseAdapter myDbHelper = new DatabaseAdapter(getContext());
                    StarDatabaseAdapter myDbHelperStars = new StarDatabaseAdapter(getContext());
                    SQLiteDatabase dbContests = myDbHelper.getWritableDatabase();
                    SQLiteDatabase dbStars = myDbHelperStars.getWritableDatabase();
                    Cursor myCursor = dbStars.rawQuery("SELECT * FROM stars WHERE title=\""+contest.getTitle()+"\" AND startTime=\""+contest.getStartTime()+"\" AND date=\""+contest.getDate()+"\"", null);
                    try{
                        if (myCursor.moveToFirst()){
                            do{
                                dbContests.execSQL("UPDATE contests SET starred="+1+" WHERE _id="+contest.getId());
                                star.setTextColor(Color.parseColor("#ffcc00"));
                            }while(myCursor.moveToNext());
                        }
                    }finally{
                        myCursor.close();
                    }
                    dbContests.close();
                    dbStars.close();

                    // set star
                    if (contest.getStarred()==1) {
                        star.setTextColor(Color.parseColor("#ffcc00"));
                    }

                    // onclicklistener for add to schedule
                    final String finalLocation = location;
                    final String finalBody = body;
                    final View.OnClickListener starOnClickListener = new View.OnClickListener() {
                        public void onClick(View v) {

                            DatabaseAdapter myDbHelper = new DatabaseAdapter(getContext());
                            StarDatabaseAdapter myDbHelperStars = new StarDatabaseAdapter(getContext());
                            SQLiteDatabase dbContests = myDbHelper.getWritableDatabase();
                            SQLiteDatabase dbStars = myDbHelperStars.getWritableDatabase();

                            if (star.getCurrentTextColor() == -1) {
                                // add to stars database
                                dbStars.execSQL("INSERT INTO stars VALUES (null,\""+contest.getTitle()+"\",\""+ finalBody + "\",\"" + contest.getStartTime() + "\",\"" + contest.getEndTime() + "\",\"" + contest.getDate() + "\",\"" + finalLocation + "\",\"" + contest.getForum() + "\",\"\",1)");
                                dbContests.execSQL("UPDATE contests SET starred="+1+" WHERE _id="+contest.getId());
                                // change star color
                                contest.setStarred(1);
                                star.setTextColor(Color.parseColor("#ffcc00"));
                                Toast.makeText(context,"Added to My Schedule",Toast.LENGTH_SHORT).show();

                            } else {
                                // remove from database
                                dbStars.execSQL("DELETE FROM stars WHERE title=\""+contest.getTitle()+"\" AND startTime=\""+contest.getStartTime()+"\" AND date=\""+contest.getDate()+"\"");
                                dbContests.execSQL("UPDATE contests SET starred="+0+" WHERE _id="+contest.getId());
                                // change star color
                                contest.setStarred(0);
                                star.setTextColor(Color.parseColor("#ffffff"));
                                Toast.makeText(context,"Removed from My Schedule",Toast.LENGTH_SHORT).show();
                            }

                            dbContests.close();
                            dbStars.close();
                        }
                    };
                    star.setOnClickListener(starOnClickListener);

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
            holder.contestLayout.setOnClickListener(shareOnClickListener);


        } else {
            holder.title.setText("No contests found");
        }

        return row;
    }

    static class ContestHolder {
        TextView title;
        TextView location;
        LinearLayout contestLayout;
    }
}


