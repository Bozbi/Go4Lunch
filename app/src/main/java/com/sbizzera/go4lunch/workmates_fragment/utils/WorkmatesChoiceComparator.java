package com.sbizzera.go4lunch.workmates_fragment.utils;

import com.sbizzera.go4lunch.workmates_fragment.models.WorkmatesFragmentAdapterModel;

import java.util.Comparator;

public class WorkmatesChoiceComparator implements Comparator<WorkmatesFragmentAdapterModel> {
    @Override
    public int compare(WorkmatesFragmentAdapterModel o1, WorkmatesFragmentAdapterModel o2) {
        if (o1.getClickable()&&o2.getClickable()){
            return o1.getChoice().compareTo(o2.getChoice());
        }if(o1.getClickable()){
            return -1;
        }if (o2.getClickable()){
            return 1;
        }
        return o1.getChoice().compareTo(o2.getChoice());
    }
}
