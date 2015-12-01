package com.walmartlabs.classwork.rideone.service;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.walmartlabs.classwork.rideone.R;
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
            String userStatusChanged = getUserStatusChangeString(newUser);
            boolean hasRiderChanged = hasRiderChanged(newRide);
            if (userStatusChanged != null || hasRiderChanged) {
                // Construct an Intent tying it to the ACTION (arbitrary event namespace)
                String message = null;
                if (userStatusChanged != null) {
                    message = "Your ride request has been " + userStatusChanged;
                } else {
                    message = "You either have a new rider request or a rider has dropped out";
                }
                broadcast(message);
            }
            user = newUser;
            if (newRide != null) {
                ride = newRide;
            }
        }

    }

    private void broadcast(String message) {
        Intent in = new Intent(getClass().getName());
        // Put extras into the intent as usual
        in.putExtra("resultCode", Activity.RESULT_OK);
        in.putExtra("message", message);
        // Fire the broadcast with intent packaged
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
        notification(message);

    }

    private void notification(String message) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_car)
                        .setContentTitle("RideOne")
                        .setContentText(message);
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private String getUserStatusChangeString(User newUser) {
        Date currDate = user.getUpdatedAt();
        Date newDate = newUser.getUpdatedAt();
        if (isAfter(currDate, newDate)) {
            if (user.getStatus() == User.Status.WAIT_LIST) {
                if (newUser.getStatus() == User.Status.PASSENGER) {
                    return "confirmed";
                } else if (newUser.getStatus() == User.Status.NO_RIDE) {
                    return "denied";
                }
            }
        }
        return null;
    }

    private boolean isAfter(Date currDate, Date newDate) {
        return newDate == null || currDate == null || newDate.after(currDate);
    }

    private boolean hasRiderChanged(Ride newRide) {
        if (ride != null && isDriver(ride) && ride != newRide) {
            Date currDate = ride.getUpdatedAt();
            Date newDate = newRide.getUpdatedAt();
            if (isAfter(currDate, newDate) && !ride.getRiderIds().equals(newRide.getRiderIds())) {
                return true;
            }
        }
        return false;
    }

    private boolean isDriver(Ride ride) {
        return user.getObjectId().equals(ride.getDriverId());
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
