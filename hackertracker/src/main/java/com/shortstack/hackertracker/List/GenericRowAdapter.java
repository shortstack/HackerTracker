package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererAdapter;
import com.shortstack.hackertracker.Model.Item;

import java.util.List;

public class GenericRowAdapter extends RendererAdapter<Item> {
    public GenericRowAdapter() {
        super(new GenericRowBuilder());
    }

    public void notifyItemUpdated(int item) {
        List<Item> collection = getCollection();
        for (int i = 0; i < collection.size(); i++) {
            if( collection.get(i) instanceof Item) {
                Item def = collection.get(i);
                if (def.getId() == item) {
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }
}
