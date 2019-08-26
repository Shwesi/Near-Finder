package com.example.toryes.studnow.bin;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TORYES on 7/3/2017.
 */

public class NearByUser implements Parcelable{
private String userid,firstname,lastname,email,phone,image,longitude,latitude,distance,distanceunit,unseenCount,fToken,uid,status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setfToken(String fToken) {
        this.fToken = fToken;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDistanceunit(String distanceunit) {
        this.distanceunit = distanceunit;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    public void setUnseenCount(String unseenCount) {
        this.unseenCount = unseenCount;
    }
    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDistance() {
        return distance;
    }

    public String getDistanceunit() {
        return distanceunit;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getImage() {
        return image;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserid() {
        return userid;
    }

    public String getfToken() {
        return fToken;
    }

    public String getUid() {
        return uid;
    }

    public String getUnseenCount() {
        return unseenCount;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(email);
        dest.writeString(image);
        dest.writeString(phone);
        dest.writeString(longitude);
        dest.writeString(latitude);
        dest.writeString(distance);
        dest.writeString(distanceunit);
        dest.writeString(unseenCount);
        dest.writeString(fToken);
        dest.writeString(uid);
        dest.writeString(status);

    }

    // Creator
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public NearByUser createFromParcel(Parcel in) {
            return new NearByUser(in);
        }

        public NearByUser[] newArray(int size) {
            return new NearByUser[size];
        }
    };

    // "De-parcel object
    public NearByUser(Parcel in) {
        userid = in.readString();
        firstname = in.readString();
        lastname = in.readString();
        phone = in.readString();
        image = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        distance = in.readString();
        distanceunit = in.readString();
        image = in.readString();
        unseenCount=in.readString();
        fToken=in.readString();
        uid=in.readString();
        status=in.readString();
    }
    public NearByUser() {

    }
}
