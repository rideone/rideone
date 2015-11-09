package com.walmartlabs.classwork.rideone.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by abalak5 on 11/8/15.
 */
@ParseClassName("Ride")
public class Ride extends ParseObject implements Parcelable {

    public Ride(){}

    public int getTotalSpots() {
        return getInt("totalSpots");
    }

    public void setTotalSpots(int totalSpots) {
        put("totalSpots", totalSpots);
    }

    public String getMake() {
        return getString("make");
    }

    public void setMake(String make) {
        put("make", make);
    }

    public String getModel() {
        return getString("model");
    }

    public void setModel(String model) {
        put("model", model);
    }

    public String getLicense() {
        return getString("license");
    }

    public void setLicense(String license) {
        put("license", license);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected Ride(Parcel in) {
    }

    public static final Creator<Ride> CREATOR = new Creator<Ride>() {
        @Override
        public Ride createFromParcel(Parcel in) {
            return new Ride(in);
        }

        @Override
        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };
}
