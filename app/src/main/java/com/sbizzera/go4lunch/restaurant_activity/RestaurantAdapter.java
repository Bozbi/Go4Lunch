package com.sbizzera.go4lunch.restaurant_activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.restaurant_activity.models.RestaurantAdapterModel;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<RestaurantAdapterModel> mWorkmateList;

    RestaurantAdapter(List<RestaurantAdapterModel> workmateModelList) {
        mWorkmateList = workmateModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_view, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RestaurantAdapterModel workmate = mWorkmateList.get(position);

        Glide.with(holder.wormateAvatarImg.getContext())
                .load(workmate.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.wormateAvatarImg);

        holder.workmateChoiceTxt.setText(workmate.getText());

    }

    @Override
    public int getItemCount() {
        return mWorkmateList.size();
    }

    public void setList(List<RestaurantAdapterModel> list) {
        mWorkmateList = list;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView wormateAvatarImg;
        TextView workmateChoiceTxt;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            wormateAvatarImg = itemView.findViewById(R.id.workmates_img);
            workmateChoiceTxt = itemView.findViewById(R.id.workmates_choice_txt);
        }
    }
}
