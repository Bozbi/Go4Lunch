package com.sbizzera.go4lunch.list_fragment.utils;

import com.sbizzera.go4lunch.list_fragment.models.ListFragmentAdapterModel;

import java.util.Comparator;

public class DistanceComparator implements Comparator<ListFragmentAdapterModel> {
    @Override
    public int compare(ListFragmentAdapterModel o1, ListFragmentAdapterModel o2) {
        if (o1.getDistance() != null && o2.getDistance() != null) {
            return o1.getDistance().compareTo(o2.getDistance());
        }
        return -2;
    }
}
