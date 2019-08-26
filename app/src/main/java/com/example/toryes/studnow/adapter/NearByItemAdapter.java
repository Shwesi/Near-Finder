package com.example.toryes.studnow.adapter;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.toryes.studnow.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Hashtable;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TORYES on 7/11/2017.
 */

public class NearByItemAdapter implements GoogleMap.InfoWindowAdapter {

    TextView tvTitle,tvPhone;
    CircleImageView profileImage;
    Hashtable<String, String> markers;
    Context context;
    Marker marker;
    private View view;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    public NearByItemAdapter(Context context , Hashtable<String, String> markers, Marker marker) {
        this.markers=markers;
        this.context=context;
        this.marker=marker;
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.map_item,null,false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }


    @Override
    public View getInfoWindow(final Marker marker) {
        tvPhone = (TextView) view.findViewById(R.id.tvPhone);
        tvTitle = (TextView) view.findViewById(R.id.tvName);
        profileImage= (CircleImageView) view.findViewById(R.id.profilePic);
//        tvPhone.setVisibility(View.GONE);
        tvPhone.setText(marker.getSnippet());
        profileImage.setVisibility(View.GONE);
        this.marker=marker;
        String url = null;
        url= markers.get(marker.getId());
        tvTitle.setText(marker.getTitle());

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {

        if (marker != null
                && marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
            marker.showInfoWindow();
        }
        return null;
    }


}
