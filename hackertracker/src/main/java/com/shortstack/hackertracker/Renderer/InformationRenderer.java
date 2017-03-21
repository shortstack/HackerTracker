package com.shortstack.hackertracker.Renderer;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.BottomSheet.InformationBottomSheetDialogFragment;
import com.shortstack.hackertracker.Model.Information;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InformationRenderer extends Renderer<Information> implements View.OnClickListener {

    @BindView(R.id.header)
    TextView header;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_info, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void hookListeners(View rootView) {
        rootView.setOnClickListener(this);
    }

    @Override
    public void render(List<Object> payloads) {
        header.setText(getContent().getTitle());
    }


    @Override
    public void onClick(View view) {
        InformationBottomSheetDialogFragment badges = InformationBottomSheetDialogFragment.newInstance(getContent());
        badges.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), badges.getTag());
    }
}
