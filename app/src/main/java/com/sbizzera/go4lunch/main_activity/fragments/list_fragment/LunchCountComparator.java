package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;

import java.util.Comparator;

public class LunchCountComparator implements Comparator<ListFragmentAdapterModel> {
    @Override
    public int compare(ListFragmentAdapterModel o1, ListFragmentAdapterModel o2) {
        return o2.getWorkmatesLunchesCount().compareTo(o1.getWorkmatesLunchesCount());
    }
}
