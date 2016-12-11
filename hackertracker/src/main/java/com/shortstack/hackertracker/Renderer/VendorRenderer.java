package com.shortstack.hackertracker.Renderer;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Fragment.VendorBottomSheetDialogFragment;
import com.shortstack.hackertracker.Model.Vendor;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VendorRenderer extends Renderer<Vendor> implements View.OnClickListener {

    @Bind(R.id.title)
    TextView title;

    @Bind(R.id.description)
    TextView description;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_vendor, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void hookListeners(View rootView) {
        super.hookListeners(rootView);
        rootView.setOnClickListener(this);
    }

    @Override
    public void render(List<Object> payloads) {
        title.setText(getContent().getTitle());
        description.setText(getContent().getDescription());
    }


    @Override
    public void onClick(View view) {
        VendorBottomSheetDialogFragment bottomSheetDialogFragment =  VendorBottomSheetDialogFragment.newInstance(getContent());
        bottomSheetDialogFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }
}
