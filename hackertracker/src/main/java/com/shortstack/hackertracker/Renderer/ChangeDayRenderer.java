package com.shortstack.hackertracker.Renderer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererContent;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Event.UpdateListContentsEvent;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeDayRenderer extends Renderer<RendererContent<String>> {
    @BindView(R.id.button)
    Button view;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_feed_day, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        //view.setText(getContent().getItem());
    }

    @OnClick(R.id.button)
    public void onButtonClick() {
        App.getStorage().setScheduleDay(App.getStorage().getScheduleDay() + 1);
        App.getApplication().postBusEvent(new UpdateListContentsEvent());
    }
}
