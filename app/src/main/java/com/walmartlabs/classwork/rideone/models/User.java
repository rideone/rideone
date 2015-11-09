package com.walmartlabs.classwork.rideone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by mkrish4 on 11/7/15.
 */
@ParseClassName("User")
public class User extends ParseObject {
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

    public void setUserEmail(String userEmail) {
        put("userEmail", userEmail);
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
}
