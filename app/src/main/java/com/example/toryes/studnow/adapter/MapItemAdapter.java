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

public class MapItemAdapter implements GoogleMap.InfoWindowAdapter {

  TextView tvTitle,tvPhone;
    CircleImageView profileImage;
    Hashtable<String, String> markers;
    Context context;
    Marker marker;
    private View view;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
  public MapItemAdapter(Context context , Hashtable<String, String> markers, Marker marker) {
      this.markers=markers;
      this.context=context;
      this.marker=marker;
      LayoutInflater inflater=LayoutInflater.from(context);
      initImageLoader();
      imageLoader = ImageLoader.getInstance();

      options = new DisplayImageOptions.Builder()
              .showStubImage(R.mipmap.ic_launcher)		//	Display Stub Image
              .showImageForEmptyUri(R.mipmap.ic_launcher)	//	If Empty image found
              .cacheInMemory()
              .cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
       view=inflater.inflate(R.layout.map_item,null,false);

      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

  }


  @Override
  public View getInfoWindow(final Marker marker) {
      tvPhone = (TextView) view.findViewById(R.id.tvPhone);
      tvTitle = (TextView) view.findViewById(R.id.tvName);
      profileImage= (CircleImageView) view.findViewById(R.id.profilePic);
      this.marker=marker;
      String url = null;
      url= markers.get(marker.getId());
      tvTitle.setText(marker.getTitle());
      tvPhone.setText(marker.getSnippet());
//      tvDist.setText(marker.getSnippet());
      imageLoader.displayImage(url, profileImage, options,
              new SimpleImageLoadingListener() {
                  @Override
                  public void onLoadingComplete(String imageUri,
                                                View view, Bitmap loadedImage) {
                      super.onLoadingComplete(imageUri, view,
                              loadedImage);
                      getInfoContents(marker);
                  }
              });
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

    private void initImageLoader() {
        int memoryCacheSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            int memClass = ((ActivityManager)
                    context.getSystemService(Context.ACTIVITY_SERVICE))
                    .getMemoryClass();
            memoryCacheSize = (memClass / 8) * 1024 * 1024;
        } else {
            memoryCacheSize = 2 * 1024 * 1024;
        }

        final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPoolSize(5)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(memoryCacheSize)
                .memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        ImageLoader.getInstance().init(config);
    }
}