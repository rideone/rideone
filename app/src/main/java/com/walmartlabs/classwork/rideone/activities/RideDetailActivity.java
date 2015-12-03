package com.walmartlabs.classwork.rideone.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.Utils;

import static com.walmartlabs.classwork.rideone.models.User.COLUMN_LOGIN_USER_ID;

public class RideDetailActivity extends AppCompatActivity {

    public static final String RIDE = "ride";
    public static final String DRIVER = "driver";
    private Ride mRide;
    private User mDriver;
    private User mCurrentUser;
    private ImageView ivProfile;
    private TextView tvFullName;
    private TextView tvPhone;
    private View vPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvFullName = (TextView) findViewById(R.id.tvFullName);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        vPalette = findViewById(R.id.vPalette);

        mRide = ((Ride)getIntent().getSerializableExtra(RIDE));
        mCurrentUser = ((User)getIntent().getSerializableExtra("user")).rebuild();
        mDriver = mRide.getDriver().rebuild();
        mRide = mRide.rebuild();

        ParseQuery<User> query = ParseQuery.getQuery(User.class);
        query.whereEqualTo(COLUMN_LOGIN_USER_ID, mDriver.getLoginUserId());
        query.getFirstInBackground(new GetCallback<User>() {
            @Override
            public void done(User user, ParseException e) {
                //        ParseFile profileImage = mRide.getDriver().getProfileImage();
                // Fill views with data
                RequestCreator requestCreator;
                if(user.getProfileImage() != null) {
                    requestCreator = Picasso.with(RideDetailActivity.this)
                            .load(user.getProfileImage().getUrl());
                } else {
                    requestCreator = Picasso.with(RideDetailActivity.this)
                            .load(R.mipmap.ic_profile_image);
                }

                requestCreator
                        .fit().centerCrop()
                        .into(ivProfile);
            }
        });

        tvFullName.setText(mDriver.getFullName());
        tvPhone.setText(mDriver.getPhone());

        // Extract FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // Dial contact's number.
        // This shows the dialer with the number, allowing you to explicitly initiate the call.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + mDriver.getPhone()));
                Context context = RideDetailActivity.this;
                if (Utils.checkCallPermission(context)) {
                    context.startActivity(callIntent);
                } else {
                    Toast.makeText(context, "Phone call is not permitted", Toast.LENGTH_LONG).show();
                }
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
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.putExtra("user", mCurrentUser.flush());
            NavUtils.navigateUpTo(this, upIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
