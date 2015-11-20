package com.walmartlabs.classwork.rideone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.fragments.ReserveRideDialog;
import com.walmartlabs.classwork.rideone.fragments.RideListFragment;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.ParseUtil;
import com.walmartlabs.classwork.rideone.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ReserveRideDialog.ReserveRideListener {

    private static final int INTENT_REQUEST_DRIVER_STATUS = 999;
    private static final int INTENT_SET_FILTER = 888;

    private User user;
    private Ride ride = null;
    private RideListFragment rideListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = ((User) getIntent().getSerializableExtra("user")).rebuild();
        //TODO: fetch ride from db based on user id

        rideListFragment = RideListFragment.newInstance();
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
            startActivityForResult(intent, INTENT_SET_FILTER);
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
//                user.setRide(ride);
                ride.setDriver(user);
                RideListFragment rideListFragment = (RideListFragment) getSupportFragmentManager().findFragmentById(R.id.flContainer);

                //TODO: Fix inefficient way of finding the existing ride
                int existingRidePos = -1;
                for(int i = 0; i < rideListFragment.rides.size(); i++) {
                    if(rideListFragment.rides.get(i).getObjectId().equals(ride.getObjectId())) {
                        existingRidePos = i;
                    }
                }

                //Not available and ride exists
                if(!ride.isAvailable() && existingRidePos != -1) {
                    rideListFragment.rides.remove(existingRidePos);
                }

                //Available and ride exists
                if(ride.isAvailable() && existingRidePos != -1) {
                    rideListFragment.rides.set(existingRidePos, ride);
                }
                //Available and ride doesn't exist
                else if(ride.isAvailable() && existingRidePos == -1) {
                    rideListFragment.aRides.insert(ride, 0);
                }

                rideListFragment.aRides.notifyDataSetChanged();
            }
        } else if(INTENT_SET_FILTER == requestCode && resultCode == Activity.RESULT_OK) {
            rideListFragment.fetchAndPopulateRideList();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onProfileView(MenuItem item) {
        Intent intent = new Intent(this, RegisterUserActivity.class);
        intent.putExtra("update", true);
        startActivity(intent);
    }

    public void openReserveRideDialog(Ride ride) {
        boolean rideRequested = false;
        if(user.getStatus().equals(User.Status.WAIT_LIST) ||
                user.getStatus().equals(User.Status.PASSENGER)) rideRequested = true;
        FragmentManager fm = getSupportFragmentManager();
        ReserveRideDialog reserveRideDialog= ReserveRideDialog.newInstance(ride, rideRequested);
        reserveRideDialog.show(fm, "fragment_reserve_ride");
    }

    //TODO: refactor code to move logic to fragment layer
    @Override
    public void reserveRideRequest(final Ride ride) {
        //if user already reserved/requested a ride, then remove ride
        if(user.getStatus().equals(User.Status.WAIT_LIST)
                || user.getStatus().equals(User.Status.PASSENGER)) {
            String rideId = user.getRideId();
            ParseQuery query = ParseQuery.getQuery(Ride.class);
            query.whereEqualTo("objectId", rideId);
            query.getFirstInBackground(new GetCallback<Ride>() {
                @Override
                public void done(Ride prevRide, ParseException e) {
                    List<String> riderIds = prevRide.getRiderIds();
                    riderIds.remove(user.getObjectId());
                    prevRide.setRiderIds(riderIds);
                    prevRide.setSpotsLeft(prevRide.getSpotsLeft() + 1);
                    prevRide.flush();
                    prevRide.saveInBackground();
                    sendRequest(ride);
                }
            });
        } else {
            sendRequest(ride);
        }
    }

    public void sendRequest(Ride ride) {
        user.setStatus(User.Status.WAIT_LIST);
        user.setRideId(ride.getObjectId());
        user.flush();
        List<String> riders = ride.getRiderIds();
        if(riders == null) {
            riders = new ArrayList<>();
        }
        riders.add(user.getObjectId());
        ride.setRiderIds(riders);
        ride.flush();

        SaveCallback callback = new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(HomeActivity.this, "Request sent to driver", Toast.LENGTH_SHORT).show();
                //TODO: instead of refreshing the entire list, just update prev ride and current ride(reserve button)
                rideListFragment.fetchAndPopulateRideList();
            }
        };
        List<ParseObject> models = Utils.joinModelLists(Arrays.asList(user), Arrays.asList(ride));
        ParseUtil.saveInBatch(models, callback);
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

    public User getUserInfo() {

        try {
            //get latest updated user info
            user = ParseQuery.getQuery(User.class).get(user.getObjectId());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return user;
    }

}
