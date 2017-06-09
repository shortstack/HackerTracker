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
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.Model.Navigation;
import com.shortstack.hackertracker.Model.UpdatedItemsModel;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Renderer.ActivityNavRenderer;
import com.shortstack.hackertracker.Renderer.FAQRenderer;
import com.shortstack.hackertracker.Renderer.GenericHeaderRenderer;
import com.shortstack.hackertracker.Renderer.HomeHeaderRenderer;
import com.shortstack.hackertracker.Renderer.ItemRenderer;

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
                .bind(Item.class, new ItemRenderer())
                .bind(Navigation.class, new ActivityNavRenderer())

                ;

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        list.setLayoutManager(layout);

        RendererAdapter adapter = new RendererAdapter(rendererBuilder);
        list.setAdapter(adapter);


        adapter.add(new RendererContent<Void>(null, TYPE_HEADER)); // Skull
        adapter.add(new Navigation("Looking for something?", "Check out the information section", InformationFragment.class ));


        adapter.add("Updates");



        int i1 = 10;

        String[] myItems = new String[i1];


        for (int i = 0; i < myItems.length; i++) {
            String[] update = new String[2];

            UpdatedItemsModel model = new UpdatedItemsModel();



            update[0] = model.state == UpdatedItemsModel.STATE_NEW ? "NEW" : "UPDATED";
            Item scheduleItem = App.getApplication().getDatabaseController().getScheduleItem(model.id);
            update[1] = scheduleItem.getTitle();

//            adapter.add(update);

            adapter.add(scheduleItem);
        }

        return rootView;
    }

}
