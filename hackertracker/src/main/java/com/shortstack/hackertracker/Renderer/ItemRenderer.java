package com.shortstack.hackertracker.Renderer;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.Alert.MaterialAlert;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Fragment.DetailsBottomSheetDialogFragment;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.R;
import com.shortstack.hackertracker.View.ItemView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ItemRenderer extends Renderer<Item> implements View.OnClickListener, View.OnLongClickListener {

    @Bind(R.id.item)
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
        rootView.setOnLongClickListener(this);
    }

    @Override
    public void render(List<Object> payloads) {
        item.setItem(getContent());
    }


    @Override
    public void onClick(View view) {
        DetailsBottomSheetDialogFragment bottomSheetDialogFragment =  DetailsBottomSheetDialogFragment.newInstance(getContent());
        bottomSheetDialogFragment.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    @Override
    public boolean onLongClick(View view) {


        MaterialAlert alert = MaterialAlert.create(getContext()).setTitle(getContent().getTitle());

        alert.setMessage(getContent().getDetailsDescription(getContext()));

        alert.setNegativeButton(R.string.action_share, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // TODO: Handle share.
            }
        });


        if( getContent().isBookmarked() ) {
            alert.setPositiveButton(R.string.unbookmark, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    App.getApplication().getDatabaseController().unbookmark(getContent());
                }
            });
        } else {
            alert.setPositiveButton(R.string.bookmark, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    App.getApplication().getDatabaseController().bookmark(getContent());
                }
            });
        }


        alert.show();
        return true;
    }

    public void updateBookmark() {
        item.updateBookmark();
    }
}
