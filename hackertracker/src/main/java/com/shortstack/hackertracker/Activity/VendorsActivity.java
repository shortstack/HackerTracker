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
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Company;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.Renderer.VendorRenderer;

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
        return App.getApplication().getDatabaseController().getVendors();
    }
}



