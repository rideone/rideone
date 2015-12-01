package com.walmartlabs.classwork.rideone.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;

public class RideDetailActivity extends AppCompatActivity {

    public static final String RIDE = "ride";
    public static final String DRIVER = "driver";
    private Ride mRide;
    private User mDriver;
    private ImageView ivProfile;
    private TextView tvFullName;
    private TextView tvPhone;
    private View vPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        vPalette = findViewById(R.id.vPalette);

        mRide = ((Ride)getIntent().getSerializableExtra(RIDE));
        mDriver = mRide.getDriver().rebuild();
        mRide = mRide.rebuild();
//        ParseFile profileImage = mRide.getDriver().getProfileImage();
        // Fill views with data
        Picasso.with(this)
                .load(R.mipmap.ic_profile_image)
                .fit().centerCrop()
                .into(ivProfile);

        tvFullName.setText(mDriver.getFullName());
        tvPhone.setText(mDriver.getPhone());

        // Extract FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // Dial contact's number.
        // This shows the dialer with the number, allowing you to explicitly initiate the call.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + mDriver.getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ride_detail, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
