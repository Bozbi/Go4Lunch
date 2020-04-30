package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;

import java.util.Comparator;

class RestaurantNameComparator implements Comparator<ListFragmentAdapterModel> {
    @Override
    public int compare(ListFragmentAdapterModel o1, ListFragmentAdapterModel o2) {
        return o1.getRestaurantName().compareTo(o2.getRestaurantName());
    }
}
