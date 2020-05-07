package com.sbizzera.go4lunch.list_fragment.models;

import java.util.List;

import javax.annotation.Nullable;

public class ListFragmentModel {
    private final List<ListFragmentAdapterModel> listAdapterModel;
    @Nullable
    private Integer sortId;

    public ListFragmentModel(List<ListFragmentAdapterModel> listAdapterModel,int sortId) {
        this.listAdapterModel = listAdapterModel;
        this.sortId = sortId;
    }

    public List<ListFragmentAdapterModel> getListAdapterModel() {
        return listAdapterModel;
    }

    @Nullable
    public Integer getSortId() {
        return sortId;
    }
}
