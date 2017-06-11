package com.shortstack.hackertracker.Renderer;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.BottomSheet.ScheduleItemBottomSheetDialogFragment;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.ItemView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ItemRenderer extends Renderer<Item> implements View.OnClickListener {

    @BindView(R.id.item)
    ItemView item;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void hookListeners(View rootView) {
        rootView.setOnClickListener(this);
    }

    @Override
    public void render(List<Object> payloads) {
        item.setItem(getContent());
    }


    @Override
    public void onClick(View view) {
        ScheduleItemBottomSheetDialogFragment bottomSheetDialogFragment =  ScheduleItemBottomSheetDialogFragment.Companion.newInstance(getContent());
        bottomSheetDialogFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }
}
