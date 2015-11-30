package com.walmartlabs.classwork.rideone.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Created by mkrish4 on 11/7/15.
 */
@ParseClassName("app_user")
public class User extends ParseObject implements CustomSerializable<User> {

    public static final String COLUMN_RIDE = "rideId";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ID = "objectId";
    public static final String COLUMN_LOGIN_USER_ID = "loginUserId";

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }


    public enum Status {
        PASSENGER, DRIVER, WAIT_LIST, NO_RIDE
    }

    private Map<String, Object> fields = new HashMap<>();

    @Override
    public Map<String, Object> getFields() {
        return this.fields;
    }

    @Override
    public User flush() {
        for(String key : keySet()) {
            if(!key.equals("sessionToken")) {
                fields.put(key, get(key));
            }

        }

        fields.put("objectId", getObjectId());
        return this;
    }

    @Override
    public User rebuild() {
        String objectId = fields.get("objectId").toString();
        User model = User.createWithoutData(User.class, objectId);

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Object value = entry.getValue();
            model.put(entry.getKey(), value);
        }

        return model;
    }

    public static ArrayList<User> flushArray(List<User> users) {
        ArrayList<User> res = new ArrayList<>(users.size());
        for(User user : users) {
            res.add(user.flush());
        }

        return res;
    }




    public String getFirstName() {
        return getString("firstName");
    }

    public void setLoginUserId(String parseUserId) {
        put(COLUMN_LOGIN_USER_ID, parseUserId);
    }

    public String getLoginUserId() {
        return getString(COLUMN_LOGIN_USER_ID);
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
        String statusStr = getString(COLUMN_STATUS);
        return (isNullOrEmpty(statusStr) ? Status.NO_RIDE : Status.valueOf(statusStr));
    }
    
    public void setRideId(String rideId) {
        put(COLUMN_RIDE, rideId);
    }

    public String getRideId() {
        try {
            return fetchIfNeeded().getString(COLUMN_RIDE);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isDriver() {
        return getStatus() == Status.DRIVER;
    }

    public void setProfileImage(ParseFile file) {
        put("profileImage", file);
    }

    public ParseFile getProfileImage() {
        return getParseFile("profileImage");
    }

}
