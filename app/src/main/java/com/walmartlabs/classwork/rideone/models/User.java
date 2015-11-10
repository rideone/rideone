package com.walmartlabs.classwork.rideone.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mkrish4 on 11/7/15.
 */
@ParseClassName("_User")
public class User extends ParseUser implements Serializable {



    public static final String COLUMN_RIDE = "ride";
    public static final String COLUMN_STATUS = "status";

    public enum Status {
        PASSENGER, DRIVER, WAIT_LIST, NO_RIDE
    }

    private Map<String, Object> fields = new HashMap<>();


    public User putAll(Map<String, Object> fields) {
        for(Map.Entry<String, Object> entry : fields.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public User flush() {
        for(String key : keySet()) {
            if(!key.equals("sessionToken")) {
                fields.put(key, get(key));
            }

        }
        return this;
    }

    public static ArrayList<User> flushArray(List<User> users) {
        ArrayList<User> res = new ArrayList<>(users.size());
        for(User user : users) {
            res.add(user.flush());
        }

        return res;
    }


    public User rebuild() {
        return putAll(this.fields);
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
        put(COLUMN_STATUS, status.name());
    }

    public Status getStatus() {
        return Status.valueOf(getString(COLUMN_STATUS));
    }
    
    public void setRide(Ride ride) {
        put(COLUMN_RIDE, ride);
    }

    public Ride getRide() {
        return (Ride) get(COLUMN_RIDE);
    }

    public boolean isDriver() {
        return getStatus() == Status.DRIVER;
    }

}
