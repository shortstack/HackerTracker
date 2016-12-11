package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererBuilder;
import com.shortstack.hackertracker.Model.Item;
import com.shortstack.hackertracker.Renderer.GenericHeaderRenderer;
import com.shortstack.hackertracker.Renderer.GenericTimeRenderer;
import com.shortstack.hackertracker.Renderer.ItemRenderer;

import java.util.Date;


public class GenericRowBuilder extends RendererBuilder<Item> {
    public GenericRowBuilder() {
        bind(Item.class, new ItemRenderer())
                .bind(String.class, new GenericHeaderRenderer())
                .bind(Date.class, new GenericTimeRenderer());
    }
}
