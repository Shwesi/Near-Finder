package com.example.toryes.studnow.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.bin.NearByUser;
import com.example.toryes.studnow.bin.NearModel;
import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.helper.DatabaseHelper;
import com.example.toryes.studnow.service.LocationTracker;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TORYES on 10/30/2017.
 */

public class NearMeAdapter extends RecyclerView.Adapter<NearMeAdapter.MyViewHolder> {

    private ArrayList<NearModel> arrayList;
    Context context;
    private List<User> users;
   OnCardClickListner onCardClickListner;
    String latitude,longitude;
    DatabaseHelper databaseHelper;
    View view;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvDistance,tvAddress;
        public ImageView imgvMap,imgvDirec,imagePlace;
        RatingBar ratingBar;


        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            imgvDirec= (ImageView) view.findViewById(R.id.direction);
            ratingBar= (RatingBar) view.findViewById(R.id.ratingBar);
            imagePlace= (ImageView) view.findViewById(R.id.imageView);
//            imgvMap= (ImageView) view.findViewById(R.id.map);
        }
    }

    public NearMeAdapter(ArrayList<NearModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context=context;
        this.users=users;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nearme_list, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // fetching data from NearbyUser with position and set it to textview
        NearModel nearModel=arrayList.get(position);
        holder.tvAddress.setText(nearModel.getVictinity());
        holder.tvName.setText(nearModel.getPlacename());
        if (nearModel.getRating()!="")
            holder.ratingBar.setRating(Float.parseFloat(nearModel.getRating()));
//        Glide.with(context).load(nearModel.getIcon()).into(holder.imagePlace);
//        if (nearModel.getLat()>=1000) {
//            double dist=nearModel.getLat() / 1000;
//            DecimalFormat newFormat = new DecimalFormat("###.##");
//            dist= Double.parseDouble(newFormat.format(dist));
//            holder.tvDistance.setText(dist+ " Km");
//        }
//        else
            holder.tvDistance.setText(nearModel.getDistance());
        holder.imgvDirec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCardClickListner!=null)
                    onCardClickListner.OnCardClicked(v,position);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCardClickListner!=null)
                    onCardClickListner.OnCardClicked(v,position);
            }
        });
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
    private String distance(double lat1, double lon1, double lat2, double lon2) {



        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        DecimalFormat decimalFormat=new DecimalFormat("##.##",new DecimalFormatSymbols(Locale.US));

        return decimalFormat.format(dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}

