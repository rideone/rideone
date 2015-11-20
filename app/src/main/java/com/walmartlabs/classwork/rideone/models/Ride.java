package com.walmartlabs.classwork.rideone.models;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dmaskev on 11/8/15.
 */
@ParseClassName("Ride")
public class Ride extends ParseObject implements CustomSerializable<Ride> {
    public static final String COLUMN_AVAILABLE = "available";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_LOCATION = "start_loc";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_SPOTS = "spots";
    public static final String COLUMN_DRIVER = "driver_id";
    public static final String COLUMN_RIDERS = "riderIds";
    public static final String COLUMN_SPOTS_LEFT = "spotsLeft";

    private User driver;
    private Map<String, Object> fields = new HashMap<>();

    @Override
    public Map<String, Object> getFields() {
        return this.fields;
    }


    @Override
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

    @Override
    public Ride rebuild() {
        String objectId = fields.get("objectId").toString();
        Ride model = Ride.createWithoutData(Ride.class, objectId);

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Object value = entry.getValue();
            model.put(entry.getKey(), value);
        }

        return model;
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

    public void setRiderIds(List<String> riders) {
        put(COLUMN_RIDERS, Joiner.on(',').join(riders));
    }
    public List<String> getRiderIds() {
         String riderIds = getString(COLUMN_RIDERS);
        if(Strings.isNullOrEmpty(riderIds)) {
            return new ArrayList<>();
        }

        return new ArrayList<String>(Arrays.asList(riderIds.split(",")));
    }

    public void setDriverId(String driverId) {
        put(COLUMN_DRIVER, driverId);
    }
    public String getDriverId() {
        return getString(COLUMN_DRIVER);
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public User getDriver() {
        return driver;
    }

    public int getSpotsLeft() {
        return getInt(COLUMN_SPOTS_LEFT);
    }

    public void setSpotsLeft(int spotsLeft) {
        put(COLUMN_SPOTS_LEFT, spotsLeft);
    }

}
