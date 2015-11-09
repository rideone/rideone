package com.walmartlabs.classwork.rideone.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * Created by dmaskev on 11/8/15.
 */
@ParseClassName("Ride")
public class Ride extends ParseObject {
    public static final String COLUMN_AVAILABLE = "available";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_LOCATION = "start_loc";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_SPOTS = "spots";
    public static final String COLUMN_DRIVER = "driver";
    public static final String COLUMN_RIDERS = "riders";

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
