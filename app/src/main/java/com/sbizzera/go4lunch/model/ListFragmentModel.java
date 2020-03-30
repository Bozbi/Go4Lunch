package com.sbizzera.go4lunch.model;

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
