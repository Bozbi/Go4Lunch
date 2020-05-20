package com.sbizzera.go4lunch.list_fragment.utils;

import com.sbizzera.go4lunch.list_fragment.models.ListFragmentAdapterModel;

import java.util.Comparator;

public class LunchCountComparator implements Comparator<ListFragmentAdapterModel> {
    @Override
    public int compare(ListFragmentAdapterModel o1, ListFragmentAdapterModel o2) {
        int o1LunchCount = Integer.parseInt(o1.getWorkmatesLunchesCount());
        int o2LunchCount = Integer.parseInt(o2.getWorkmatesLunchesCount());
        return o2LunchCount - (o1LunchCount);
    }
}
