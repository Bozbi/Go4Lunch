package com.sbizzera.go4lunch.workmates_fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbizzera.go4lunch.main_activity.OnItemBoundWithRestaurantClickListener;
import com.sbizzera.go4lunch.R;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragmentAdapter extends RecyclerView.Adapter<WorkmatesFragmentAdapter.ViewHolder> {

    private List<WorkmatesFragmentAdapterModel> mWormatesList = new ArrayList<>();
    private OnItemBoundWithRestaurantClickListener mListener;

    public WorkmatesFragmentAdapter(OnItemBoundWithRestaurantClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkmatesFragmentAdapterModel workmate = mWormatesList.get(position);

        Glide.with(holder.workmateAvatar.getContext())
                .load(workmate.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.workmateAvatar);

        holder.workmateChoice.setText(workmate.getChoice());
        holder.workmateChoice.setTypeface(holder.workmateChoice.getTypeface(),workmate.getTextStyle());
        holder.itemView.setOnClickListener(v->{
            mListener.onItemBoundWithRestaurantClick(workmate.getRestaurantId());
        });
        holder.itemView.setClickable(workmate.getClickable());
    }

    @Override
    public int getItemCount() {
        return mWormatesList.size();
    }

    public void setWorkmatesList(List<WorkmatesFragmentAdapterModel> workmatesList) {
        mWormatesList = workmatesList;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView workmateChoice;
        ImageView workmateAvatar;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            workmateChoice = itemView.findViewById(R.id.workmates_choice_txt);
            workmateAvatar = itemView.findViewById(R.id.workmates_img);
        }
    }
}
