package com.shortstack.hackertracker.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.Model.Information;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Renderer.FAQRenderer;
import com.shortstack.hackertracker.Renderer.GenericHeaderRenderer;
import com.shortstack.hackertracker.Renderer.InformationRenderer;

import butterknife.BindView;
import butterknife.ButterKnife;


public class InformationActivity extends Fragment {


    @BindView(R.id.list)
    RecyclerView list;


    public static InformationActivity newInstance() {
        return new InformationActivity();
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
                .bind(String[].class, new FAQRenderer())
                .bind(String.class, new GenericHeaderRenderer())
                .bind(Information.class, new InformationRenderer());

        RendererAdapter adapter = new RendererAdapter(rendererBuilder);
        list.setAdapter(adapter);


        adapter.add(new Information(getContext(), R.array.location_information));
        adapter.add(new Information(getContext(), R.array.badge_information));
        adapter.add(new Information(getContext(), R.array.workshop_information));
        adapter.add(new Information(getContext(), R.array.wifi_information));
        adapter.add(new Information(getContext(), R.array.radio_information));
        // radio
        // workshop
        // location/time
        //

        adapter.add("FAQ");


        String[] myItems = getResources().getStringArray(R.array.faq_questions);

        for (int i = 0; i < myItems.length - 1; i += 2) {
            String[] update = new String[2];

            update[0] = myItems[i];
            update[1] = myItems[i + 1];

            adapter.add(update);
        }
    }
}
