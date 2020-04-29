package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;


import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.services.ViewModelFactory;

import java.util.ArrayList;

public class ListFragment extends Fragment {

    private ListFragmentAdapter mAdapter;
    private OnItemBoundWithRestaurantClickListener mListener;


    public static ListFragment newInstance() {
        return new ListFragment();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mAdapter = new ListFragmentAdapter(mListener);

        ListFragmentViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListFragmentViewModel.class);
        viewModel.getModel().observe(this, this::updateUI);

        RecyclerView recyclerView = view.findViewById(R.id.list_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        return view;

    }

    public void updateUI(ListFragmentModel model) {
        mAdapter.setList(model.getListAdapterModel());
        mAdapter.notifyDataSetChanged();
    }

    public void setListener(OnItemBoundWithRestaurantClickListener listener) {
        mListener = listener;
    }
}


