package com.shortstack.hackertracker.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.*;
import android.widget.*;
import com.shortstack.hackertracker.Activity.Vendors;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;

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

            holder.title = (TextView) row.findViewById(R.id.vendor_title);
            holder.website = (TextView) row.findViewById(R.id.vendor_website);
            holder.body = (TextView) row.findViewById(R.id.vendor_body);
            holder.logo = (ImageView) row.findViewById(R.id.vendor_logo);

            holder.vendorLayout = (LinearLayout) row.findViewById(R.id.vendorLayout);
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
            holder.website.setText(vendor.getWebsite());

            // set body
            holder.body.setText(vendor.getBody());

            // set logo
            String logo = vendor.getLogo();
            int resID = context.getResources().getIdentifier(logo , "id", "com.shortstack.hackertracker");
            holder.logo.setBackgroundResource(resID);


        } else {
            holder.title.setText("No Vendors found");
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
}


