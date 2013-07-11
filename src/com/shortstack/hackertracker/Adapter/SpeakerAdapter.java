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

                        Boolean demo = speaker.getDemo();
                        Boolean tool = speaker.getTool();
                        Boolean exploit = speaker.getExploit();
                        Boolean info = speaker.getInfo();

                        // build speaker details into string
                        StringBuilder sb = new StringBuilder();
                        sb.append("Start Time: " + startTime + " \n");
                        sb.append("End Time: " + endTime + " \n");
                        sb.append("Location: " + location + " \n\n");
                        if (body != null) {
                            sb.append(body + " \n\n");
                        }
                        sb.append("By " + speakerName);

                        // build alert dialog layout
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View layout = inflater.inflate(R.layout.speaker_details,
                                (ViewGroup) finalRow.findViewById(R.id.layout_root));

                        // assign values to layout parts
                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText(sb);

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
                        builder.setView(layout);
                        builder.setTitle(title.split("- ")[1]);
                        alertDialog = builder.create();
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


