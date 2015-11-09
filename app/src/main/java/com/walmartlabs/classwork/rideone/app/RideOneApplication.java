package com.walmartlabs.classwork.rideone.app;

import android.app.Application;

import com.parse.Parse;
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

    public static final String YOUR_APPLICATION_ID = "YdbpBjG3E8iXNESLZwJSqfeed1jLJekJqEnoZE9j";
    public static final String YOUR_CLIENT_KEY = "HetcGiLMeQCotj5BIJWtPTMoZNHzzQmIyMRjZLLF";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        // Register your parse models here
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Ride.class);
        Parse.initialize(this, "925H5Y5W7c3MF4qGZAW1jQSdOB4PxsFUn8bjiDvk", "CHlz5l7WQbIVw8tzSOFklabYwIhjuFBWQzXvKuXV");
        ParseUser.enableRevocableSessionInBackground();
    }
}
