package com.walmartlabs.classwork.rideone.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.adapters.PassengerListAdapter;
import com.walmartlabs.classwork.rideone.fragments.TimePickerFragment;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.ParseUtil;
import com.walmartlabs.classwork.rideone.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.walmartlabs.classwork.rideone.models.Ride.COLUMN_DRIVER;
import static com.walmartlabs.classwork.rideone.models.User.Status.NO_RIDE;
import static com.walmartlabs.classwork.rideone.models.User.Status.PASSENGER;
import static com.walmartlabs.classwork.rideone.util.Utils.getLocalHourAndMinute;
import static com.walmartlabs.classwork.rideone.util.Utils.parseHourAndMinute;

/**
 * Activity for managing the ride and the driver's status
 * * set driver available/unavailable - which translates to creating a an active Ride or deactivating a Ride
 * * set Time
 * * set Destination
 * * set Start Point
 * * set number of spots available
 * * list passengers pending/confirmed
 * * confirm/remove a passenger
 * <p/>
 * Created by dmaskev on 11/8/15.
 */
public class DriverStatusActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    public static final int DEFAULT_SPOTS = 2;

    private EditText etStartTime;
    private Ride ride;
    private User driver;
    //    private ParseProxyObject proxyRide;
    private Switch swAvailable;
    private Spinner spDestination;
    private Spinner spStartLoc;
    private ArrayAdapter<User> aPassengers;
    private EditText etSpots;
    private List<User> riders = new ArrayList<>();
    private List<User> removePassengers = new ArrayList<>();

    PassengerListAdapter.PassengerListListener passengerListListener = new PassengerListAdapter.PassengerListListener() {
        @Override
        public void onAccept(User user, PassengerListAdapter.ViewHolder vh) {
            vh.ivAccept.setVisibility(View.INVISIBLE);
            vh.tvAccept.setVisibility(View.INVISIBLE);
            user.setStatus(PASSENGER);
        }

        @Override
        public void onRemove(User user, PassengerListAdapter.ViewHolder vh) {
//                //TODO: alert with confirmation
            aPassengers.remove(user);
            user.setStatus(NO_RIDE);
            removePassengers.add(user);
        }

        @Override
        public void onPhoneCall(User user, PassengerListAdapter.ViewHolder vh) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + user.getPhone()));
            Context context = DriverStatusActivity.this;
            if (Utils.checkCallPermission(context)) {
                context.startActivity(callIntent);
            } else {
                Toast.makeText(context, "Phone call is not permitted", Toast.LENGTH_LONG).show();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_status);
        //TODO: navigate back button
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //If Ride doesn't exist, create one with time set to next hour
        //TODO: receive ride and driver from main activity


        driver = ((User) getIntent().getSerializableExtra("user")).rebuild();
        Ride rideArg = ((Ride) getIntent().getSerializableExtra("ride"));
        if(rideArg != null) {
            ride = rideArg.rebuild();
        }

        if (ride != null) {
            setupRideInfo();
        }
        else {
            ParseQuery<Ride> query = ParseQuery.getQuery(Ride.class);
            query.whereEqualTo(COLUMN_DRIVER, driver.getObjectId());
            query.findInBackground(new FindCallback<Ride>() {
                public void done(List<Ride> rideList, ParseException e) {

                      if (e != null) {
                        Log.e(DriverStatusActivity.class.getSimpleName(), "Failed fetching rides for driver " + driver.getObjectId());
                        alert(R.string.alert_network_error);
                        return;
                    } else {
                        Log.d(DriverStatusActivity.class.getSimpleName(), "Retrieved " + rideList.size() + " rides");
                        if (rideList != null && !rideList.isEmpty()) {
                            ride = rideList.get(0);
                        } else {
                            ride = createDefaultRide(driver);
                        }

                        setupRideInfo();
                    }
                }
            });
        }
//        fetchedComment.getParseObject("post")
//                .fetchIfNeededInBackground(new GetCallback<ParseObject>() {
//                    public void done(ParseObject post, ParseException e) {
//                        String title = post.getString("title");
//                        // Do something with your new title variable
//                    }
//                });

