package com.shortstack.hackertracker.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
            holder.name = (TextView) row.findViewById(R.id.name);
            holder.name.setVisibility(View.GONE);
            holder.demo = (ImageView) row.findViewById(R.id.demo);
            holder.demo.setVisibility(View.GONE);
            holder.exploit = (ImageView) row.findViewById(R.id.exploit);
            holder.exploit.setVisibility(View.GONE);
            holder.tool = (ImageView) row.findViewById(R.id.tool);
            holder.tool.setVisibility(View.GONE);
            holder.music = (ImageView) row.findViewById(R.id.music);
            holder.music.setVisibility(View.GONE);
            holder.icons = (LinearLayout) row.findViewById(R.id.icons);
            holder.icons.setVisibility(View.GONE);
            holder.is_new = (TextView) row.findViewById(R.id.isNew);
            holder.is_new.setVisibility(View.GONE);
            holder.where = (TextView) row.findViewById(R.id.where);
            holder.defaultLayout = (LinearLayout) row.findViewById(R.id.rootLayout);
            row.setTag(holder);

        } else {
            holder = (DefaultHolder)row.getTag();
        }

        final Default item = data.get(position);

        // if items in list, populate data
        if (item.getTitle() != null) {

            // if it's a DJ/music, truncate "MUSIC"
            if (item.getTitle().contains("MUSIC - ")) {
                holder.title.setText(item.getTitle().split(" - ")[1]);
            } else {
                // set title
                holder.title.setText(item.getTitle());
            }

            // set name if it's a speaker
            if (item.getType()==1) {
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(item.getName());
            } else {
                holder.name.setVisibility(View.GONE);
            }

            // set speaker type
            if (item.getType()==1) {

                if (item.getTool()==1) {
                    holder.icons.setVisibility(View.VISIBLE);
                    holder.tool.setVisibility(View.VISIBLE);
                } else {
                    holder.tool.setVisibility(View.GONE);
                }
                if (item.getExploit()==1) {
                    holder.icons.setVisibility(View.VISIBLE);
                    holder.exploit.setVisibility(View.VISIBLE);
                } else {
                    holder.exploit.setVisibility(View.GONE);
                }
                if (item.getDemo()==1) {
                    holder.icons.setVisibility(View.VISIBLE);
                    holder.demo.setVisibility(View.VISIBLE);
                } else {
                    holder.demo.setVisibility(View.GONE);
                }

                if (item.getTool()==0 && item.getExploit()==0 && item.getDemo()==0)
                    holder.icons.setVisibility(View.GONE);

            } else {

                if (item.getTitle().contains("MUSIC - ")) {
                    holder.icons.setVisibility(View.VISIBLE);
                    holder.music.setVisibility(View.VISIBLE);
                } else {
                    holder.icons.setVisibility(View.GONE);
                    holder.music.setVisibility(View.GONE);
                }

                holder.tool.setVisibility(View.GONE);
                holder.exploit.setVisibility(View.GONE);
                holder.demo.setVisibility(View.GONE);
            }

            // set time
            holder.time.setText(item.getBegin());

            // set location
            holder.where.setText(item.getWhere());

            // if new, show "new"
            holder.is_new.setVisibility(View.GONE);
            if (item.isNew()!=null) {
                if (item.isNew() == 1)
                    holder.is_new.setVisibility(View.VISIBLE);
            } else {
                holder.is_new.setVisibility(View.GONE);
            }

            // set onclicklistener for share button
            final View finalRow = row;
            final View.OnClickListener openOnClickListener = new View.OnClickListener() {
                public void onClick(View v) {

                // hide keyboard
                hideKeyboard(v);

                // build layout
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.details,
                        (ViewGroup) finalRow.findViewById(R.id.layout_root));

                // declare layout parts
                TextView titleText = (TextView) layout.findViewById(R.id.title);
                TextView nameText = (TextView) layout.findViewById(R.id.speaker);
                TextView timeText = (TextView) layout.findViewById(R.id.time);
                TextView dateText = (TextView) layout.findViewById(R.id.date);
                TextView locationText = (TextView) layout.findViewById(R.id.where);
                TextView forumText = (TextView) layout.findViewById(R.id.link);
                TextView bodyText = (TextView) layout.findViewById(R.id.description);
                ImageView demo = (ImageView) layout.findViewById(R.id.demo);
                ImageView exploit = (ImageView) layout.findViewById(R.id.exploit);
                ImageView tool = (ImageView) layout.findViewById(R.id.tool);
                LinearLayout icons = (LinearLayout) layout.findViewById(R.id.icons);
                final ImageButton share = (ImageButton) layout.findViewById(R.id.share);
                final ImageButton star = (ImageButton) layout.findViewById(R.id.star);
                Button closeButton = (Button) layout.findViewById(R.id.closeButton);

                // if not a speaker, hide speaker name
                if (item.getType()!=Constants.TYPE_SPEAKER) {
                    nameText.setVisibility(View.GONE);
                } else {
                    nameText.setText(item.getName());
                }

                // if speaker, show speaker type
                if (item.getTool()==0 && item.getExploit()==0 && item.getDemo()==0)
                    icons.setVisibility(View.GONE);
                else {
                    icons.setVisibility(View.VISIBLE);
                    if (item.getTool()==1) {
                        tool.setVisibility(View.VISIBLE);
                    }
                    if (item.getExploit()==1) {
                        exploit.setVisibility(View.VISIBLE);
                    }
                    if (item.getDemo()==1) {
                        demo.setVisibility(View.VISIBLE);
                    }
                }

                // if no forum, hide forum
                if (item.getLink()==null) {
                    forumText.setVisibility(View.GONE);
                } else {
                    forumText.setText("Site: " + item.getLink());
                }

                // set title
                titleText.setText(item.getTitle());

                // set location
                if (item.getWhere()!=null) {
                    locationText.append(item.getWhere());
                }

                // set body
                bodyText.setText(item.getDescription());

                // set date
                dateText.setText(getDate(item.getDate()));

                // set time
                timeText.setText(item.getBegin() + " - " + item.getEnd());

                // check if entry is already in starred database
                if (item.getStarred()==1)
                   star.setImageResource(R.drawable.star_selected);

                // onclicklistener for share
                final View.OnClickListener shareOnClickListener = new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out \"" + item.getTitle() + "\" at DEF CON 22!");

                        StringBuilder sb = new StringBuilder();
                        sb.append(item.getTitle());
                        if(item.getName()!=null)
                            sb.append("\n\nSpeaker: " + item.getName());
                        sb.append("\n\nDate: " + getDate(item.getDate()) + "\n\nTime: " + item.getBegin() + "\n\nLocation: " + item.getWhere());
                        if(item.getDescription()!=null)
                            sb.append("\n\nMore details:\n\n" + item.getDescription());

                        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                        sendIntent.setType("text/plain");
                        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.action_share)));

                    }
                };
                share.setOnClickListener(shareOnClickListener);

                // onclicklistener for add to schedule
                final View.OnClickListener starOnClickListener = new View.OnClickListener() {
                    public void onClick(View v) {

                        DatabaseAdapterOfficial myDbOfficialHelper = new DatabaseAdapterOfficial(context);
                        DatabaseAdapter myDbHelper = new DatabaseAdapter(context);
                        DatabaseAdapterStarred myDbHelperStars = new DatabaseAdapterStarred(context);
                        SQLiteDatabase dbDefaults = myDbHelper.getWritableDatabase();
                        SQLiteDatabase dbOfficial = myDbOfficialHelper.getWritableDatabase();
                        SQLiteDatabase dbStars = myDbHelperStars.getWritableDatabase();

                        // if not starred, star it
                        if (item.getStarred()==0) {

                            // add to stars database
                            dbStars.execSQL("INSERT INTO data VALUES (" + item.getId() + ")");

                            if (item.getType() == 1) {
                                dbOfficial.execSQL("UPDATE data SET starred=" + 1 + " WHERE id=" + item.getId());
                            } else {
                                dbDefaults.execSQL("UPDATE data SET starred=" + 1 + " WHERE id=" + item.getId());
                            }

                            // change star
                            item.setStarred(1);
                            star.setImageResource(R.drawable.star_selected);
                            Toast.makeText(context,"Added to My Schedule",Toast.LENGTH_SHORT).show();

                        } else {

                            // remove from database
                            dbStars.delete("data", "id=" + item.getId(), null);

                            if (item.getType() == 1) {
                                dbOfficial.execSQL("UPDATE data SET starred=" + 0 + " WHERE id=" + item.getId());
                            } else {
                                dbDefaults.execSQL("UPDATE data SET starred=" + 0 + " WHERE id=" + item.getId());
                            }

                            // change star
                            item.setStarred(0);
                            star.setImageResource(R.drawable.star_unselected);
                            Toast.makeText(context,"Removed from My Schedule",Toast.LENGTH_SHORT).show();
                        }

                        dbDefaults.close();
                        dbOfficial.close();
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
            holder.defaultLayout.setOnClickListener(openOnClickListener);

        }


        return row;
    }

    private String getDate(int date) {

        switch (date) {
            case 0:
                return Constants.DAY_0;
            case 1:
                return Constants.DAY_1;
            case 2:
                return Constants.DAY_2;
            case 3:
                return Constants.DAY_3;
            case 4:
                return Constants.DAY_4;
        }
        return "";

    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    static class DefaultHolder {
        TextView title;
        TextView time;
        TextView name;
        TextView where;
        TextView is_new;
        ImageView demo;
        ImageView tool;
        ImageView exploit;
        ImageView music;
        LinearLayout icons;
        LinearLayout defaultLayout;
    }
}


