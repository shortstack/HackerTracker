package com.shortstack.hackertracker.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
            SpeakerHolder holder;
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


            // if tweets in list, populate data
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

                        // enter values
                        titleText.setText(title.split("- ")[1]);
                        speakerText.setText(speakerName);
                        timeText.setText("Time: " + startTime + " - " + endTime);
                        locationText.setText("Location: " + location);
                        bodyText.setText(body);

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

                        // set up & show alert dialog
                        builder = new AlertDialog.Builder( v.getRootView().getContext());
                        alertDialog = builder.create();
                        alertDialog.setView(layout, 0, 0, 0, 0);
                        alertDialog.setButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
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


