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

import com.shortstack.hackertracker.Activity.HomeActivity;
import com.shortstack.hackertracker.Application.HackerTrackerApplication;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.Fragment.DetailsFragment;
import com.shortstack.hackertracker.Model.Default;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Utils.DialogUtil;

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

            // set title
            holder.title.setText(item.getTitle());

            // set name if it's a speaker
            if (!item.getType().equals(Constants.TYPE_SPEAKER) || item.getName()==null) {
                holder.name.setVisibility(View.GONE);
            } else if (item.getName().equals("")){
                holder.name.setVisibility(View.GONE);
            } else {
                holder.name.setVisibility(View.VISIBLE);
                holder.name.setText(item.getName());
            }

            // set speaker type
            if (item.getType().equals(Constants.TYPE_SPEAKER)) {

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

                holder.icons.setVisibility(View.GONE);
                holder.tool.setVisibility(View.GONE);
                holder.exploit.setVisibility(View.GONE);
                holder.demo.setVisibility(View.GONE);

            }

            // set time
            holder.time.setText(item.getBegin());

            // set location
            holder.where.setText(item.getLocation());

            // if new, show "new"
            holder.is_new.setVisibility(View.GONE);
            if (item.isNew()!=null) {
                if (item.isNew() == 1)
                    holder.is_new.setVisibility(View.VISIBLE);
            } else {
                holder.is_new.setVisibility(View.GONE);
            }

            // set onclicklistener for viewing event
            holder.defaultLayout.setOnClickListener(new detailsOnClickListener(item));

        }


        return row;
    }

    public class detailsOnClickListener implements View.OnClickListener {

        private Default item;

        public detailsOnClickListener(Default item) {
            this.item = item;
        }

        @Override
        public void onClick(View v) {

            // open DetailsFragment
            DetailsFragment detailsFragment = DialogUtil.getProfileDialog(item);
            detailsFragment.show(HomeActivity.fragmentManager, "detailsFragment");
        }
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
        LinearLayout icons;
        LinearLayout defaultLayout;
    }
}


