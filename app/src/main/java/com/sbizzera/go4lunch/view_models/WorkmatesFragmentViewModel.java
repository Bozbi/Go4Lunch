package com.sbizzera.go4lunch.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.model.WorkmatesAdapterModel;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreLunch;
import com.sbizzera.go4lunch.model.firestore_database_models.FireStoreUser;
import com.sbizzera.go4lunch.model.places_place_details_models.WorkmatesFragmentModel;
import com.sbizzera.go4lunch.services.FireStoreService;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragmentViewModel extends ViewModel {

    public static final String TAG = "WorkmatesFragVM";

    private FireStoreService fireStoreService;
    private MediatorLiveData<WorkmatesFragmentModel> modelLiveData = new MediatorLiveData<>();

    WorkmatesFragmentViewModel(FireStoreService fireStoreService) {
        this.fireStoreService = fireStoreService;
        wireUpMediator();
    }

    private void wireUpMediator() {
        LiveData<List<FireStoreUser>> allUsersLiveData = fireStoreService.getAllUsers();
        LiveData<List<FireStoreLunch>> allTodaysLunchesLiveData = fireStoreService.getAllTodaysLunches();

        modelLiveData.addSource(allUsersLiveData, allUsers -> {
            modelLiveData.postValue(combineSources(allUsers, allTodaysLunchesLiveData.getValue()));
        });

        modelLiveData.addSource(allTodaysLunchesLiveData, allTodayLunch -> {
            modelLiveData.postValue(combineSources(allUsersLiveData.getValue(), allTodayLunch));
        });
    }

    private WorkmatesFragmentModel combineSources(List<FireStoreUser> allUsers, List<FireStoreLunch> allTodayLunch) {
        List<WorkmatesAdapterModel> workmatesModeList = fromUserAndLunchesToModel(allUsers, allTodayLunch);

        return new WorkmatesFragmentModel(
                workmatesModeList
        );
    }

    private List<WorkmatesAdapterModel> fromUserAndLunchesToModel(List<FireStoreUser> allUsers, List<FireStoreLunch> allTodayLunch) {
        List<WorkmatesAdapterModel> workmateModelList = new ArrayList<>();
        if (allUsers != null) {
            for (FireStoreUser user : allUsers) {
                WorkmatesAdapterModel workmateModel = new WorkmatesAdapterModel(
                        user.getUserAvatarUrl(),
                        user.getUserName() + " hasn't decided yet!"
                );
                if (allTodayLunch != null && allTodayLunch.size() > 0) {
                    for (FireStoreLunch lunch : allTodayLunch) {
                        if (user.getUserId().equals(lunch.getUserId())) {
                            workmateModel.setChoice(lunch.getUserName() + " eats at " + lunch.getRestaurantName());
                        }
                    }
                }
                workmateModelList.add(workmateModel);
            }
        }
        return workmateModelList;
    }


    public LiveData<WorkmatesFragmentModel> getModelLiveData() {
        return modelLiveData;
    }
}
