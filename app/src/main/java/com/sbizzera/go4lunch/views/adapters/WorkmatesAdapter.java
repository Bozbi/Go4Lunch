package com.sbizzera.go4lunch.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbizzera.go4lunch.events.OnItemBindWithRestaurantClickListener;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.FakeWorkmates;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    private List<FakeWorkmates> mWormatesList;
    private OnItemBindWithRestaurantClickListener mListener;

    public WorkmatesAdapter(List<FakeWorkmates> workmatesList, OnItemBindWithRestaurantClickListener listener) {
        mWormatesList = workmatesList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_view, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemBoundWithRestaurantClick("");
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FakeWorkmates workmate = mWormatesList.get(position);

        Glide.with(holder.workmateAvatar.getContext())
                .load(workmate.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.workmateAvatar);

        holder.workmateChoice.setText(workmate.getChoice());
    }

    @Override
    public int getItemCount() {
        return mWormatesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView workmateChoice;
        public ImageView workmateAvatar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            workmateChoice = itemView.findViewById(R.id.workmates_choice_txt);
            workmateAvatar = itemView.findViewById(R.id.workmates_img);
        }
    }
}
