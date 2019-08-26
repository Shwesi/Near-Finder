package com.example.toryes.studnow.placeapi;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.adapter.MapItemAdapter;
import com.example.toryes.studnow.adapter.NearByItemAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.Nearby;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
 
    String googlePlacesData;
    GoogleMap mMap;
    String url;
    private Hashtable<String, String> markers;
    Marker marker;
    LatLng latLngNear;
    Context context;
 public GetNearbyPlacesData(LatLng latLngNear, Context context){
     this.latLngNear=latLngNear;
     this.context=context;
 }
    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", googlePlacesData);
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }
 
    @Override
    protected void onPostExecute(String result) {
//        Log.e("GooglePlacesReadTask", result);
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }
 
    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        markers = new Hashtable<String, String>();
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute","Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
//            LatLng latLngNear=new LatLng(lat,lng);
//            CalculationByDistance(latLng,latLngNear);
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            float[] results = new float[1];
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            Location.distanceBetween(latLng.latitude,latLng.longitude,lat,lng,results);

            final Marker hamburg = mMap.addMarker(new MarkerOptions().position(latLng)
                    .title(placeName).snippet(distance(latLngNear.latitude,latLngNear.longitude,lat,lng)+" KM"));

//            markers.put(hamburg.getId(), members.getImg()+"");
            mMap.setInfoWindowAdapter(new NearByItemAdapter(context,markers,marker));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLngNear);
        markerOptions.title("Your Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
         mMap.addMarker(markerOptions);
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        DecimalFormat newFormat = new DecimalFormat("###.##",new DecimalFormatSymbols(Locale.US));
        dist= Double.parseDouble(newFormat.format(dist));
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}