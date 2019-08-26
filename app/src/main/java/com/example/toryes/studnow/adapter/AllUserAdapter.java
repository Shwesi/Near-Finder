package com.example.toryes.studnow.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.activity.LoginActvity;
import com.example.toryes.studnow.bin.Members;
import com.example.toryes.studnow.bin.NearByUser;
import com.example.toryes.studnow.bin.User;
import com.example.toryes.studnow.fragment.ChatFragment;
import com.example.toryes.studnow.helper.DatabaseHelper;
import com.example.toryes.studnow.service.LocationTracker;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TORYES on 7/3/2017.
 */

public class AllUserAdapter extends RecyclerView.Adapter<AllUserAdapter.MyViewHolder> {

    public static ArrayList<NearByUser> arrayList;
    Context context;
    private List<User> users;
    OnCardClickListner onCardClickListner;
    String latitude,longitude;
    DatabaseHelper databaseHelper;
    View view;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvPhone, tvDistance,tvBadge;
        public CircleImageView imageViewProfile;
        public ImageView imageView,imagevIewStatus,imgvChat;
        LinearLayout linearLaout;
        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.textName);
            tvPhone = (TextView) view.findViewById(R.id.textPhone);
            tvDistance = (TextView) view.findViewById(R.id.textDistance);
            imageViewProfile= (CircleImageView) view.findViewById(R.id.profilePic);
            imageView= (ImageView) view.findViewById(R.id.imagChat);
            tvBadge= (TextView) view.findViewById(R.id.badge_textView);
            imagevIewStatus= (ImageView) view.findViewById(R.id.onlineImg);
            imgvChat= (ImageView) view.findViewById(R.id.imagChat);
            linearLaout= (LinearLayout) view.findViewById(R.id.linearLaout);
        }
    }

    public void add(User user) {
        users.add(user);
        notifyItemInserted(users.size() - 1);
    }
    public AllUserAdapter(ArrayList<NearByUser> arrayList, Context context,List<User> users) {
        this.arrayList = arrayList;
        this.context=context;
        this.users=users;
        databaseHelper=new DatabaseHelper(context);
        LocationTracker locationTracker=new LocationTracker(context);
        if(locationTracker.canGetLocation()){
            latitude=locationTracker.getLatitude()+"";
            longitude=locationTracker.getLongitude()+"";
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_all_user, parent, false);


        return new MyViewHolder(itemView);
    }
    public User getUser(int position) {
        return users.get(position);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // fetching data from NearbyUser with position and set it to textview
        Log.e("arrar",arrayList.toString());
        Log.e("arraer",users.toString());
        int i=position;

        final User nearByUser=users.get(position);
        if (position<getItemCount()-1){
            holder.imagevIewStatus.setVisibility(View.VISIBLE);
            holder.linearLaout.setVisibility(View.VISIBLE);
            holder.tvDistance.setVisibility(View.VISIBLE);
            holder.tvBadge.setVisibility(View.VISIBLE);
            holder.imgvChat.setVisibility(View.VISIBLE);
        holder.tvDistance.setText(distance(Double.parseDouble(latitude),Double.parseDouble(longitude),Double.parseDouble(nearByUser.lat),Double.parseDouble(nearByUser.lon))+" km ");
        holder.tvName.setText(nearByUser.firstname+" "+nearByUser.lastname);
//        holder.tvPhone.setText(nearByUser.phoneNumber);
        holder.tvBadge.setText(databaseHelper.countMessage(nearByUser.email)+"");
if (nearByUser.last_seen.matches("Online"))
    holder.imagevIewStatus.setImageResource(R.drawable.online_green);
        else
    holder.imagevIewStatus.setImageResource(R.drawable.online_bg_grey);
        if (holder.tvBadge.getText().toString().matches("0")){
            holder.tvBadge.setVisibility(View.GONE);
        }
        else
            holder.tvBadge.setVisibility(View.VISIBLE);
        Glide.with(context).load(nearByUser.profilePic).into(holder.imageViewProfile);
    }
    else {
            holder.imagevIewStatus.setVisibility(View.GONE);
            holder.tvName.setText(nearByUser.firstname);
            holder.tvDistance.setVisibility(View.GONE);
            holder.tvBadge.setVisibility(View.GONE);
            holder.imgvChat.setVisibility(View.GONE);
            holder.imageViewProfile.setBorderWidth(0);
            holder.imageViewProfile.setMaxWidth(18);
            holder.imageViewProfile.setMaxHeight(18);
            holder.imageViewProfile.setImageResource(R.drawable.ic_share_black_24dp);
            holder.linearLaout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        // returns arralist size
        return users.size();
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
        DecimalFormat decimalFormat=new DecimalFormat("##.##");

        return decimalFormat.format(dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}

