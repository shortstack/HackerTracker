package com.shortstack.hackertracker.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.orhanobut.logger.Logger;
import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Company;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Renderer.VendorRenderer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VendorsActivity extends Fragment {

    @BindView(R.id.list)
    RecyclerView list;

    public static VendorsActivity newInstance() {
        
        Bundle args = new Bundle();
        
        VendorsActivity fragment = new VendorsActivity();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        list.setLayoutManager(layout);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
//                layout.getOrientation());
//        list.addItemDecoration(dividerItemDecoration);

        RendererBuilder rendererBuilder = new RendererBuilder()
                .bind(Company.class, new VendorRenderer());

        RendererAdapter adapter = new RendererAdapter(rendererBuilder);
        list.setAdapter(adapter);

        adapter.addAll(getVendors());
    }

    public List<Company> getVendors() {
        ArrayList<Company> result = new ArrayList<>();

        SQLiteDatabase db = App.getApplication().vendorDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM data", new String[]{});

        try {
            if (cursor.moveToFirst()) {
                do {

                    Company item = new Company();

                    item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                    item.setLink(cursor.getString(cursor.getColumnIndex("link")));
                    item.setPartner(cursor.getInt(cursor.getColumnIndex("partner")));

                    result.add(item);
                } while (cursor.moveToNext());
            }
        } catch (IllegalStateException ex) {
            Logger.e("Could not get vendors!");

            CrashlyticsCore core = Crashlytics.getInstance().core;
            core.log("Fail on getVendors: " + ex.getMessage());

            if( cursor != null ) {
                core.log("Cursor is not null.");
                core.log("Title of Vendor is " + cursor.getString(cursor.getColumnIndex("title")));
            }


        }finally {
            cursor.close();
        }
        db.close();

        return result;
    }
}



