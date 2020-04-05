package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;

import com.sbizzera.go4lunch.main_activity.fragments.list_fragment.ListFragmentAdapterModel;

import java.util.List;

public class ListFragmentModel {
    private final List<ListFragmentAdapterModel> listAdapterModel;

    public ListFragmentModel(List<ListFragmentAdapterModel> listAdapterModel) {
        this.listAdapterModel = listAdapterModel;
    }

    public List<ListFragmentAdapterModel> getListAdapterModel() {
        return listAdapterModel;
    }

}
