package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.Model.Default;

import java.util.Date;


public class GenericRowBuilder extends RendererBuilder<Default> {
    public GenericRowBuilder() {
        bind(Default.class, new GenericDefaultRenderer())
                .bind(String.class, new GenericHeaderRenderer())
                .bind(Date.class, new GenericTimeRenderer());
    }
}
