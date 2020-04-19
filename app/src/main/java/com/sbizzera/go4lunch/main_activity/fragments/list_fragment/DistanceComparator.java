package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;

import java.util.Comparator;

public class DistanceComparator implements Comparator<ListFragmentAdapterModel> {
    @Override
    public int compare(ListFragmentAdapterModel o1, ListFragmentAdapterModel o2) {
        if (o1.getDistance() != null && o2.getDistance()!=null) {
            Double o1double =Double.parseDouble(o1.getDistance());
            Double o2double =Double.parseDouble(o2.getDistance());
            return o1double.compareTo(o2double);
        }
        return 0;
    }
}
