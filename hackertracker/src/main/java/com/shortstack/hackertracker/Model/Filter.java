package com.shortstack.hackertracker.Model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Filter {

    private String[] mTypes;
    private boolean mShowStarred;

    public Filter( String[] types, boolean showStarred ) {
        mTypes = types;
        mShowStarred = showStarred;
    }

    public Filter( Set<String> types, boolean showStarred ) {
        this(types.toArray(new String[types.size()]), showStarred);
    }

    public String[] getTypesArray (){
        return mTypes;
    }

    public Set<String> getTypesSet() {
        return new HashSet<>(Arrays.asList(mTypes));
    }

    public boolean isShowingStarred() {
        return mShowStarred;
    }
}
