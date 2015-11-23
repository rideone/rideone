package com.walmartlabs.classwork.rideone.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;

/**
 * Created by mkrish4 on 10/22/15.
 */
public class RideOneApplication extends Application {
//    private static final String YOUR_APPLICATION_ID = "O66Xp5K5jnjoLwffwK0h62tH2yz6niLHZHEZBE2S";
//    private static final String YOUR_CLIENT_KEY = "mPzWVrde3weHblcGgYzzAw5Tar33tyXwm4pOA37v";

    public static final String YOUR_APPLICATION_ID = "dCEUtYeFs0Hd5XMgmUlqsjmHp8wwlEXkcdBnXJ3Y";
    public static final String YOUR_CLIENT_KEY = "kp8cyn1fr90UK2kVYX6etugkL0nncthHAg0l57lY";

    @Override
    public void onCreate() {
        super.onCreate();
//        Parse.enableLocalDatastore(this);
        // Register your parse models here
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Ride.class);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);
        ParseUser.enableRevocableSessionInBackground();
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("test",true);
        installation.saveInBackground();
    }
}
