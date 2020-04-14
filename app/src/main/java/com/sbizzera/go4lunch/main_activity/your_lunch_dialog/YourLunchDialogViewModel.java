package com.sbizzera.go4lunch.main_activity.your_lunch_dialog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.model.firestore_models.FireStoreLunch;
import com.sbizzera.go4lunch.services.FireStoreService;

public class YourLunchDialogViewModel extends ViewModel {

    private LiveData<YourLunchDialogModel> modelLiveData;
    private FireStoreService fireStoreService;

    public YourLunchDialogViewModel(FireStoreService fireStoreService) {
        this.fireStoreService = fireStoreService;
        wireUp();
    }

    private void wireUp() {
        modelLiveData = Transformations.map(fireStoreService.getUserLunch(), this::createLunchText);
    }

    private YourLunchDialogModel createLunchText(FireStoreLunch userLunch) {
        if (userLunch == null){
            return new YourLunchDialogModel("",true,"fakeid");
        }else{
            return new YourLunchDialogModel("",false,"fakeid");
        }
    }


    public LiveData<YourLunchDialogModel> getModel() {
        return modelLiveData;
    }

}
