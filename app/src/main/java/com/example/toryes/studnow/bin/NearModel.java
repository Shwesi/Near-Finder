package com.example.toryes.studnow.bin;

/**
 * Created by TORYES on 10/30/2017.
 */

public class NearModel {
    private String placename,victinity,distance,icon,rating,placeId;

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceId() {
        return placeId;
    }

    private double lat,lon,distanceInLong;

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getIcon() {
        return icon;
    }

    public String getRating() {
        return rating;
    }

    public void setDistanceInLong(double distanceInLong) {
        this.distanceInLong = distanceInLong;
    }

    public double getDistanceInLong() {
        return distanceInLong;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public void setVictinity(String victinity) {
        this.victinity = victinity;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getPlacename() {
        return placename;
    }

    public String getVictinity() {
        return victinity;
    }
}
