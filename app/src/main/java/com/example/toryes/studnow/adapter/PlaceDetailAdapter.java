package com.example.toryes.studnow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.bin.NearModel;
import com.example.toryes.studnow.bin.Review;
import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.helper.DatabaseHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TORYES on 11/18/2017.
 */

public class PlaceDetailAdapter extends RecyclerView.Adapter<PlaceDetailAdapter.MyViewHolder> {

    private ArrayList<Review> arrayList;
    Context context;
    private List<User> users;
    OnCardClickListner onCardClickListner;
    String latitude,longitude;
    DatabaseHelper databaseHelper;
    View view;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvText;
        public CircleImageView imgUser;
        RatingBar ratingBar;


        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvText = (TextView) view.findViewById(R.id.tvReviews);
            ratingBar= (RatingBar) view.findViewById(R.id.ratingBar);
            imgUser= (CircleImageView) view.findViewById(R.id.userImage);
//            imgvMap= (ImageView) view.findViewById(R.id.map);
        }
    }

    public PlaceDetailAdapter(ArrayList<Review> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context=context;
        this.users=users;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // fetching data from NearbyUser with position and set it to textview
        Review review=arrayList.get(position);
        holder.tvText.setText(review.getDetails());
        holder.tvName.setText(review.getName());
            holder.ratingBar.setRating(Float.parseFloat(review.getRating()));
        Glide.with(context).load(review.getImage()).into(holder.imgUser);
//        if (nearModel.getLat()>=1000) {
//            double dist=nearModel.getLat() / 1000;
//            DecimalFormat newFormat = new DecimalFormat("###.##");
//            dist= Double.parseDouble(newFormat.format(dist));
//            holder.tvDistance.setText(dist+ " Km");
//        }
//        else

//        if (position%2==0)
//            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.edt_bg));
//        else
//            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
//        holder.tvAddress.setText(nearModel.getVictinity());

    }

    @Override
    public int getItemCount() {
        // returns arralist size
        return arrayList.size();
    }

    public interface OnCardClickListner {
        void OnCardClicked(View view, int position);
    }

    public void setOnCardClickListner(OnCardClickListner onCardClickListner) {
        this.onCardClickListner = onCardClickListner;
    }

}


