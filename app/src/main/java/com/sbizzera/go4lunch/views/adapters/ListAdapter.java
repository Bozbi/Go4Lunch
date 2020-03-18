package com.sbizzera.go4lunch.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sbizzera.go4lunch.events.OnItemBindWithRestaurantClickListener;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.FakeRestaurants;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<FakeRestaurants> mRestaurantsList;

    private OnItemBindWithRestaurantClickListener mListener;

    public ListAdapter(List<FakeRestaurants> restaurantsList,OnItemBindWithRestaurantClickListener listener) {
        mRestaurantsList = restaurantsList;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_view, parent, false);
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
        FakeRestaurants restaurant = mRestaurantsList.get(position);
        holder.name.setText(restaurant.getName());
        holder.address.setText(restaurant.getAddress());
        holder.openingHours.setText(restaurant.getOpeningHours());
        holder.distance.setText(restaurant.getDistance());
        holder.workmateFreq.setText(restaurant.getWorkmateFrequentation());
        Glide.with(holder.img.getContext())
                .load(restaurant.getPhotoUrl())
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return mRestaurantsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView address;
        public TextView openingHours;
        public TextView distance;
        public TextView workmateFreq;
        public ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.restaurant_name_txt);
            address = itemView.findViewById(R.id.restaurant_address_txt);
            openingHours = itemView.findViewById(R.id.restaurant_opening_status_txt);
            distance = itemView.findViewById(R.id.restaurant_distance_txt);
            workmateFreq = itemView.findViewById(R.id.restaurant_workmates_frequentation);
            img = itemView.findViewById(R.id.restaurant_img);

        }
    }
}
