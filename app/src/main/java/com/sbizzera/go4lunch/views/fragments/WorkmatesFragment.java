package com.sbizzera.go4lunch.views.fragments;

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

import com.sbizzera.go4lunch.events.OnItemBindWithRestaurantClickListener;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.WorkmatesFragmentModel;
import com.sbizzera.go4lunch.view_models.ViewModelFactory;
import com.sbizzera.go4lunch.view_models.WorkmatesFragmentViewModel;
import com.sbizzera.go4lunch.views.adapters.WorkmatesAdapter;

public class WorkmatesFragment extends Fragment {

    private OnItemBindWithRestaurantClickListener mListener;
    private WorkmatesAdapter mAdapter;

    public WorkmatesFragment(OnItemBindWithRestaurantClickListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        WorkmatesFragmentViewModel viewModel = new ViewModelProvider(this,ViewModelFactory.getInstance()).get(WorkmatesFragmentViewModel.class);
        viewModel.getModelLiveData().observe(this, this::updateUI);
        mAdapter = new WorkmatesAdapter(mListener);

        RecyclerView rcv = view.findViewById(R.id.workmates_rcv);
        rcv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv.setAdapter(mAdapter);

        return view;
    }

    private void updateUI(WorkmatesFragmentModel model){
        mAdapter.setWorkmatesList(model.getWorkmatesList());
        mAdapter.notifyDataSetChanged();
    }
}
