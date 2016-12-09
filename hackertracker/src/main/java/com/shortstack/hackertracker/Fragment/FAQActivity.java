package com.shortstack.hackertracker.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.Home.FAQRenderer;
import com.shortstack.hackertracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FAQActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.list)
    RecyclerView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
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

        RendererBuilder rendererBuilder = new RendererBuilder().bind(String[].class, new FAQRenderer());

        LinearLayoutManager layout = new LinearLayoutManager(this);
        list.setLayoutManager(layout);

        RendererAdapter adapter = new RendererAdapter(rendererBuilder);
        list.setAdapter(adapter);

        String[] myItems = getResources().getStringArray(R.array.faq_questions);

        for (int i = 0; i < myItems.length - 1; i += 2) {
            String[] update = new String[2];

            update[0] = myItems[i];
            update[1] = myItems[i + 1];

            adapter.add(update);
        }
    }
}



