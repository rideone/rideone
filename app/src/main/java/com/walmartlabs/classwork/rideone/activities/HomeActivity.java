package com.walmartlabs.classwork.rideone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.fragments.DriverListFragment;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.walmartlabs.classwork.rideone.models.User.Status.PASSENGER;
import static com.walmartlabs.classwork.rideone.models.User.Status.WAIT_LIST;

public class HomeActivity extends AppCompatActivity {

    private User user;
//    private ParseProxyObject proxyUser;
//    private ParseProxyObject proxyRide;
//    private List<ParseProxyObject> proxyRiders;
    private Ride ride = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = ((User) getIntent().getSerializableExtra("user")).rebuild();
        //TODO: fetch ride from db based on user id
        ride = createDummyRide();
//        proxyRide = new ParseProxyObject(ride);

        //TODO: fetch riders associated with this ride from db
        List<User> riders = ride.getRiders();

        //TODO: need real user for creating/canceling ride requests
        user = null;


        DriverListFragment driverListFragment = DriverListFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, driverListFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            ParseUser.logOut();
            finish();
        } else if (id == R.id.miStatus) {
            Intent intent = new Intent(this, DriverStatusActivity.class);
//            intent.putExtra("driver", proxyUser);

            if(ride == null) {
                Ride ride = new Ride();
                ride.setAvailable(true);
                ride.setDate(new Date());
                ride.setRiders(new ArrayList<User>());
                //TODO: preset ride values from driver's profile
            }

            ride.flush();
            intent.putExtra("ride", ride);
            intent.putExtra("riders", User.flushArray(ride.getRiders()));

            startActivity(intent);

            //TODO: startActivityForResult - get updated Ride
        }

        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem item) {
        Intent intent = new Intent(this, RegisterUserActivity.class);
        intent.putExtra("update", true);
        startActivity(intent);
    }

    private Ride createDummyRide() {
        Ride ride = new Ride();
        ride.setDate(Utils.getNextHour());
        ride.setAvailable(true);

        ride.setRiders(createDummyRiders());

        ride.setSpots(2);
        return ride;
    }

    @NonNull
    private List<User> createDummyRiders() {
        return Arrays.asList(createUser("Pass", "One", "6506483030", PASSENGER),
                createUser("Pass", "Two", "6506483031", PASSENGER),
                createUser("Wait", "One", "6506483033", WAIT_LIST),
                createUser("Wait", "Two", "6506483034", WAIT_LIST));
    }

    private User createUser(String name, String lastName, String phone, User.Status status) {
        User u = new User();
        u.setFirstName(name);
        u.setLastName(lastName);
        u.setPhone(phone);
        u.setStatus(status);
        return u;
    }


}
