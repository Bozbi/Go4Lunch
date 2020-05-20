package com.sbizzera.go4lunch.workmates_fragment.models;

import java.util.List;

public class WorkmatesFragmentModel {
    private List<WorkmatesFragmentAdapterModel> workmatesList;

    public WorkmatesFragmentModel(List<WorkmatesFragmentAdapterModel> workmatesList) {
        this.workmatesList = workmatesList;
    }

    public List<WorkmatesFragmentAdapterModel> getWorkmatesList() {
        return workmatesList;
    }

}
