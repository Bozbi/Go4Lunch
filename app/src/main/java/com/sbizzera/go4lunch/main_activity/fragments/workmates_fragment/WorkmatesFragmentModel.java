package com.sbizzera.go4lunch.main_activity.fragments.workmates_fragment;

import com.sbizzera.go4lunch.main_activity.fragments.workmates_fragment.WorkmatesFragmentAdapterModel;

import java.util.List;

public class WorkmatesFragmentModel {
    private List<WorkmatesFragmentAdapterModel> workmatesList;

    public WorkmatesFragmentModel(List<WorkmatesFragmentAdapterModel> workmatesList) {
        this.workmatesList = workmatesList;
    }

    public List<WorkmatesFragmentAdapterModel> getWorkmatesList() {
        return workmatesList;
    }

    public void setWorkmatesList(List<WorkmatesFragmentAdapterModel> workmatesList) {
        this.workmatesList = workmatesList;
    }
}
