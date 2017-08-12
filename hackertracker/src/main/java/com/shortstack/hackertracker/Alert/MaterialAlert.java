package com.shortstack.hackertracker.Alert;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.shortstack.hackertracker.R;

public class MaterialAlert {

    protected AlertDialog.Builder mBuilder;
    private Context mContext;

    public static MaterialAlert create(Context context) {
        return new MaterialAlert(context);
    }

    public MaterialAlert(Context context) {
        mContext = context;
        mBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
    }

    public MaterialAlert setTitle(int title) {
        mBuilder.setTitle(getString(title));
        return this;
    }

    public MaterialAlert setTitle(String title) {
        mBuilder.setTitle(title);
        return this;
    }

    public MaterialAlert setMessage(int message) {
        setMessage(getString(message));
        return this;
    }

    public MaterialAlert setMessage(String message) {
        mBuilder.setMessage(message);
        return this;
    }

    public MaterialAlert setItems(int items, DialogInterface.OnClickListener listener) {
        mBuilder.setItems(items, listener);
        return this;
    }

    public MaterialAlert setItems(final Item[] items, DialogInterface.OnClickListener listener) {
        ListAdapter adapter = new ArrayAdapter<Item>(
                mContext,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items) {
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView) v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * mContext.getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };

        mBuilder.setAdapter(adapter, listener);
        return this;
    }

    public MaterialAlert setPositiveButton(int text, AlertDialog.OnClickListener listener) {
        mBuilder.setPositiveButton(getString(text), listener);
        return this;
    }

    public MaterialAlert setNegativeButton(int text, AlertDialog.OnClickListener listener) {
        mBuilder.setNegativeButton(getString(text), listener);
        return this;
    }

    public MaterialAlert setBasicNegativeButton(int text) {
        mBuilder.setNegativeButton(getString(text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return this;
    }

    public MaterialAlert setBasicNegativeButton() {
        setBasicNegativeButton(R.string.cancel);
        return this;
    }

    public MaterialAlert setView(View view) {
        mBuilder.setView(view);
        return this;
    }

    @NonNull
    private String getString(int text) {
        return mContext.getString(text);
    }

    public AlertDialog build() {
        //if( !mHasPositiveButton ) setBasicPositiveButton();
        return mBuilder.create();
    }

    public MaterialAlert setBasicPositiveButton() {
        mBuilder.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return this;
    }

    public MaterialAlert setDismissCallback(DialogInterface.OnDismissListener listener) {
        mBuilder.setOnDismissListener(listener);
        return this;
    }

    public void show() {
        build().show();
    }

    public static class Item {
        public final String text;
        public final int icon;

        public Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }

        public Item(String text) {
            this.text = text;
            this.icon = 0;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}