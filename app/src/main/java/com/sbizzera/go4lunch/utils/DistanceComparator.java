package com.sbizzera.go4lunch.utils;

import com.sbizzera.go4lunch.model.ListFragmentAdapterModel;

import java.util.Comparator;

public class DistanceComparator implements Comparator<ListFragmentAdapterModel> {
    @Override
    public int compare(ListFragmentAdapterModel o1, ListFragmentAdapterModel o2) {
        if (o1.getDistance() != null && o2.getDistance() != null) {
            Integer o1int = Integer.parseInt(o1.getDistance());
            Integer o2int = Integer.parseInt(o2.getDistance());
            return o1int.compareTo(o2int);
        }
        return 0;
    }
}
