package com.walmartlabs.classwork.rideone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaskev on 11/8/15.
 */
@ParseClassName("Ride")
public class Ride extends ParseObject implements Serializable {
    public static final String COLUMN_AVAILABLE = "available";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_LOCATION = "start_loc";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_SPOTS = "spots";
    public static final String COLUMN_DRIVER = "driver";
    public static final String COLUMN_RIDERS = "riders";

    private Map<String, Object> fields = new HashMap<>();


    public Ride putAll(Map<String, Object> fields) {
        for(Map.Entry<String, Object> entry : fields.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public Ride flush() {
        for(String key : keySet()) {
            fields.put(key, get(key));
        }

        fields.put("objectId", getObjectId());
        return this;
    }

    public static ArrayList<Ride> flushArray(List<Ride> rides) {
        ArrayList<Ride> res = new ArrayList<>(rides.size());
        for(Ride ride : rides) {
            res.add(ride.flush());
        }

        return res;
    }

    public Ride rebuild() {
        Ride ride = Ride.createWithoutData(Ride.class, fields.get("objectId").toString());
        return ride.putAll(this.fields);
    }


    public int getSpots() {
        return getInt(COLUMN_SPOTS);
    }

    public void setSpots(int spots) {
        put(COLUMN_SPOTS, spots);
    }

    public boolean isAvailable() {
        return getBoolean(COLUMN_AVAILABLE);
    }

    public void setAvailable(boolean available) {
        put(COLUMN_AVAILABLE, available);
    }

    public Date getDate() {
        return getDate(COLUMN_DATE);
    }

    public void setDate(Date date) {
        put(COLUMN_DATE, date);
    }

    public String getStartLocation() {
        return getString(COLUMN_START_LOCATION);
    }

    public void setStartLocation(String startLocation) {
        put(COLUMN_START_LOCATION, startLocation);
    }

    public String getDestination() {
        return getString(COLUMN_DESTINATION);
    }

    public void setDestination(String destination) {
        put(COLUMN_DESTINATION, destination);
    }

    public void setRiders(List<User> riders) {
        put(COLUMN_RIDERS, riders);
    }
    public List<User> getRiders() {
        return getList(COLUMN_RIDERS);
    }

    public void setDriver(User driver) {
        put(COLUMN_DRIVER, driver);
    }

    public User getDriver() {
        return (User) get(COLUMN_DRIVER);

    }

}
