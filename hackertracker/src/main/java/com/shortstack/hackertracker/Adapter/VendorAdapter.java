package com.shortstack.hackertracker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shortstack.hackertracker.Model.Vendor;

import junit.framework.Assert;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 7/11/13
 * Time: 9:21 AM
 * Description:
 */
public class VendorAdapter extends ArrayAdapter<Vendor> {

    Context context;
    int layoutResourceId;
    Vendor data[] = null;

    public VendorAdapter(Context context, int layoutResourceId, Vendor[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VendorHolder holder;
        View row = convertView;

        if ( row == null )
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new VendorHolder();

            row.setTag(holder);


        } else {
            holder = (VendorHolder)row.getTag();
        }

        final Vendor vendor = data[position];

        // if vendors in list, populate data
        if (vendor.getTitle() != null) {

            // set title
            holder.title.setText(vendor.getTitle());

            // set website
            if (vendor.getLink()!=null) {
                holder.website.setText(vendor.getLink());
            } else {
                holder.website.setVisibility(View.GONE);
            }

            // set body
            holder.body.setText(vendor.getDescription());

            // set logo
            String logo = vendor.getImage();
            int imageResource = getDrawable(context,logo);
            holder.logo.setImageResource(imageResource);

        }

        return row;
    }

    static class VendorHolder {
        TextView title;
        TextView website;
        TextView body;
        ImageView logo;
        LinearLayout vendorLayout;
    }

    public static int getDrawable(Context context, String name)
    {
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);

        return context.getResources().getIdentifier("vendor_"+name,
                "drawable", context.getPackageName());
    }

}


