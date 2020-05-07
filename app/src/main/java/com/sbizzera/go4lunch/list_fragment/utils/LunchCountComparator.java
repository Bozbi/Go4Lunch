package com.sbizzera.go4lunch.list_fragment.utils;

import com.sbizzera.go4lunch.list_fragment.models.ListFragmentAdapterModel;

import java.util.Comparator;

public class LunchCountComparator implements Comparator<ListFragmentAdapterModel> {
    @Override
    public int compare(ListFragmentAdapterModel o1, ListFragmentAdapterModel o2) {
        return o2.getWorkmatesLunchesCount().compareTo(o1.getWorkmatesLunchesCount());
    }
}
