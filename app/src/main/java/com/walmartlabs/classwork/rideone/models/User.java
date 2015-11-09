package com.walmartlabs.classwork.rideone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by mkrish4 on 11/7/15.
 */
@ParseClassName("User")
public class User extends ParseObject {
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
}
