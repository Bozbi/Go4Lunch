package com.sbizzera.go4lunch.main_activity.fragments.workmates_fragment;

import android.content.Context;
import android.graphics.Typeface;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreLunch;
import com.sbizzera.go4lunch.model.firestore_models.FireStoreUser;
import com.sbizzera.go4lunch.services.FireStoreRepo;
import com.sbizzera.go4lunch.utils.Go4LunchUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkmatesFragmentViewModel extends ViewModel {

    private FireStoreRepo mFireStoreRepo;
    private Context mContext;
    private MediatorLiveData<WorkmatesFragmentModel> modelLiveData = new MediatorLiveData<>();

    public WorkmatesFragmentViewModel(
            FireStoreRepo fireStoreRepo,
            Context context
    ) {
        this.mFireStoreRepo = fireStoreRepo;
        mContext = context;
        wireUpMediator();
    }

    private void wireUpMediator() {
        LiveData<List<FireStoreUser>> allUsersLiveData = mFireStoreRepo.getAllUsers();
        LiveData<List<FireStoreLunch>> allTodaysLunchesLiveData = mFireStoreRepo.getAllTodaysLunches();

        modelLiveData.addSource(allUsersLiveData, allUsers -> {
            modelLiveData.postValue(combineSources(allUsers, allTodaysLunchesLiveData.getValue()));
        });

        modelLiveData.addSource(allTodaysLunchesLiveData, allTodayLunch -> {
            modelLiveData.postValue(combineSources(allUsersLiveData.getValue(), allTodayLunch));
        });
    }

    private WorkmatesFragmentModel combineSources(
            List<FireStoreUser> allUsers,
            List<FireStoreLunch> allTodayLunch
    ) {
        List<WorkmatesFragmentAdapterModel> workmatesModeList = fromUserAndLunchesToModel(allUsers, allTodayLunch);
        return new WorkmatesFragmentModel(workmatesModeList);
    }

    private List<WorkmatesFragmentAdapterModel> fromUserAndLunchesToModel(List<FireStoreUser> allUsers, List<FireStoreLunch> allTodayLunch) {
        List<WorkmatesFragmentAdapterModel> workmateModelList = new ArrayList<>();
        if (allUsers != null) {
            for (FireStoreUser user : allUsers) {
                String userFirstName = Go4LunchUtils.getUserFirstName(user.getUserName());
                WorkmatesFragmentAdapterModel workmateModel = new WorkmatesFragmentAdapterModel(
                        user.getUserAvatarUrl(),
                        userFirstName + mContext.getString(R.string.hasnt_decided),
                        false,
                        Typeface.ITALIC,
                        null
                );
                if (allTodayLunch != null && allTodayLunch.size() > 0) {
                    for (FireStoreLunch lunch : allTodayLunch) {
                        if (user.getUserId().equals(lunch.getUserId())) {
                            workmateModel.setChoice(userFirstName + mContext.getString(R.string.eat_at) + lunch.getRestaurantName());
                            workmateModel.setClickable(true);
                            workmateModel.setTextStyle(Typeface.BOLD);
                            workmateModel.setRestaurantId(lunch.getRestaurantId());
                        }
                    }
                }
                workmateModelList.add(workmateModel);
            }
        }
        Collections.sort(workmateModelList, (o1, o2) -> {
            if (o1.getClickable()) {
                return -1;
            } else {
                return 0;
            }
        });
        return workmateModelList;
    }


    public LiveData<WorkmatesFragmentModel> getModelLiveData() {
        return modelLiveData;
    }
}
