package com.shortstack.hackertracker.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.*;
import android.widget.*;
import com.shortstack.hackertracker.Model.Event;
import com.shortstack.hackertracker.R;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:21 AM
 * Description:
 */
public class EventAdapter extends ArrayAdapter<Event> {

    public AlertDialog.Builder builder;
    public AlertDialog alertDialog;
    Context context;
    int layoutResourceId;
    Event data[] = null;

    public EventAdapter(Context context, int layoutResourceId, Event[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventHolder holder;
        View row = convertView;

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EventHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.location = (TextView) row.findViewById(R.id.location);
            holder.eventLayout = (LinearLayout) row.findViewById(R.id.entertainmentLayout);
            row.setTag(holder);


        } else {
            holder = (EventHolder)row.getTag();
        }

        final Event event = data[position];


        // if tweets in list, populate data
        if (event.getTitle() != null) {

            // set title
            holder.title.setText(event.getTitle());

            // set location
            holder.location.setText(event.getLocation());

            // set onclicklistener for share button
            final View finalRow = row;
            final View.OnClickListener shareOnClickListener = new View.OnClickListener() {
                public void onClick(View v) {

                    // get event details
                    String title = event.getTitle();
                    String body = event.getBody();
                    String startTime = event.getStartTime();
                    String endTime =  event.getEndTime();
                    String location = event.getLocation();

                    // build layout
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.entertainment_details,
                            (ViewGroup) finalRow.findViewById(R.id.layout_root));

                    // declare layout parts
                    TextView titleText = (TextView) layout.findViewById(R.id.title);
                    TextView timeText = (TextView) layout.findViewById(R.id.time);
                    TextView locationText = (TextView) layout.findViewById(R.id.location);
                    TextView bodyText = (TextView) layout.findViewById(R.id.body);
                    Button closeButton = (Button) layout.findViewById(R.id.closeButton);

                    // enter values
                    titleText.setText(title.split("- ")[1]);
                    timeText.setText("Time: " + startTime + " - " + endTime);
                    locationText.setText("Location: " + location);
                    bodyText.setText(body);

                    // set up & show alert dialog
                    builder = new AlertDialog.Builder( v.getRootView().getContext(), android.R.style.Theme_Translucent_NoTitleBar);
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
            holder.eventLayout.setOnClickListener(shareOnClickListener);




        } else {
            holder.title.setText("No events found");
        }

        return row;
    }

    static class EventHolder {
        TextView title;
        TextView location;
        LinearLayout eventLayout;
    }
}


