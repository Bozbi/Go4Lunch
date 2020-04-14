package com.sbizzera.go4lunch.main_activity.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.sbizzera.go4lunch.R;

import timber.log.Timber;

public class NoPermissionFragment extends DialogFragment {

    public static NoPermissionFragment newInstance() {
        Bundle args = new Bundle();
        NoPermissionFragment fragment = new NoPermissionFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_no_permission,container,false);
        return v;
    }
}
