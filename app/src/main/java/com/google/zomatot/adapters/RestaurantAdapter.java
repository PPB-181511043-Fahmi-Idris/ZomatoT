package com.google.zomatot.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zomatot.R;
import com.google.zomatot.model.Restaurant;

import java.util.ArrayList;


public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder> {

    private ArrayList<Restaurant> mData;
    private Activity mACtivity;

    public RestaurantAdapter(ArrayList<Restaurant> data, Activity activity) {
        this.mData = data;
        this.mACtivity = activity;
    }

    @Override
    public RestaurantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_restaurant, parent, false);
        return new RestaurantHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantHolder holder, int position) {
        Restaurant restaurant = mData.get(position);

        holder.setName(restaurant.getName());
        holder.setAddress(restaurant.getAddress());
        holder.setCost("Kisaran Harga: " + restaurant.getCurrency() + restaurant.getCost());
        holder.setRating(restaurant.getRating());

        Glide.with(mACtivity)
                .load(restaurant.getImageUrl())
                .into(holder.restaurantImageView);
    }

    @Override
    public int getItemCount() {
        if (mData == null)
            return 0;
        return mData.size();
    }

    public class RestaurantHolder extends RecyclerView.ViewHolder {

        ImageView restaurantImageView;
        TextView restaurantNameTextView;
        TextView restaurantAddressTextView;
        TextView restaurantRatingTextView;
        TextView costTextView;

        public RestaurantHolder(View itemView) {
            super(itemView);

            restaurantImageView = (ImageView) itemView.findViewById(R.id.imageview_restaurant);
            restaurantNameTextView = (TextView) itemView.findViewById(R.id.textview_restaurant_name);
            restaurantAddressTextView = (TextView) itemView.findViewById(R.id.restaurant_address_textview);
            restaurantRatingTextView = (TextView) itemView.findViewById(R.id.rating);
            costTextView = (TextView) itemView.findViewById(R.id.cost_for_two_textview);
        }

        public void setName(String name) {
            restaurantNameTextView.setText(name);
        }

        public void setAddress(String address) {
            restaurantAddressTextView.setText(address);
        }

        public void setRating(String rating) {
            restaurantRatingTextView.setText(rating);
        }

        public void setCost(String cost) {
            costTextView.setText(cost);
        }

    }
}
