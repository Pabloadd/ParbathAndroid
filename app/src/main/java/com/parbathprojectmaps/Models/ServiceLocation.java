package com.parbathprojectmaps.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Hashtable;

public class ServiceLocation implements Parcelable {

    private String location_path;
    private ToiletPlaces place_description;
    private GeoPoint position_lat_long;


    public ServiceLocation(String location_path,ToiletPlaces place_description, GeoPoint position_lat_long ) {
        this.location_path = location_path;
        this.place_description = place_description;
        this.position_lat_long = position_lat_long;

    }

    public ServiceLocation(){}


    protected ServiceLocation(Parcel in) {
        place_description = in.readParcelable(ToiletPlaces.class.getClassLoader());
    }
//
    public static final Creator<ServiceLocation> CREATOR = new Creator<ServiceLocation>() {
        @Override
        public ServiceLocation createFromParcel(Parcel in) {
            return new ServiceLocation(in);
        }

        @Override
        public ServiceLocation[] newArray(int size) {
            return new ServiceLocation[size];
        }
    };

    public ToiletPlaces getPlace_description() {
        return place_description;
    }

    public void setPlace_description(ToiletPlaces place_description) {
        this.place_description = place_description;
    }

    public GeoPoint getPosition_lat_long() {
        return position_lat_long;
    }

    public void setPosition_lat_long(GeoPoint position_lat_long) {
        this.position_lat_long = position_lat_long;
    }

    public String getLocation_path() {
        return location_path;
    }

    public void setLocation_path(String location_path) {
        this.location_path = location_path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(place_description, i);
    }

}
