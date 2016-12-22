package com.shortstack.hackertracker.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Renderer.InfoRenderer;


public class InformationActivity extends ListActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        RendererBuilder rendererBuilder = new RendererBuilder().bind(String.class, new InfoRenderer());

        RendererAdapter adapter = new RendererAdapter(rendererBuilder);
        list.setAdapter(adapter);

        String[] myItems = getResources().getStringArray(R.array.information_headers);

        for (int i = 0; i < myItems.length; i++) {
            adapter.add(myItems[i]);
        }
    }
}
