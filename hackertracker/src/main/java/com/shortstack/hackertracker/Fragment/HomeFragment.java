package com.shortstack.hackertracker.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;
import com.pedrogomez.renderers.RendererContent;
import com.shortstack.hackertracker.Model.Navigation;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Renderer.ActivityNavRenderer;
import com.shortstack.hackertracker.Renderer.FAQRenderer;
import com.shortstack.hackertracker.Renderer.GenericHeaderRenderer;
import com.shortstack.hackertracker.Renderer.HomeHeaderRenderer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {

    private static final int TYPE_HEADER = 0;

    @BindView(R.id.list)
    RecyclerView list;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, rootView);

        RendererBuilder rendererBuilder = new RendererBuilder()
                .bind(TYPE_HEADER, new HomeHeaderRenderer())
                .bind(String.class, new GenericHeaderRenderer())
                .bind(String[].class, new FAQRenderer())
                .bind(Navigation.class, new ActivityNavRenderer())

                ;

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        list.setLayoutManager(layout);

        RendererAdapter adapter = new RendererAdapter(rendererBuilder);
        list.setAdapter(adapter);


        adapter.add(new RendererContent<Void>(null, TYPE_HEADER)); // Skull
        adapter.add(new Navigation("Looking for something?", "Check out the information section", InformationFragment.class ));

        String[] myItems = getResources().getStringArray(R.array.updates);


        for (int i = 0; i < myItems.length - 1; i+=2) {
            String[] update = new String[2];

            update[0] = myItems[i];
            update[1] = myItems[i+1];

            adapter.add(update);
        }

        return rootView;
    }

}
