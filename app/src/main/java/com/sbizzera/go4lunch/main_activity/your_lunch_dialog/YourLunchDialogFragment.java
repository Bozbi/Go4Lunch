package com.sbizzera.go4lunch.main_activity.your_lunch_dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;

public class YourLunchDialogFragment extends DialogFragment{

    private static final String EXTRA_MODEL = "EXTRA_MODEL";
    private LinearLayout dialogContainer;
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
        View view = requireActivity().getLayoutInflater().inflate(R.layout.your_lunch_dialog_fragment, null);
        TextView dialogTxt = view.findViewById(R.id.dialog_txt);
        YourLunchModel model = (YourLunchModel) getArguments().getSerializable(EXTRA_MODEL);
        dialogTxt.setText(model.getDialogtext());


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(requireActivity(), R.style.AppTheme))
                .setTitle("Your Lunch")
                .setIcon(R.drawable.ic_notification_icon_orange)
                .setView(view)
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        if (model.isPositiveAvailable()){
            builder.setPositiveButton("Check Your Lunch", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onItemBoundWithRestaurantClick(model.getRestaurantId());
                }
            });
        }


        return builder.create();
    }



    public void setListener(OnItemBoundWithRestaurantClickListener listener) {
        this.listener = listener;
    }
}
