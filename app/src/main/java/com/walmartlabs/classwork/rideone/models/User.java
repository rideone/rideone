package com.walmartlabs.classwork.rideone.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by mkrish4 on 11/7/15.
 */
@ParseClassName("User")
public class User extends ParseObject {
    public User() {}

    public String getUserEmail() {
        return getString("userEmail");
    }

    public String getFirstName() {
        return getString("firstName");
    }

    public String getLastName() {
        return getString("lastName");
    }

    public int getTotalSeats() {
        return getInt("totalSeats");
    }

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public void setBody(String body) {
        put("body", body);
    }

    public void setFirstName(String firstName) {
        put("firstName", firstName);
    }

    public void setLastName(String lastName) {
        put("lastName", lastName);
    }

    public void setRide(Ride ride) {
        put("ride", ride);
    }

    public Ride getRide() {
        return (Ride) get("ride");
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
    }

    protected User(Parcel in) {
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
