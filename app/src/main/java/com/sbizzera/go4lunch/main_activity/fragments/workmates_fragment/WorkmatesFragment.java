package com.sbizzera.go4lunch.main_activity.fragments.workmates_fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.R;
<<<<<<< HEAD
import com.sbizzera.go4lunch.utils.ViewModelFactory;
=======
import com.sbizzera.go4lunch.main_activity.MainActivity;
import com.sbizzera.go4lunch.main_activity.RestaurantClickedListenable;
import com.sbizzera.go4lunch.services.ViewModelFactory;
>>>>>>> review_nino

public class WorkmatesFragment extends Fragment implements RestaurantClickedListenable {

    private OnItemBoundWithRestaurantClickListener mListener;
    private WorkmatesFragmentAdapter mAdapter;

    public static WorkmatesFragment newInstance() {
        Bundle args = new Bundle();
        WorkmatesFragment fragment = new WorkmatesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        WorkmatesFragmentViewModel viewModel = new ViewModelProvider(this,ViewModelFactory.getInstance()).get(WorkmatesFragmentViewModel.class);
        viewModel.getModelLiveData().observe(this, this::updateUI);
        mAdapter = new WorkmatesFragmentAdapter(mListener);

        RecyclerView rcv = view.findViewById(R.id.workmates_rcv);
        rcv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv.setAdapter(mAdapter);

        getActivity().setTitle(getString(R.string.workmate_title_bar_title));
        return view;
    }

    private void updateUI(WorkmatesFragmentModel model){
        mAdapter.setWorkmatesList(model.getWorkmatesList());
        mAdapter.notifyDataSetChanged();
    }

    public void setListener(OnItemBoundWithRestaurantClickListener listener) {
        mListener = listener;
    }
}
