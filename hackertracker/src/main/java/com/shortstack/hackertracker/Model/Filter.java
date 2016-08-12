package com.shortstack.hackertracker.Model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Filter {

    private String[] mTypes;

    public Filter(String[] types) {
        mTypes = types;
    }

    public Filter(Set<String> types) {
        this(types.toArray(new String[types.size()]));
    }

    public String[] getTypesArray() {
        return mTypes;
    }

    public Set<String> getTypesSet() {
        return new HashSet<>(Arrays.asList(mTypes));
    }

}
