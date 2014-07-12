package com.shortstack.hackertracker.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:21 AM
 * Description:
 */
public class DefaultAdapter extends ArrayAdapter<Default> {

    Context context;
    int layoutResourceId;
    List<Default> data;

    public DefaultAdapter(Context context, int layoutResourceId, List<Default> data) {
        super(context,layoutResourceId,data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DefaultHolder holder;
        View row = convertView;

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DefaultHolder();
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.time = (TextView) row.findViewById(R.id.time);
            holder.location = (TextView) row.findViewById(R.id.location);
            holder.defaultLayout = (LinearLayout) row.findViewById(R.id.rootLayout);
            row.setTag(holder);

        } else {
            holder = (DefaultHolder)row.getTag();
        }

        final Default item = data.get(position);

        // if items in list, populate data
        if (item.getTitle() != null) {

            // set title
            holder.title.setText(item.getTitle());

            // set time
            holder.time.setText(item.getStartTime());

            // set location
            holder.location.setText(item.getLocation());

            // set onclicklistener for share button
            final View finalRow = row;
            final View.OnClickListener shareOnClickListener = new View.OnClickListener() {
                public void onClick(View v) {

                    // build layout
                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.details,
                            (ViewGroup) finalRow.findViewById(R.id.layout_root));

                    // declare layout parts
                    TextView titleText = (TextView) layout.findViewById(R.id.title);
                    TextView nameText = (TextView) layout.findViewById(R.id.speaker);
                    TextView timeText = (TextView) layout.findViewById(R.id.time);
                    TextView locationText = (TextView) layout.findViewById(R.id.location);
                    TextView forumText = (TextView) layout.findViewById(R.id.forum);
                    TextView bodyText = (TextView) layout.findViewById(R.id.body);
                    final ImageView star = (ImageView) layout.findViewById(R.id.star);
                    Button closeButton = (Button) layout.findViewById(R.id.closeButton);

                    // if not a speaker, hide speaker name
                    if (item.getType()!=Constants.TYPE_SPEAKER) {
                        nameText.setVisibility(View.GONE);
                    } else {
                        nameText.setText(item.getName());
                    }

                    // if no forum, hide forum
                    if (item.getForum()==null) {
                        forumText.setVisibility(View.GONE);
                    } else {
                        forumText.setText("Site: " + item.getForum());
                    }

                    // set title
                    titleText.setText(item.getTitle());

                    // set location
                    if (item.getLocation()!=null) {
                        locationText.append(item.getLocation());
                    }

                    // set body
                    bodyText.setText(item.getBody());

                    // set time
                    timeText.setText(item.getStartTime() + " - " + item.getEndTime());

                    // check if entry is already in starred database
                    if (item.getStarred()==1)
                       star.setImageResource(R.drawable.star_selected);

                    // onclicklistener for add to schedule
                    final View.OnClickListener starOnClickListener = new View.OnClickListener() {
                        public void onClick(View v) {

                            DatabaseAdapter myDbHelper = new DatabaseAdapter(context);
                            StarDatabaseAdapter myDbHelperStars = new StarDatabaseAdapter(context);
                            SQLiteDatabase dbDefaults = myDbHelper.getWritableDatabase();
                            SQLiteDatabase dbStars = myDbHelperStars.getWritableDatabase();

                            // if not starred, star it
                            if (item.getStarred()==0) {
                                // add to stars database
                                dbStars.execSQL("INSERT INTO data VALUES ("+item.getId()+")");
                                dbDefaults.execSQL("UPDATE data SET starred=" + 1 + " WHERE id=" + item.getId());
                                // change star
                                item.setStarred(1);
                                star.setImageResource(R.drawable.star_selected);
                                Toast.makeText(context,"Added to My Schedule",Toast.LENGTH_SHORT).show();

                            } else {
                                // remove from database
                                dbStars.delete("data", "id=" + item.getId(), null);
                                dbDefaults.execSQL("UPDATE data SET starred=" + 0 + " WHERE id=" + item.getId());
                                // change star
                                item.setStarred(0);
                                star.setImageResource(R.drawable.star_unselected);
                                Toast.makeText(context,"Removed from My Schedule",Toast.LENGTH_SHORT).show();
                            }

                            dbDefaults.close();
                            dbStars.close();
                        }
                    };
                    star.setOnClickListener(starOnClickListener);

                    // set up & show alert dialog
                    final Dialog alertDialog=new Dialog(context,android.R.style.Theme_Black_NoTitleBar);
                    alertDialog.setContentView(layout);
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();

                }
            };
            holder.defaultLayout.setOnClickListener(shareOnClickListener);

        }

        return row;
    }

    static class DefaultHolder {
        TextView title;
        TextView time;
        TextView location;
        LinearLayout defaultLayout;
    }
}


