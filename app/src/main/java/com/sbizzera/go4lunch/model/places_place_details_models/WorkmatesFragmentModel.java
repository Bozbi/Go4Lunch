package com.sbizzera.go4lunch.model.places_place_details_models;

import com.sbizzera.go4lunch.model.WorkmatesAdapterModel;

import java.util.List;

public class WorkmatesFragmentModel {
    private List<WorkmatesAdapterModel> workmatesList;

    public WorkmatesFragmentModel(List<WorkmatesAdapterModel> workmatesList) {
        this.workmatesList = workmatesList;
    }

    public List<WorkmatesAdapterModel> getWorkmatesList() {
        return workmatesList;
    }

    public void setWorkmatesList(List<WorkmatesAdapterModel> workmatesList) {
        this.workmatesList = workmatesList;
    }
}
