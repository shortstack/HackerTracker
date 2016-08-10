package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.Model.Default;


public class GenericRowBuilder extends RendererBuilder<Default> {
    public GenericRowBuilder() {
        this.bind(Default.class, new GenericDefaultRenderer());
    }
}
