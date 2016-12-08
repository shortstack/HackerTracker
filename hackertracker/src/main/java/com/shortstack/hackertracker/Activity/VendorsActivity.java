package com.shortstack.hackertracker.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Home.VendorRenderer;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VendorsActivity extends AppCompatActivity {
    
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.list)
    RecyclerView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_vendors);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        List<Vendor> vendors = getVendors();


        RendererBuilder rendererBuilder = new RendererBuilder().bind(Vendor.class, new VendorRenderer());

        LinearLayoutManager layout = new LinearLayoutManager(this);
        list.setLayoutManager(layout);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),
                layout.getOrientation());
        list.addItemDecoration(dividerItemDecoration);


        RendererAdapter adapter = new RendererAdapter(rendererBuilder);
        list.setAdapter(adapter);

        adapter.addAll(vendors);

    }

    public List<Vendor> getVendors() {
        ArrayList<Vendor> result = new ArrayList<>();

        SQLiteDatabase db = App.vendorDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM data ORDER BY title", new String[]{});

        try {
            if (cursor.moveToFirst()) {
                do {

                    Vendor item = new Vendor();

                    item.setId(cursor.getInt(cursor.getColumnIndex("id")));
                    item.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    item.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                    item.setImage(cursor.getString(cursor.getColumnIndex("image")));
                    item.setLink(cursor.getString(cursor.getColumnIndex("link")));

                    result.add(item);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        db.close();

        return result;
    }
}



