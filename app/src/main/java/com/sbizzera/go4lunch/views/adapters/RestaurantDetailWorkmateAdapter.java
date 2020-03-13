package com.sbizzera.go4lunch.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.FakeWorkmates;

import java.util.List;

public class RestaurantDetailWorkmateAdapter extends RecyclerView.Adapter<RestaurantDetailWorkmateAdapter.ViewHolder> {

    private List<FakeWorkmates> mWorkmateList;

    public RestaurantDetailWorkmateAdapter(List<FakeWorkmates> workmateList) {
        this.mWorkmateList = workmateList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workmates_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FakeWorkmates workmate = mWorkmateList.get(position);

        Glide.with(holder.wormateAvatarImg.getContext())
                .load(workmate.getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.wormateAvatarImg);

        holder.workmateChoiceTxt.setText(workmate.getChoice());

    }

    @Override
    public int getItemCount() {
        return mWorkmateList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView wormateAvatarImg;
        TextView workmateChoiceTxt;

         ViewHolder(@NonNull View itemView) {
            super(itemView);
            wormateAvatarImg = itemView.findViewById(R.id.workmates_img);
            workmateChoiceTxt = itemView.findViewById(R.id.workmates_choice_txt);
        }
    }
}
