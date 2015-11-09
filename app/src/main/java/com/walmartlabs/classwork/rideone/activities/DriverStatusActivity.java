package com.walmartlabs.classwork.rideone.activities;

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

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.adapters.PassengerListAdapter;
import com.walmartlabs.classwork.rideone.fragments.TimePickerFragment;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.walmartlabs.classwork.rideone.models.User.Status.DRIVER;
import static com.walmartlabs.classwork.rideone.models.User.Status.NO_RIDE;
import static com.walmartlabs.classwork.rideone.models.User.Status.PASSENGER;
import static com.walmartlabs.classwork.rideone.models.User.Status.WAIT_LIST;
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
 *
 * Created by dmaskev on 11/8/15.
 */
public class DriverStatusActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private EditText etStartTime;
    private Ride ride;
    private User driver;
    private Switch swAvailable;
    private Spinner spDestination;
    private Spinner spStartLoc;
    private ArrayAdapter<User> aPassengers;
    private EditText etSpots;
    private List<User> riders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_status);
        //TODO: navigate back button


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //If Ride doesn't exist, create one with time set to next hour
        //TODO: receive ride and driver from main activity
        driver = createDefaultUser("Driver", "One", "6506483029", DRIVER);
        ride = createDefaultRide(driver);
        riders = new ArrayList<User>(ride.getRiders());


        int[] hourAndMinute = Utils.getLocalHourAndMinute(ride.getDate());
        etStartTime = (EditText) findViewById(R.id.etStartTime);
        etStartTime.setText(Utils.formatDuration(hourAndMinute[0], hourAndMinute[1]));
        etStartTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    TimePickerFragment.createInstance(ride.getDate()).show(getSupportFragmentManager(), "timePicker");
                }

                return true;
            }
        });

        TextView tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        swAvailable = (Switch) findViewById(R.id.swAvailable);
        swAvailable.setChecked(ride.isAvailable());
        swAvailable.setTextColor(tvStartTime.getTextColors());

        ListView lvPassengers = (ListView) findViewById(R.id.lvPassengers);
        aPassengers = new PassengerListAdapter(this, riders, new PassengerListAdapter.PassengerListListener() {
            @Override
            public void onAccept(User user, PassengerListAdapter.ViewHolder vh) {
                vh.ivAccept.setVisibility(View.INVISIBLE);
                user.setStatus(PASSENGER);
            }

            @Override
            public void onRemove(User user, PassengerListAdapter.ViewHolder vh) {
//                //TODO: alert with confirmation
                aPassengers.remove(user);
                user.setStatus(NO_RIDE);
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
        });
        lvPassengers.setAdapter(aPassengers);

        spDestination = (Spinner) findViewById(R.id.spDestination);
        populateSpinner(ride.getDestination(), spDestination);
        spStartLoc = (Spinner) findViewById(R.id.spStartLoc);
        populateSpinner(ride.getStartLocation(), spStartLoc);

        etSpots = (EditText) findViewById(R.id.etSpots);

    }


    private Ride createDefaultRide(User driver) {
        Ride ride = new Ride();
        ride.setDate(Utils.getNextHour());
        ride.setAvailable(true);

        //TODO: remove these dummy passengers
        ride.setRiders(Arrays.asList(createDefaultUser("Pass", "One", "6506483030", PASSENGER),
                createDefaultUser("Pass", "Two", "6506483031", PASSENGER),
                createDefaultUser("Wait", "One", "6506483033", WAIT_LIST),
                createDefaultUser("Wait", "Two", "6506483034", WAIT_LIST)));

        ride.setSpots(2);
        ride.setDriver(driver);
        return ride;
    }

    private User createDefaultUser(String name, String lastName, String phone, User.Status status) {
        User u = new User();
        u.setFirstName(name);
        u.setLastName(lastName);
        u.setPhone(phone);
        u.setStatus(status);
        return u;
    }

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
            onSave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        etStartTime.setText(Utils.formatDuration(hourOfDay, minute));
    }

    private void populateSpinner(String value, Spinner spinner) {
        if(!isNullOrEmpty(value)) {
            int colorPos = ((ArrayAdapter<String>)spinner.getAdapter()).getPosition(value);
            spinner.setSelection(colorPos);
        }
    }

    public void onSave() {
        //TODO: save all changes to ride

        ride.setAvailable(swAvailable.isChecked());
        ride.setDestination(spDestination.getSelectedItem().toString());
        ride.setDate(parseHourAndMinute(etStartTime.getText().toString()));
        ride.setSpots(Integer.parseInt(etSpots.getText().toString()));
        ride.setStartLocation(spStartLoc.getSelectedItem().toString());

        ride.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(DriverStatusActivity.class.getSimpleName(), "Saved ride");
                if (e != null) {
                    Log.e(DriverStatusActivity.class.getSimpleName(), "Failed to save ride", e);
                    Toast.makeText(DriverStatusActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TODO: return Ride to main activity

        finish();
    }

}
