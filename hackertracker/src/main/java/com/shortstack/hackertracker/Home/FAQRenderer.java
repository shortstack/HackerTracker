package com.shortstack.hackertracker.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;
import com.shortstack.hackertracker.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FAQRenderer extends Renderer<String[]> {

    public static final int POS_QUESTION = 0;
    public static final int POS_ANSWER = 1;

    @Bind(R.id.question)
    TextView question;

    @Bind(R.id.answer)
    TextView answer;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.row_faq, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render(List<Object> payloads) {
        question.setText(getContent()[POS_QUESTION]);
        answer.setText(getContent()[POS_ANSWER]);
    }
}
