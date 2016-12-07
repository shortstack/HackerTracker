package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererAdapter;
import com.shortstack.hackertracker.Model.Default;

import java.util.List;

public class GenericRowAdapter extends RendererAdapter<Default> {
    public GenericRowAdapter() {
        super(new GenericRowBuilder());
    }

    public void notifyItemUpdated(int item) {
        List<Default> collection = getCollection();
        for (int i = 0; i < collection.size(); i++) {
            if( collection.get(i) instanceof Default ) {
                Default def = collection.get(i);
                if (def.getId() == item) {
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }
}
