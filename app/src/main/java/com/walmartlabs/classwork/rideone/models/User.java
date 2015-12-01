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
    public static final String COLUMN_PROFILE_IMAGE = "profileImage";
    public static final String COLUMN_FULL_NAME = "fullName";
    public static final String COLUMN_PHONE = "phone";

    public String getFullName() {
        String v = getString(COLUMN_FULL_NAME);
        if(isNullOrEmpty(v)) {
            v = "";
        }
        return v;
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
            //ParseFile not serializble. so remove profileimage and fetch it in the next activity
            if(!key.equals("sessionToken") && !key.equals(COLUMN_PROFILE_IMAGE)) {
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

    public void setLoginUserId(String parseUserId) {
        put(COLUMN_LOGIN_USER_ID, parseUserId);
    }

    public String getLoginUserId() {
        return getString(COLUMN_LOGIN_USER_ID);
    }

//    public int getTotalSeats() {
//        return getInt("totalSeats");
//    }

    public void setFullName(String firstName) {
        put(COLUMN_FULL_NAME, firstName);
    }

//    public void setTotalSeats(int totalSeats) {
//        put("totalSeats", totalSeats);
//    }

    public void setPhone(String phone) {
        put(COLUMN_PHONE, phone);
    }

    public String getPhone() {
        return getString(COLUMN_PHONE);
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
        put(COLUMN_PROFILE_IMAGE, file);
    }

    public ParseFile getProfileImage() {
        return getParseFile(COLUMN_PROFILE_IMAGE);
    }

}
