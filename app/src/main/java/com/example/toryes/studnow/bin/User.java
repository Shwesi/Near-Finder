package com.example.toryes.studnow.bin;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kartik Sharma
 * Created on: 9/1/2016 , 8:35 PM
 * Project: FirebaseChat
 */

@IgnoreExtraProperties
public class User implements Comparable{
    public String uid;
    public String email;
    public String firebaseToken;
    public String last_seen;
    public String phoneNumber;
    public String profilePic;
    public boolean emailVerified;
    double distance;

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public String lat, lon,firstname,lastname,password;
    public User() {
    }

    public User(String uid, String email, String firebaseToken,String last_seen,String phoneNumber,String profilePic,String lat,String lon,String firstname,String lastname,String password,boolean emailVerified) {
        this.uid = uid;
        this.email = email;
        this.firebaseToken = firebaseToken;
        this.last_seen=last_seen;
        this.lat=lat;
        this.lon=lon;
        this.phoneNumber=phoneNumber;
        this.profilePic=profilePic;
        this.firstname=firstname;
        this.lastname=lastname;
        this.password=password;
        this.emailVerified=emailVerified;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("lat", lat);
        result.put("lon", lon);


        return result;


    }
    @Exclude
    public Map<String, Object> toUpdateMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email",email);
        result.put("lat", lat);
        result.put("lon", lon);
        result.put("firstname",firstname);
        result.put("lastname", lastname);
        result.put("password", password);
        result.put("phoneNumber", phoneNumber);


        return result;


    }
    @Exclude
    public Map<String, Object> toImageUpdateMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("profilePic",profilePic);


        return result;


    }
    @Override
    public int compareTo(@NonNull Object o) {
        if (((User) o).getDistance() == distance) {
            return 1;
        }
        if (((User) o).getDistance() >distance) {
            return -1;
        } else {
            return 0;
        }


    }
}
