package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.Model.Item;

import java.util.Date;


public class GenericRowBuilder extends RendererBuilder<Item> {
    public GenericRowBuilder() {
        bind(Item.class, new GenericDefaultRenderer())
                .bind(String.class, new GenericHeaderRenderer())
                .bind(Date.class, new GenericTimeRenderer());
    }
}
