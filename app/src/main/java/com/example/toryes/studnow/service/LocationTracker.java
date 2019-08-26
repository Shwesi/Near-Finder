package com.example.toryes.studnow.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.Constants;
import com.example.toryes.studnow.Utils.Prefrence;
import com.example.toryes.studnow.Utils.ProgressDialogBox;
import com.example.toryes.studnow.Utils.SharedPrefUtil;
import com.example.toryes.studnow.bin.LoginCredentials;
import com.example.toryes.studnow.bin.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by TORYES on 7/1/2017.
 */

public class LocationTracker extends Service implements LocationListener,Constants {

    Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1/1000; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 *5 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;
    public LocationTracker() {

    }
    public LocationTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
//    public void showSettingsAlert(){
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is settings");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
//            }
//        });
//
//        // on pressing cancel button
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        // Showing Alert Message
//        alertDialog.show();
//    }

    @Override
    public void onLocationChanged(Location location) {
        LoginCredentials loginCredentials= Prefrence.getLoginCeredential(this);
//        markers = new Hashtable<String, String>();
        if (loginCredentials!=null) {
//            String user_id=loginCredentials.getUser_id();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            updateLocationApi();
//            Toast.makeText(mContext, location.getLongitude() + " " + location.getLatitude(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void updateLocationApi(){
        updateFirebaseToken();
//        Context context=this;
//        AsyncHttpClient client = new AsyncHttpClient();
//        JSONObject updateLocParamas = new JSONObject();
//        StringEntity entity = null;
//        try {
//            updateLocParamas.put(KEY_USER_ID,userId);
//        updateLocParamas.put(KEY_LONGITUDE,longitude);
//        updateLocParamas.put(KEY_LATITUDE,latitude);
//            entity= new StringEntity(updateLocParamas.toString());
//            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        client.post(context,BASE_URL+"userapi/setMyLocation/",entity,"application/json", new AsyncHttpResponseHandler() {
//            @Override
//            public void onStart() {
////                //Start progress indicator here
////                ProgressDialogBox.showProgressDialog(mContext);
//            }
//
//            @Override
//            public void onFinish() {
//                // Completed the request (either success or failure)
////                ProgressDialogBox.disMissDailog();
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                try {
//                    String str = new String(responseBody, "UTF-8");
//                    Log.e("str",str);
////                   parsing json data
////                    parseListUserNearByAPi(str);
//
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
////                try {
//////                    String str = new String(responseBody, "UTF-8");
//////                    parseListUserNearByAPi(str);
//////                    Log.e("str1",str);
////                } catch (UnsupportedEncodingException e) {
////                    e.printStackTrace();
////                }
//            }
//
//        });



    }

//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("lat", latitude);
//        result.put("lon", longitude);
//
//
//        return result;
//    }

    private void updateFirebaseToken() {
        final LoginCredentials loginCredentials = Prefrence.getLoginCeredential(this);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();


        final User user1 = new User();
        user1.email = loginCredentials.getEmail();
        user1.password = loginCredentials.getPassword();
        user1.firstname = loginCredentials.getFirst_name();
        user1.lastname = loginCredentials.getLast_name();
        user1.lat = latitude + "";
        user1.lon = longitude + "";
        user1.phoneNumber = loginCredentials.getPhone();
        user1.firebaseToken =loginCredentials.getFirebasetoken();
        user1.profilePic = loginCredentials.getImage();
        user1.uid = loginCredentials.getUseridentificationNo();
//        user1.last_seen = Prefrence.getString(this,"last_seen");
        Map<String, Object> postValues = user1.toMap();
        database.child(Constants.ARG_USERS).child(loginCredentials.getUseridentificationNo()).updateChildren(postValues).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                            mOnUserDatabaseListener.onSuccess(context.getString(R.string.user_successfully_added));
//                    LoginCredentials loginCredentials = new LoginCredentials();
////                            loginCredentials.setUser_id(jsonObject.getString(KEY_USER_ID));
////                            loginCredentials.setUseridentificationNo(jsonObject.getString(KEY_UNIQUE_ID));
//                    loginCredentials.setFirst_name(user1.firstname);
//                    loginCredentials.setLast_name(user1.lastname);
//                    loginCredentials.setEmail(user1.email);
//                    loginCredentials.setPhone(user1.phoneNumber);
//                    loginCredentials.setPassword(user1.password);
//                    loginCredentials.setImage(user1.profilePic);
//                    loginCredentials.setLongitude(longitude + "");
//                    loginCredentials.setLatitude(latitude + "");
                }


            }
        });
//        User user = new User(firebaseUser.getUid(),
//                firebaseUser.getEmail(),
//                new SharedPrefUtil(this).getString(ARG_FIREBASE_TOKEN),convertTimestamp(), user1.phoneNumber,user1.profilePic,latitude,longitude,user1.firstname,user1.lastname,user1.password);
//        database.child(Constants.ARG_USERS)
//                .child(user1.uid)
//                .setValue(user1)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
////                            mOnUserDatabaseListener.onSuccess(context.getString(R.string.user_successfully_added));
//                            LoginCredentials loginCredentials = new LoginCredentials();
////                            loginCredentials.setUser_id(jsonObject.getString(KEY_USER_ID));
////                            loginCredentials.setUseridentificationNo(jsonObject.getString(KEY_UNIQUE_ID));
//                            loginCredentials.setFirst_name(user1.firstname);
//                            loginCredentials.setLast_name(user1.lastname);
//                            loginCredentials.setEmail(user1.email);
//                            loginCredentials.setPhone(user1.phoneNumber);
//                            loginCredentials.setPassword(user1.password);
//                            loginCredentials.setImage(user1.profilePic);
//                            loginCredentials.setLongitude(longitude + "");
//                            loginCredentials.setLatitude(latitude + "");
//                        }
//
//
//                    }
//                });
    }
}