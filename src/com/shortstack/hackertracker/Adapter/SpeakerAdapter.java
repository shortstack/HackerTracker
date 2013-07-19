package com.shortstack.hackertracker.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import com.shortstack.hackertracker.Model.Speaker;
import com.shortstack.hackertracker.R;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:21 AM
 * Description:
 */
public class SpeakerAdapter extends ArrayAdapter<Speaker> {

        public AlertDialog.Builder builder;
        public AlertDialog alertDialog;
        Context context;
        int layoutResourceId;
        Speaker data[] = null;

        public SpeakerAdapter(Context context, int layoutResourceId, Speaker[] data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final SpeakerHolder holder;
            View row = convertView;

            if ( row == null )
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new SpeakerHolder();
                holder.title = (TextView) row.findViewById(R.id.title);
                holder.location = (TextView) row.findViewById(R.id.location);
                holder.speakerLayout = (LinearLayout) row.findViewById(R.id.speakerLayout);
                row.setTag(holder);


            } else {
                holder = (SpeakerHolder)row.getTag();
            }

            final Speaker speaker = data[position];


            // if speakers in list, populate data
            if (speaker.getTitle() != null) {

                // set title
                holder.title.setText(speaker.getTitle());

                // set location
                holder.location.setText(speaker.getLocation());


                // set onclicklistener for share button
                final View finalRow = row;
                final View.OnClickListener shareOnClickListener = new View.OnClickListener() {
                    public void onClick(View v) {

                        // get speaker details
                        String title = speaker.getTitle();
                        String body = speaker.getBody();
                        String speakerName = speaker.getSpeaker();

                        String startTime = speaker.getStartTime();
                        String endTime =  speaker.getEndTime();
                        String location = speaker.getLocation();

                        Boolean demo = false;
                        Boolean tool = false;
                        Boolean exploit = false;
                        Boolean info = false;

                        Integer starred = speaker.getStarred();

                        if (speaker.getDemo() != null && speaker.getTool() != null && speaker.getExploit() != null && speaker.getInfo() != null) {
                            demo = speaker.getDemo();
                            tool = speaker.getTool();
                            exploit = speaker.getExploit();
                            info = speaker.getInfo();
                        }

                        // build layout
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.speaker_details,
                                (ViewGroup) finalRow.findViewById(R.id.layout_root));

                        // declare layout parts
                        TextView titleText = (TextView) layout.findViewById(R.id.title);
                        TextView speakerText = (TextView) layout.findViewById(R.id.speaker);
                        TextView timeText = (TextView) layout.findViewById(R.id.time);
                        TextView locationText = (TextView) layout.findViewById(R.id.location);
                        TextView bodyText = (TextView) layout.findViewById(R.id.body);
                        final TextView star = (TextView) layout.findViewById(R.id.star);
                        Button closeButton = (Button) layout.findViewById(R.id.closeButton);

                        // enter values
                        titleText.setText(title.split("- ")[1]);
                        if (speakerName!=null) {
                            speakerName = speakerName.replaceAll("\"","“");
                        }
                        speakerText.setText(speakerName);
                        if (!startTime.equals("") && !endTime.equals("")) {
                            timeText.setText("Time: " + startTime + " - " + endTime);
                        } else {
                            timeText.setVisibility(View.GONE);
                        }
                        if (location!=null) {
                            location = location.replaceAll("\"","“");
                            locationText.setText("Location: " + location);
                        } else {
                            locationText.setVisibility(View.GONE);
                        }
                        if (body!=null) {
                            body = body.replaceAll("\"","“");
                        }
                        bodyText.setText(body);

                        // set star
                        if (speaker.getStarred()==1) {
                            star.setTextColor(Color.parseColor("#ffcc00"));
                        }

                        // display images if applicable
                        if (demo) {
                            ImageView image = (ImageView) layout.findViewById(R.id.image_demo);
                            image.setImageResource(R.drawable.icon_demo);
                        }
                        if (tool) {
                            ImageView image = (ImageView) layout.findViewById(R.id.image_tool);
                            image.setImageResource(R.drawable.icon_tool);
                        }
                        if (exploit) {
                            ImageView image = (ImageView) layout.findViewById(R.id.image_exploit);
                            image.setImageResource(R.drawable.icon_exploit);
                        }

                        // onclicklistener for add to schedule
                        final String finalLocation = location;
                        final String finalBody = body;
                        final String finalSpeakerName = speakerName;
                        final View.OnClickListener starOnClickListener = new View.OnClickListener() {
                            public void onClick(View v) {

                                DatabaseAdapter myDbHelper = new DatabaseAdapter(getContext());
                                SQLiteDatabase dbSpeakers = myDbHelper.getWritableDatabase();

                                if (star.getCurrentTextColor() == -1) {
                                    // add to stars database
                                    dbSpeakers.execSQL("INSERT INTO stars VALUES (null,\""+ speaker.getTitle() +"\",\""+ finalBody +"\",\""+speaker.getStartTime()+"\",\""+speaker.getEndTime()+"\",\""+speaker.getDate()+"\",\""+ finalLocation +"\",\"\","+"\""+finalSpeakerName+"\",1)");
                                    dbSpeakers.execSQL("UPDATE speakers SET starred="+1+" WHERE _id="+speaker.getId());
                                    // change star color
                                    speaker.setStarred(1);
                                    star.setTextColor(Color.parseColor("#ffcc00"));
                                    Toast.makeText(context,"Added to My Schedule",Toast.LENGTH_SHORT).show();

                                } else {
                                    // remove from database
                                    dbSpeakers.execSQL("DELETE FROM stars WHERE title=\""+ speaker.getTitle() +"\" AND startTime=\""+speaker.getStartTime()+"\" AND date=\""+speaker.getDate()+"\"");
                                    dbSpeakers.execSQL("UPDATE speakers SET starred="+0+" WHERE _id="+speaker.getId());
                                    // change star color
                                    speaker.setStarred(0);
                                    star.setTextColor(Color.parseColor("#ffffff"));
                                    Toast.makeText(context,"Removed from My Schedule",Toast.LENGTH_SHORT).show();
                                }

                                dbSpeakers.close();
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
                holder.speakerLayout.setOnClickListener(shareOnClickListener);



            } else {
                holder.title.setText("No speakers found");
            }

            return row;
        }

        static class SpeakerHolder {
            TextView title;
            TextView location;
            LinearLayout speakerLayout;
        }
    }


