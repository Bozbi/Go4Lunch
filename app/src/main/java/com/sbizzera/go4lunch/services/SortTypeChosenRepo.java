package com.sbizzera.go4lunch.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SortTypeChosenRepo {

    private static SortTypeChosenRepo sSortTypeChosenRepo;
    private MutableLiveData<Integer> mSortTypeChoseLD = new MutableLiveData<>();

    private SortTypeChosenRepo(){
        mSortTypeChoseLD.setValue(1);
    }

    public static SortTypeChosenRepo getInstance(){
        if(sSortTypeChosenRepo==null){
            sSortTypeChosenRepo = new SortTypeChosenRepo();
        }
        return sSortTypeChosenRepo;
    }

    public LiveData<Integer> getSelectedChipID(){
        return mSortTypeChoseLD;
    }

    public void setSelectedChipID(int checkedId) {
        mSortTypeChoseLD.setValue(checkedId);
    }
}
