package com.sbizzera.go4lunch.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sbizzera.go4lunch.OnItemBindWithRestaurantClickListener;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.FakeWorkmates;
import com.sbizzera.go4lunch.views.adapters.WorkmatesAdapter;

import java.util.List;

public class WorkmatesFragment extends Fragment {

    List<FakeWorkmates> mWorkmates = FakeWorkmates.getWorkMatesList();

    OnItemBindWithRestaurantClickListener mListener;

    public WorkmatesFragment(OnItemBindWithRestaurantClickListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workmates, container, false);

        WorkmatesAdapter adapter = new WorkmatesAdapter(mWorkmates, mListener);
        RecyclerView rcv = view.findViewById(R.id.workmates_rcv);
        rcv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv.setAdapter(adapter);


        return view;
    }
}
