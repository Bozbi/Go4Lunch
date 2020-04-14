package com.sbizzera.go4lunch.main_activity.your_lunch_dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.services.ViewModelFactory;

public class YourLunchDialogFragment extends DialogFragment implements View.OnClickListener {

    private TextView yourLunchTxt;
    private LinearLayout dialogContainer;
    private OnItemBoundWithRestaurantClickListener listener;

    public static YourLunchDialogFragment newInstance() {

        Bundle args = new Bundle();

        YourLunchDialogFragment fragment = new YourLunchDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_lunch_dialog_fragment,container,false);

        yourLunchTxt = view.findViewById(R.id.dialog_txt);
        dialogContainer = view.findViewById(R.id.yourLunchContainer);
        dialogContainer.setOnClickListener(this);


        YourLunchDialogViewModel viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(YourLunchDialogViewModel.class);
        viewModel.getModel().observe(this, this::updateUI);

        return view;
    }

    private void updateUI(YourLunchDialogModel model) {
        dialogContainer.setTag(model.getRestaurantId());
        dialogContainer.setClickable(model.getClickable());
        yourLunchTxt.setText(model.getYourLunchText());
    }

    @Override
    public void onClick(View v) {
        listener.onItemBoundWithRestaurantClick(v.getTag().toString());
    }

    public void setListener(OnItemBoundWithRestaurantClickListener listener) {
        this.listener = listener;
    }
}
