package com.sbizzera.go4lunch.main_activity.your_lunch_dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.main_activity.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.main_activity.RestaurantClickedListenable;

public class YourLunchDialogFragment extends DialogFragment implements RestaurantClickedListenable {

    private static final String EXTRA_MODEL = "EXTRA_MODEL";
    private OnItemBoundWithRestaurantClickListener listener;

    public static YourLunchDialogFragment newInstance(YourLunchModel yourLunchModel) {
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(EXTRA_MODEL, yourLunchModel);
        YourLunchDialogFragment fragment = new YourLunchDialogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_notification_icon_orange)
                .setNegativeButton(R.string.dialog_negative_text, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        if (getArguments() != null) {
            YourLunchModel model = (YourLunchModel) getArguments().getSerializable(EXTRA_MODEL);
            if (model != null) {
                if (model.getDialogtext() != null) {
                    builder.setMessage(model.getDialogtext());
                }
                if (model.isPositiveAvailable()) {
                    builder.setPositiveButton(R.string.dialog_positive_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onItemBoundWithRestaurantClick(model.getRestaurantId());
                        }
                    });
                }
            }
        }


        return builder.create();
    }

    public void setListener(OnItemBoundWithRestaurantClickListener listener) {
        this.listener = listener;
    }
}
