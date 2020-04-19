package com.sbizzera.go4lunch.main_activity.fragments.list_fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.events.OnItemBoundWithRestaurantClickListener;

import java.util.ArrayList;
import java.util.List;

public class ListFragmentAdapter extends RecyclerView.Adapter<ListFragmentAdapter.ViewHolder> {

    private List<ListFragmentAdapterModel> restaurantModelList = new ArrayList<>();

    private OnItemBoundWithRestaurantClickListener mListener;


    public ListFragmentAdapter(OnItemBoundWithRestaurantClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListFragmentAdapterModel restaurant = restaurantModelList.get(position);
        holder.name.setText(restaurant.getRestaurantName());
        holder.address.setText(restaurant.getRestaurantAddress());
        holder.openingHours.setText(restaurant.getOpenHoursText());
        holder.openingHours.setTextColor(holder.openingHours.getResources().getColor(restaurant.getOpenHoursTextColor()));
        holder.distance.setText(restaurant.getDistance());
        holder.m_Text.setVisibility(restaurant.getMetersTextVisibility());
        holder.workmateFreq.setText(restaurant.getWorkmatesLunchesCount());
        holder.star1Img.setVisibility(restaurant.getStar1Visibility());
        holder.star2Img.setVisibility(restaurant.getStar2Visibility());
        holder.star3Img.setVisibility(restaurant.getStar3Visibility());

        Glide.with(holder.img.getContext())
                .load(restaurant.getPhotoUrl())
                .placeholder(R.drawable.restaurant_photo_placeholder)
                .into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            mListener.onItemBoundWithRestaurantClick(restaurant.getRestaurantId());
        });

    }

    @Override
    public int getItemCount() {
        return restaurantModelList.size();
    }

    public void setList(List<ListFragmentAdapterModel> restaurantModelList) {
        this.restaurantModelList = restaurantModelList;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        TextView openingHours;
        TextView distance;
        TextView m_Text;
        TextView workmateFreq;
        ImageView img;
        ImageView star1Img;
        ImageView star2Img;
        ImageView star3Img;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.restaurant_name_txt);
            address = itemView.findViewById(R.id.restaurant_address_txt);
            openingHours = itemView.findViewById(R.id.restaurant_opening_status_txt);
            distance = itemView.findViewById(R.id.restaurant_distance_txt);
            m_Text = itemView.findViewById(R.id.m_txt);
            workmateFreq = itemView.findViewById(R.id.restaurant_workmates_frequentation);
            img = itemView.findViewById(R.id.restaurant_img);
            star1Img = itemView.findViewById(R.id.restaurant_star_1_img);
            star2Img = itemView.findViewById(R.id.restaurant_star_2_img);
            star3Img = itemView.findViewById(R.id.restaurant_star_3_img);

        }
    }
}