//        ride = ((Ride) getIntent().getSerializableExtra("ride")).rebuild();
//        proxyRide = (ParseProxyObject) getIntent().getSerializableExtra("ride");
//        List<ParseProxyObject> proxyRiders = (List<ParseProxyObject>) getIntent().getSerializableExtra("riders");


    }

    private Ride createDefaultRide(User driver) {
        Ride ride = new Ride();
        ride.setAvailable(true);
        ride.setDate(new Date());
        ride.setRiders(new ArrayList<User>());
        ride.setSpots(DEFAULT_SPOTS);
        ride.setDriverId(driver.getObjectId());
        return ride;
    }

    private void setupRideInfo() {
        setupPassengerList();
        setupStartTimeWidget();
        setupSwitchWidget();
        setupSpinners();

        etSpots = (EditText) findViewById(R.id.etSpots);
        etSpots.setText(String.valueOf(ride.getSpots()));
    }

    private void setupPassengerList() {
        riders.clear();
        removePassengers.clear();

        boolean isDbCallNeeded = true;
        if (ride.getRiders() != null) {
            riders.addAll(ride.getRiders());
            isDbCallNeeded = false;
        }

        ListView lvPassengers = (ListView) findViewById(R.id.lvPassengers);
        aPassengers = new PassengerListAdapter(this, riders, passengerListListener);
        lvPassengers.setAdapter(aPassengers);

        if (!isDbCallNeeded) {
            return;
        }

        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(User.COLUMN_RIDE, ride);
        query.findInBackground(new FindCallback<User>() {
            public void done(List<User> list, ParseException e) {
                if (e != null) {
                    Log.e(DriverStatusActivity.class.getSimpleName(), "Failed fetching riders for ride " + ride.getObjectId());
                    alert(R.string.alert_network_error);
                    return;
                } else {
                    Log.d(DriverStatusActivity.class.getSimpleName(), "Retrieved " + list.size() + " riders");
                    if (list != null && !list.isEmpty()) {
//                            riders.addAll(list);
                        aPassengers.addAll(list);
//                            aPassengers.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void setupSpinners() {
        spDestination = (Spinner) findViewById(R.id.spDestination);
        populateSpinner(ride.getDestination(), spDestination);
        spStartLoc = (Spinner) findViewById(R.id.spStartLoc);
        populateSpinner(ride.getStartLocation(), spStartLoc);
    }

    private void setupSwitchWidget() {
        TextView tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        swAvailable = (Switch) findViewById(R.id.swAvailable);
        swAvailable.setChecked(ride.isAvailable());
        swAvailable.setTextColor(tvStartTime.getTextColors());
    }

    private void setupStartTimeWidget() {
        final int[] hourAndMinute = getLocalHourAndMinute(ride.getDate());
        etStartTime = (EditText) findViewById(R.id.etStartTime);
        etStartTime.setText(Utils.formatDuration(hourAndMinute[0], hourAndMinute[1]));
        etStartTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    TimePickerFragment.createInstance(hourAndMinute[0], hourAndMinute[1]).show(getSupportFragmentManager(), "timePicker");
                }

                return true;
            }
        });
    }


//    private Ride createDefaultRide(User driver) {
//        Ride ride = new Ride();
//        ride.setDate(Utils.getNextHour());
//        ride.setAvailable(true);
//
//        //TODO: remove these dummy passengers
//        ride.setRiders(createDummyRiders());
//
//        ride.setSpots(2);
//        ride.setDriver(driver);
//        return ride;
//    }

//    @NonNull
//    private List<User> createDummyRiders() {
//        return Arrays.asList(createDefaultUser("Pass", "One", "6506483030", PASSENGER),
//                createDefaultUser("Pass", "Two", "6506483031", PASSENGER),
//                createDefaultUser("Wait", "One", "6506483033", WAIT_LIST),
//                createDefaultUser("Wait", "Two", "6506483034", WAIT_LIST));
//    }

//    private User createDefaultUser(String name, String lastName, String phone, User.Status status) {
//        User u = new User();
//        u.setFirstName(name);
//        u.setLastName(lastName);
//        u.setPhone(phone);
//        u.setStatus(status);
//        return u;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_driver_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.mi_driver_status_save) {
            onSave(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(ParseUtil.class.getSimpleName(), "Failed to save ride info ", e);
                        alert(R.string.alert_network_error);
                    }
                    Intent data = new Intent();
                    ride.flush();
                    data.putExtra("ride", ride);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        etStartTime.setText(Utils.formatDuration(hourOfDay, minute));
    }

    private void populateSpinner(String value, Spinner spinner) {
        if (!isNullOrEmpty(value)) {
            int colorPos = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(value);
            spinner.setSelection(colorPos);
        }
    }

    public void onSave(final SaveCallback callback) {
        ride = populateRideInfo(ride);

        //If ride already exists: update ride in parallel with updating passengers
        if (!isNullOrEmpty(ride.getObjectId())) {

            assignPassengers(riders, removePassengers, ride);

            //Combine riders, removePassengers and ride into one list
            List<ParseObject> models = Utils.joinModelLists(riders, removePassengers, Arrays.asList(ride));


            ParseUtil.saveInBatch(models, callback);
        }
        //If ride doesn't exist: create ride first, and then update the passengers
        else {
            ride.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(ParseUtil.class.getSimpleName(), "Failed to create ride ", e);
                        alert(R.string.alert_network_error);
                        return;
                    }

                    assignPassengers(riders, removePassengers, ride);
                    List<User> passengers = Utils.joinLists(riders, removePassengers);
                    ParseUtil.saveInBatch(passengers, callback);
                }
            });


        }
    }

//    @NonNull
//    private SaveCallback createSaveCallback() {
//        return new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                Log.d(DriverStatusActivity.class.getSimpleName(), "Saved ride");
//                if (e != null) {
//                    Log.e(DriverStatusActivity.class.getSimpleName(), "Failed to save ride", e);
//                    Toast.makeText(DriverStatusActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//    }

    private Ride populateRideInfo(Ride ride) {
        ride.setAvailable(swAvailable.isChecked());
        ride.setDestination(spDestination.getSelectedItem().toString());
        ride.setDate(parseHourAndMinute(etStartTime.getText().toString()));
        ride.setSpots(Integer.parseInt(etSpots.getText().toString()));
        ride.setStartLocation(spStartLoc.getSelectedItem().toString());

        return ride;

//        //If ride already exists then save passengers in parallel
//        if(!isNullOrEmpty(ride.getObjectId())) {
//            ride.saveInBackground();
//            savePassengers(riders, ride);
//        }
//        //If ride doesn't exist yet, first save ride, and then its passengers
//        else {
//            ride.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        savePassengers(riders, ride);
//                    } else {
//                        alert(R.string.alert_network_error);
//                    }
//                }
//            });
//
//        }


    }

    private void alert(int msgResource) {
        Toast.makeText(DriverStatusActivity.this, msgResource, Toast.LENGTH_LONG).show();
    }

    private void assignPassengers(List<User> passengers, List<User> removePassengers, Ride ride) {
        for (User rider : passengers) {
            rider.setRide(ride);
        }

        for (User rider : removePassengers) {
            rider.setRide(null);
        }
    }

//    private void savePassengers(List<User> passengers, List<User> removePassengers, Ride ride, final SaveCallback callback) {
//
//        ParseUtil.saveInBatch(users, callback);
//    }

}
