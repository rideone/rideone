package com.walmartlabs.classwork.rideone.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;

import java.util.Date;

import static com.walmartlabs.classwork.rideone.models.User.COLUMN_LOGIN_USER_ID;

/**
 * Created by mkrish4 on 11/24/15.
 */
public class StatusCheckService extends IntentService {
    private static User user;
    private static Ride ride;

    public StatusCheckService() {
        super(StatusCheckService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Fetch data passed into the intent on start
        if (user == null) {
            user = ((User) intent.getSerializableExtra("user")).rebuild();
            if (user.getRideId() != null) {
                ride = fetchRide(user.getRideId());
            }
        }
        User newUser = fetchUser(user.getLoginUserId());
        Ride newRide = null;
        if (newUser != null) {
            if (newUser.getRideId() != null) {
                newRide = fetchRide(newUser.getRideId());
                if (ride == null) {
                    ride = newRide;
                }
            }
            boolean userStatusChanged = hasUserStatusChanged(newUser);
            boolean hasRiderRequest = hasNewRiderRequest(newRide);
            if (userStatusChanged || hasRiderRequest) {
                // Construct an Intent tying it to the ACTION (arbitrary event namespace)
                String message = null;
                if (userStatusChanged) {
                    message = "Your ride request has been confirmed";
                } else {
                    message = "You either have a new rider request or a rider has dropped out";
                }
                Intent in = new Intent(getClass().getName());
                // Put extras into the intent as usual
                in.putExtra("resultCode", Activity.RESULT_OK);
                in.putExtra("message", message);
                // Fire the broadcast with intent packaged
                LocalBroadcastManager.getInstance(this).sendBroadcast(in);
            }
            user = newUser;
            if (newRide != null) {
                ride = newRide;
            }
        }

    }

    private boolean hasUserStatusChanged(User newUser) {
        Date currDate = user.getUpdatedAt();
        Date newDate = newUser.getUpdatedAt();
        return (isAfter(currDate, newDate) && (user.getStatus() == User.Status.WAIT_LIST && newUser.getStatus() == User.Status.PASSENGER));
    }

    private boolean isAfter(Date currDate, Date newDate) {
        return newDate == null || currDate == null || newDate.after(currDate);
    }

    private boolean hasNewRiderRequest(Ride newRide) {
        if (ride != null && ride != newRide) {
            Date currDate = ride.getUpdatedAt();
            Date newDate = newRide.getUpdatedAt();
            if (isAfter(currDate, newDate) && !ride.getRiderIds().equals(newRide.getRiderIds())) {
                return true;
            }
        }
        return false;
    }

    private User fetchUser(final String loginUserId) {
        return fetchEntity(loginUserId, COLUMN_LOGIN_USER_ID, User.class);
    }

    private Ride fetchRide(final String rideId) {
        return fetchEntity(rideId, "objectId", Ride.class);
    }

    private <T extends ParseObject> T fetchEntity(final String entityId, String idColName, Class<T> tClass) {
        ParseQuery<T> query = ParseQuery.getQuery(tClass);
        query.whereEqualTo(idColName, entityId);
//        query.whereGreaterThan("updatedAt", lastSyncTime);
        try {
            return query.getFirst();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Failed to get entity for entityId " + entityId, e);
        }
        return null;
    }

}
