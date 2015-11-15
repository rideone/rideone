package com.walmartlabs.classwork.rideone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.fragments.RideListFragment;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int INTENT_REQUEST_DRIVER_STATUS = 999;

    private User user;
    private Ride ride = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = ((User) getIntent().getSerializableExtra("user")).rebuild();
        //TODO: fetch ride from db based on user id

        RideListFragment rideListFragment = RideListFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, rideListFragment);
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
            //TODO: pass ride
//            ride.flush();
//            intent.putExtra("ride", ride);
//            intent.putExtra("riders", User.flushArray(ride.getRiders()));

            user.flush();
            intent.putExtra("user", user);

            if(ride != null) {
                ride.flush();
                intent.putExtra("ride", ride);
            }

            startActivityForResult(intent, INTENT_REQUEST_DRIVER_STATUS);

        } else if (id == R.id.miFilter) {
            Intent intent = new Intent(this, FilterActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(INTENT_REQUEST_DRIVER_STATUS == requestCode && resultCode == Activity.RESULT_OK) {
            ride = (Ride) data.getSerializableExtra("ride");
            if(ride != null) {
                ride = ride.rebuild();
                //Has to do this because Serialization doesn't happen for relationships
                user.setRide(ride);
                ride.setDriver(user);

                RideListFragment rideListFragment = (RideListFragment) getSupportFragmentManager().findFragmentById(R.id.flContainer);

                //TODO: Fix inefficient way of finding the existing ride
                int existingRidePos = -1;
                for(int i = 0; i < rideListFragment.rides.size(); i++) {
                    if(rideListFragment.rides.get(i).getObjectId().equals(ride.getObjectId())) {
                        existingRidePos = i;
                    }
                }

                if(existingRidePos != -1) {
                    rideListFragment.rides.set(existingRidePos, ride);
                    rideListFragment.aRides.notifyDataSetChanged();
                } else {
                    rideListFragment.aRides.insert(ride, 0);
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);


    }

    public void onProfileView(MenuItem item) {
        Intent intent = new Intent(this, RegisterUserActivity.class);
        intent.putExtra("update", true);
        startActivity(intent);
    }

    public void addUserToWaitList(Ride ride) {
        String userId = user.getObjectId();
        user.setStatus(User.Status.WAIT_LIST);
        user.flush();
        user.saveInBackground();
        List<User> riders = ride.getRiders();
        riders.add(user);
        ride.setRiders(riders);
        ride.flush();
        ride.saveInBackground();
    }

//    private Ride createDummyRide() {
//        Ride ride = new Ride();
//        ride.setDate(Utils.getNextHour());
//        ride.setAvailable(true);
//        User driver = new User();
//        driver.setFirstName("Driver1");
//        ride.setDriver(driver);
//        ride.setRiders(createDummyRiders());
//
//        ride.setSpots(2);
//        return ride;
//    }
//
//    @NonNull
//    private List<User> createDummyRiders() {
//        return Arrays.asList(createUser("Pass", "One", "6506483030", PASSENGER),
//                createUser("Pass", "Two", "6506483031", PASSENGER),
//                createUser("Wait", "One", "6506483033", WAIT_LIST),
//                createUser("Wait", "Two", "6506483034", WAIT_LIST));
//    }
//
//    private User createUser(String name, String lastName, String phone, User.Status status) {
//        User u = new User();
//        u.setFirstName(name);
//        u.setLastName(lastName);
//        u.setPhone(phone);
//        u.setStatus(status);
//        return u;
//    }


}
