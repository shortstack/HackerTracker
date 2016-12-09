package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererAdapter;
import com.shortstack.hackertracker.Model.Item;

import java.util.Date;
import java.util.List;

public class GenericRowAdapter extends RendererAdapter<Item> {
    public GenericRowAdapter() {
        super(new GenericRowBuilder());
    }

    public void notifyItemUpdated(int item) {
        List collection = getCollection();
        for (int i = 0; i < collection.size(); i++) {
            if( collection.get(i) instanceof Item) {
                Item def = (Item) collection.get(i);
                if (def.getId() == item) {
                    notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void notifyTimeChanged() {

        List collection = getCollection();
        boolean hasRemovedEvent = false;

        for (int i = collection.size() - 1; i >= 0; i--) {
            if( collection.get(i) instanceof Date ) {
                notifyItemChanged(i);
            }

            if( collection.get(i) instanceof Item) {
                Item def = (Item) collection.get(i);

                if( def.hasExpired() ) {
                    collection.remove(i);
                    notifyItemRemoved(i);

                    hasRemovedEvent = true;


                }
            }
        }


        if( hasRemovedEvent ) {
            for (int i = collection.size() - 1; i > 0; i--) {
                if( collection.get(i) instanceof Date && collection.get(i-1) instanceof Date
                        || collection.get(i) instanceof String && collection.get(i - 1) instanceof String
                        || collection.get(i) instanceof String && collection.get(i - 1) instanceof Date) {
                    collection.remove(i - 1);
                    notifyItemRemoved(i - 1);
                }
            }
        }
    }
}
