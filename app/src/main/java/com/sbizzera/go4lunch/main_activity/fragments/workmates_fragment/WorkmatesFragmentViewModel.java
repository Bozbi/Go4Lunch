package com.sbizzera.go4lunch.main_activity.fragments.workmates_fragment;

import android.graphics.Typeface;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.model.firestore_models.FireStoreLunch;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreUser;
import com.sbizzera.go4lunch.services.FireStoreService;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WorkmatesFragmentViewModel extends ViewModel {

    public static final String TAG = "WorkmatesFragVM";

    private FireStoreService fireStoreService;
    private MediatorLiveData<WorkmatesFragmentModel> modelLiveData = new MediatorLiveData<>();

    public WorkmatesFragmentViewModel(FireStoreService fireStoreService) {
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
        List<WorkmatesFragmentAdapterModel> workmatesModeList = fromUserAndLunchesToModel(allUsers, allTodayLunch);

        return new WorkmatesFragmentModel(
                workmatesModeList
        );
    }

    private List<WorkmatesFragmentAdapterModel> fromUserAndLunchesToModel(List<FireStoreUser> allUsers, List<FireStoreLunch> allTodayLunch) {
        List<WorkmatesFragmentAdapterModel> workmateModelList = new ArrayList<>();
        if (allUsers != null) {
            for (FireStoreUser user : allUsers) {
                String userFirstName = Go4LunchUtils.getUserFirstName(user.getUserName());
                WorkmatesFragmentAdapterModel workmateModel = new WorkmatesFragmentAdapterModel(
                        user.getUserAvatarUrl(),
                        userFirstName + " hasn't decided yet!",
                        false,
                        Typeface.ITALIC,
                        null
                );
                if (allTodayLunch != null && allTodayLunch.size() > 0) {
                    for (FireStoreLunch lunch : allTodayLunch) {
                        if (user.getUserId().equals(lunch.getUserId())) {
                            workmateModel.setChoice(userFirstName + " eats at " + lunch.getRestaurantName());
                            workmateModel.setClickable(true);
                            workmateModel.setTextStyle(Typeface.BOLD);
                            workmateModel.setRestaurantId(lunch.getRestaurantId());
                        }
                    }
                }
                workmateModelList.add(workmateModel);
            }
        }
       Collections.sort(workmateModelList, new Comparator<WorkmatesFragmentAdapterModel>() {
           @Override
           public int compare(WorkmatesFragmentAdapterModel o1, WorkmatesFragmentAdapterModel o2) {
               if (o1.getClickable()){
                   return -1;
               }else{
               return 0;}
           }
       });
        return workmateModelList;
    }


    public LiveData<WorkmatesFragmentModel> getModelLiveData() {
        return modelLiveData;
    }
}
