package com.shortstack.hackertracker.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shortstack.hackertracker.Adapter.DatabaseAdapter;
import com.shortstack.hackertracker.Adapter.DatabaseAdapterStarred;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Whitney Champion on 6/11/15.
 */
public class DetailsFragment extends DialogFragment {

    private Default item;
    private Context context;
    private View rootView;

    public static DetailsFragment newInstance(Default item) {
        DetailsFragment frag = new DetailsFragment();
        Bundle args = new Bundle();

        args.putParcelable("item", Parcels.wrap(item));

        frag.setArguments(args);

        return (frag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        item = Parcels.unwrap(args.getParcelable("item"));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // remove title bar
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        // set background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // set animation
        dialog.getWindow().getAttributes().windowAnimations = R.style.detailsAnimation;

        // full width
        dialog.getWindow().getAttributes().gravity = Gravity.FILL_HORIZONTAL;

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        context = inflater.getContext();

        if (rootView != null) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try {
            rootView = inflater.inflate(R.layout.details, container, false);
        } catch (InflateException e) {
        }

        // declare layout parts
        TextView titleText = (TextView) rootView.findViewById(R.id.title);
        TextView nameText = (TextView) rootView.findViewById(R.id.speaker);
        TextView timeText = (TextView) rootView.findViewById(R.id.when);
        TextView locationText = (TextView) rootView.findViewById(R.id.where);
        TextView forumText = (TextView) rootView.findViewById(R.id.link);
        TextView bodyText = (TextView) rootView.findViewById(R.id.description);
        LinearLayout whereLayout = (LinearLayout) rootView.findViewById(R.id.where_holder);
        ImageView demo = (ImageView) rootView.findViewById(R.id.demo);
        ImageView exploit = (ImageView) rootView.findViewById(R.id.exploit);
        ImageView tool = (ImageView) rootView.findViewById(R.id.tool);
        LinearLayout icons = (LinearLayout) rootView.findViewById(R.id.icons);
        final ImageButton share = (ImageButton) rootView.findViewById(R.id.share);
        final ImageButton star = (ImageButton) rootView.findViewById(R.id.star);

        // check if event
        String[] darknet = {"4770","4771","4772"};
        if ( Arrays.asList(darknet).contains(String.valueOf(item.getId()))) {
            Toast.makeText(context,"Jump in your Gibsons.",Toast.LENGTH_LONG).show();
        }

        // check if correct event
        int event = 4832;
        if (event == item.getId()) {
            Toast.makeText(context,"Who saw your number on the wall",Toast.LENGTH_LONG).show();

            // show event location
            whereLayout.setVisibility(View.GONE);
            LinearLayout newWhereLayout = (LinearLayout) rootView.findViewById(R.id.number);
            newWhereLayout.setVisibility(View.VISIBLE);

            // set on changed listener
            EditText number = (EditText) rootView.findViewById(R.id.the_number);
            number.addTextChangedListener(numberWatcher);

        }

        // set title
        titleText.setText(item.getTitle());

        // if not a speaker, hide speaker name
        if (!item.getType().equals(Constants.TYPE_SPEAKER) || item.getName()==null) {
            nameText.setVisibility(View.GONE);
        } else if (item.getName().isEmpty()) {
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

        // if no link, hide link
        if (item.getLink()==null || item.getLink().equals("") || item.getLink().equals(" ")) {
            forumText.setVisibility(View.GONE);
        } else {
            forumText.setText("More info: " + item.getLink());
        }

        // set location
        if (item.getLocation()!=null) {
            locationText.append(item.getLocation());
        } else {
            whereLayout.setVisibility(View.GONE);
        }

        // set body
        bodyText.setText(item.getDescription());

        // set date & time
        timeText.setText(item.getDate() + " @ " + item.getBegin() + " - " + item.getEnd());

        // check if entry is already in starred database
        if (item.getStarred()==1)
            star.setImageResource(R.drawable.star_selected);

        // onclicklistener for share
        final View.OnClickListener shareOnClickListener = new View.OnClickListener() {
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out \"" + item.getTitle() + "\" at DEF CON 23!");

                StringBuilder sb = new StringBuilder();
                sb.append(item.getTitle());
                if(item.getName()!=null)
                    sb.append("\n\nSpeaker: " + item.getName());
                sb.append("\n\nDate: " + item.getDate() + "\n\nTime: " + item.getBegin() + "\n\nLocation: " + item.getLocation());
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

                DatabaseAdapter myDbOfficialHelper = new DatabaseAdapter(context);
                DatabaseAdapterStarred myDbHelperStars = new DatabaseAdapterStarred(context);
                SQLiteDatabase dbOfficial = myDbOfficialHelper.getWritableDatabase();
                SQLiteDatabase dbStars = myDbHelperStars.getWritableDatabase();

                // if not starred, star it
                if (item.getStarred()==0) {

                    // add to starred database
                    dbStars.execSQL("INSERT INTO data VALUES (" + item.getId() + ")");
                    dbOfficial.execSQL("UPDATE data SET starred=" + 1 + " WHERE id=" + item.getId());

                    // set up alarm
                    if (!item.getBegin().equals("")) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, Integer.parseInt(item.getDate().split("-")[0]));
                        calendar.set(Calendar.MONTH, Integer.parseInt(item.getDate().split("-")[1]));
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(item.getDate().split("-")[2]));
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(item.getBegin().split(":")[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(item.getBegin().split(":")[1]));
                        calendar.set(Calendar.SECOND, 0);
                        long when = calendar.getTimeInMillis() - 1200000;

                        HackerTrackerApplication.scheduleNotification(HackerTrackerApplication.getNotification("\"" + item.getTitle() + "\" is starting in 20 minutes in " + item.getLocation() + "."), when, item.getId());
                    }

                    // change star
                    item.setStarred(1);
                    star.setImageResource(R.drawable.star_selected);
                    Toast.makeText(context, R.string.schedule_added, Toast.LENGTH_SHORT).show();

                } else {

                    // remove from starred database
                    dbStars.delete("data", "id=" + item.getId(), null);
                    dbOfficial.execSQL("UPDATE data SET starred=" + 0 + " WHERE id=" + item.getId());

                    // remove alarm
                    HackerTrackerApplication.cancelNotification(item.getId());

                    // change star
                    item.setStarred(0);
                    star.setImageResource(R.drawable.star_unselected);
                    Toast.makeText(context,R.string.schedule_removed,Toast.LENGTH_SHORT).show();
                }

                dbOfficial.close();
                dbStars.close();
            }
        };
        star.setOnClickListener(starOnClickListener);

        return rootView;
    }

    // edittext onchanged listener
    TextWatcher numberWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals("867-5309")) {
                ImageView daemon = (ImageView) rootView.findViewById(R.id.daemon);
                daemon.setVisibility(View.VISIBLE);
                daemon.setOnClickListener(codeOnClickListener);
            }
        }
    };

    // onclick listener for barcode
    final View.OnClickListener codeOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            DialogUtil.saveBarcodeDialog(context).show();

        }
    };

    // save barcode to device
    public static void saveImage(Context context) {

        Drawable drawable = context.getResources().getDrawable(R.drawable.daemon_1);
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        String imagePath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "barcode", "barcode");
        Uri uri = Uri.parse(imagePath);

    }


}
