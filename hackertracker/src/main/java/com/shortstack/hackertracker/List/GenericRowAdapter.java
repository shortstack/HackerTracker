package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererAdapter;
import com.shortstack.hackertracker.Model.Default;

public class GenericRowAdapter extends RendererAdapter<Default> {
    public GenericRowAdapter() {
        super(new GenericRowBuilder());
    }
}
