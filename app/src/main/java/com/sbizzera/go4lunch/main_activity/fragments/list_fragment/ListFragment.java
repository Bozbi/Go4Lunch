package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.main_activity.OnItemBoundWithRestaurantClickListener;

import com.sbizzera.go4lunch.utils.ViewModelFactory;

import com.sbizzera.go4lunch.main_activity.RestaurantClickedListenable;


public class ListFragment extends Fragment implements RestaurantClickedListenable {

    private ListFragmentAdapter mAdapter;
    private OnItemBoundWithRestaurantClickListener mListener;
    private ChipGroup chipGroup;
    private ListFragmentViewModel viewModel;


    public static ListFragment newInstance() {
        return new ListFragment();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mAdapter = new ListFragmentAdapter(mListener);

        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListFragmentViewModel.class);
        viewModel.getModel().observe(this, this::updateUI);

        RecyclerView recyclerView = view.findViewById(R.id.list_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        chipGroup = view.findViewById(R.id.chip_group);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            viewModel.setSelectedChipID(checkedId);
            recyclerView.scrollToPosition(0);
        });

        getActivity().setTitle(getString(R.string.list_title_bar_title));

        return view;

    }

    public void updateUI(ListFragmentModel model) {
        mAdapter.setList(model.getListAdapterModel());
        mAdapter.notifyDataSetChanged();
    }

    public void setListener(OnItemBoundWithRestaurantClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(viewModel.getModel().getValue()!=null&&viewModel.getModel().getValue().getSortId()!=null && viewModel.getModel().getValue().getSortId()!=1){
            Chip chipToCheck = chipGroup.findViewById(viewModel.getModel().getValue().getSortId());
            chipToCheck.setChecked(true);
        }else{
            Chip chipToCheck = chipGroup.findViewById(R.id.distance_chip);
            chipToCheck.setChecked(true);
        }
    }
}


