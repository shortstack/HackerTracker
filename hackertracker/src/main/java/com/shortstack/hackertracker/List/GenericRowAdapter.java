package com.shortstack.hackertracker.List;

import com.pedrogomez.renderers.RendererAdapter;
import com.shortstack.hackertracker.Application.App;
import com.shortstack.hackertracker.Model.Item;

import java.util.Date;
import java.util.List;

public class GenericRowAdapter extends RendererAdapter<Item> {
    public GenericRowAdapter() {
        super(new GenericRowBuilder());
    }

    public void notifyTimeChanged() {

        if(App.getStorage().showExpiredEvents())
            return;

        List collection = getCollection();
        boolean hasRemovedEvent = false;

        for (int i = collection.size() - 1; i >= 0; i--) {
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

            // If no events and only headers remain.
            if( collection.size() == 2 ) {
                if( collection.get(0) instanceof String && collection.get(1) instanceof Date ) {
                    collection.clear();
                    notifyItemRangeRemoved(0, 2);
                }
            }
        }
    }
}
