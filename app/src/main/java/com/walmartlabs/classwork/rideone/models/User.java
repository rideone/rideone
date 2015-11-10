package com.walmartlabs.classwork.rideone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by mkrish4 on 11/7/15.
 */
@ParseClassName("_User")
public class User extends ParseUser {
    public enum Status {
        PASSENGER, DRIVER, WAIT_LIST, NO_RIDE
    }

    public String getUserId() {
        return getString("userId");
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

    public void setFirstName(String firstName) {
        put("firstName", firstName);
    }

    public void setLastName(String lastName) {
        put("lastName", lastName);
    }

    public void setTotalSeats(int totalSeats) {
        put("totalSeats", totalSeats);
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setStatus(Status status) {
        put("status", status.name());
    }

    public Status getStatus() {
        return Status.valueOf(getString("status"));
    }
    
    public void setRide(Ride ride) {
        put("ride", ride);
    }

    public Ride getRide() {
        return (Ride) get("ride");
    }
}
