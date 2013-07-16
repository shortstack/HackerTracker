package com.shortstack.hackertracker.Activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.shortstack.hackertracker.Adapter.VendorAdapter;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;
import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Whitney Champion
 * Date: 8/29/12
 * Time: 2:25 PM
 */
public class Vendors extends HackerTracker {

    public Vendor[] vendorData;
    public VendorAdapter adapter;
    public ListView vendorsList;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendors);


        // populate vendors
        List<Vendor> vendors = getVendors();
        if (!(vendors.size() < 1)) {

            vendorData = vendors.toArray(new Vendor[vendors.size()]);

            adapter = new VendorAdapter(this, R.layout.vendor_row, vendorData);

            vendorsList = (ListView) findViewById(R.id.vendors);

            vendorsList.setAdapter(adapter);
        }



    }


    // get list of vendors
        public List<Vendor> getVendors() {
        ArrayList<Vendor> result = new ArrayList<Vendor>();
        SQLiteDatabase db = myDbHelper.getWritableDatabase();

        Cursor myCursor = db.rawQuery("SELECT * FROM vendors", null);

        try{
            if (myCursor.moveToFirst()){
                do{
                    Vendor vendor = new Vendor();
                    vendor.setTitle(myCursor.getString((myCursor.getColumnIndex("title"))));
                    vendor.setBody(myCursor.getString((myCursor.getColumnIndex("body"))));
                    vendor.setWebsite(myCursor.getString((myCursor.getColumnIndex("website"))));
                    vendor.setLogo(myCursor.getString(myCursor.getColumnIndex("logo")));

                    result.add(vendor);
                }while(myCursor.moveToNext());
            }
        }finally{
            myCursor.close();
        }
        db.close();
        return result;
    }










}
