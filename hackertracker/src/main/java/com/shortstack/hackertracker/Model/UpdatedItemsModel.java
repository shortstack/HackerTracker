package com.shortstack.hackertracker.Model;

import java.util.Random;

public class UpdatedItemsModel {

    public static final int STATE_NEW = 0;
    public static final int STATE_UPDATED = 1;

    public int id;
    public int state;

    public UpdatedItemsModel() {
        Random random = new Random();
        int[] ids = new int[]{7, 178, 164, 126, 228, 208};

        id = ids[random.nextInt(ids.length)];
        state = random.nextInt(1);
    }
}
