package com.walmartlabs.classwork.rideone.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.activities.HomeActivity;
import com.walmartlabs.classwork.rideone.activities.RideDetailActivity;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by abalak5 on 10/21/15.
 */
public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.VH> {

    private HomeActivity mContext;
    private List<Ride> mRides;
    private User mUser;
    private int bgColor;
    private int colorDriving;
    private int colorRequested;

    public RideListAdapter(Context context, List<Ride> rides, User currentUser) {
        mRides = rides;
        mContext = (HomeActivity) context;
        mUser = currentUser;

        bgColor = ContextCompat.getColor(context, R.color.bgGray);
        colorDriving = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        colorRequested = ContextCompat.getColor(context, R.color.colorRideRequested);
    }

    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_list_item, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(final VH viewHolder, int position) {
        final Ride ride = mRides.get(position);

        User userInfo = mContext.getUserInfo();
        String userId = userInfo.getObjectId();
        if(ride.getDriverId().equalsIgnoreCase(userId)) {
            viewHolder.ivReserve.setVisibility(INVISIBLE);
            viewHolder.ivCancel.setVisibility(INVISIBLE);
            viewHolder.tvRibbon.setText("Driving");
            viewHolder.tvRibbon.setBackgroundColor(colorDriving);
            viewHolder.tvRibbon.setVisibility(VISIBLE);

        } else {
            //user has requested a ride or has been confirmed
            String rideIdOfUser = (userInfo.getRideId() != null ? userInfo.getRideId() : null);
            if (rideIdOfUser != null && rideIdOfUser.equalsIgnoreCase(ride.getObjectId())) {
                viewHolder.tvRibbon.setVisibility(VISIBLE);
                viewHolder.tvRibbon.setText("Riding");
                viewHolder.tvRibbon.setBackgroundColor(colorDriving);
                if(userInfo.getStatus().equals(User.Status.WAIT_LIST)) {
                    viewHolder.tvRibbon.setText("Requested");
                    viewHolder.tvRibbon.setBackgroundColor(colorRequested);
                }

                viewHolder.ivReserve.setVisibility(INVISIBLE);
                viewHolder.ivCancel.setVisibility(VISIBLE);
            } else {
                viewHolder.ivReserve.setVisibility(VISIBLE);
                viewHolder.ivCancel.setVisibility(INVISIBLE);
                viewHolder.tvRibbon.setVisibility(INVISIBLE);
            }
        }

        viewHolder.ivReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                context.openReserveRideDialog(ride);
                mContext.reserveRideRequest(ride);
            }
        });

        viewHolder.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.cancelRideAndSendRequest(null);
                viewHolder.ivCancel.setVisibility(INVISIBLE);
            }
        });

        addOnClickToDetailsActivity(ride, viewHolder.rlDetails, viewHolder.ivProfile, viewHolder.tvFullName);

        //why do this?
        viewHolder.rootView.setTag(ride);
        // Populate data into the template view using the data object
        viewHolder.tvFullName.setText(Html.fromHtml(ride.getDriver().getFullName()));

        //TODO: should use resource plurals for 'spots' word http://developer.android.com/guide/topics/resources/string-resource.html#Plurals
        viewHolder.tvStartLoc.setText(ride.getStartLocation());
        viewHolder.tvDestination.setText(ride.getDestination());
        viewHolder.tvSpotsAvailable.setText(Html.fromHtml(String.valueOf(ride.getSpotsLeft()) + " spots"));

        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String startTime = df.format(ride.getDate());
        viewHolder.tvTime.setText(startTime);
/*            Date tweetDate = getTimeStamp(user.getCreatedAt());
            String timeStamp = getRelativeTimeStamp(currDate, tweetDate);
            viewHolder.tvRelativeTimeStamp.setText(timeStamp);*/
        ParseFile profileImage = ride.getDriver().getProfileImage();
        viewHolder.ivProfile.setImageResource(0);
        Transformation transformation = new RoundedTransformationBuilder()
                .borderWidthDp(1)
                .borderColor(bgColor)
                .cornerRadiusDp(50)
                .oval(false)
                .build();

        RequestCreator requestCreator = null;
        if (profileImage != null) {
            requestCreator = Picasso.with(mContext)
                    .load(profileImage.getUrl());
        } else {
            //this is to solve stale image because of recycling views. when we scroll down the already inflated list item is re-used
            //so we see the same image that we see at position 0 for postiion 4.
            requestCreator = Picasso.with(mContext)
                    .load(R.mipmap.ic_profile_image);
        }

        requestCreator
                .fit()
                .transform(transformation)
                .into(viewHolder.ivProfile);

        Picasso.with(mContext)
                .load(R.drawable.ic_belt_blue_dark)
                .fit()
                .transform(transformation)
                .into(viewHolder.ivReserve);
        Picasso.with(mContext)
                .load(R.drawable.ic_belt_unclick)
                .fit()
                .transform(transformation)
                .into(viewHolder.ivCancel);
//        Picasso.with(mContext)
//                .load(R.drawable.ic_belt_noarrows2)
//                .fit()
//                .transform(transformation)
//                .into(viewHolder.ivReserveDone);
    }

    private void navigateToDetailsActivity(Ride ride, View ivProfile, View tvFullName) {
        Intent intent;
//        ActivityOptionsCompat options = ActivityOptionsCompat.
//                makeSceneTransitionAnimation(mContext, ivProfile, "profile");

        Pair<View, String> p1 = Pair.create(ivProfile, "profile");
        Pair<View, String> p2 = Pair.create(tvFullName, "fullname");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(mContext, p1, p2);

        intent = new Intent(mContext, RideDetailActivity.class);
        ride.getDriver().flush();
        intent.putExtra("ride", ride.flush());
        intent.putExtra("user", mUser.flush());


        mContext.startActivity(intent, options.toBundle());
    }

    @Override
    public int getItemCount() {
        return mRides.size();
    }

    // Provide a reference to the views for each contact item
    public final class VH extends RecyclerView.ViewHolder implements View.OnClickListener {
        final View rootView;
        public ImageView ivProfile;
        public TextView tvFullName;
        public TextView tvTime;
        public TextView tvSpotsAvailable;
        public TextView tvStartLoc;
        public TextView tvDestination;
        public TextView tvRibbon;
        public ImageView ivReserve;

        public Context mContext;
        public RelativeLayout rlDetails;
        public ImageView ivCancel;


        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            ivCancel = (ImageView)itemView.findViewById(R.id.ivCancel);
            tvFullName = (TextView)itemView.findViewById(R.id.tvFullName);
            tvSpotsAvailable = (TextView)itemView.findViewById(R.id.tvSpotsAvailable);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
            tvDestination = (TextView)itemView.findViewById(R.id.tvDestination);
            tvRibbon = (TextView)itemView.findViewById(R.id.tvRibbon);
            tvStartLoc = (TextView)itemView.findViewById(R.id.tvStartLoc);
            ivReserve = (ImageView)itemView.findViewById(R.id.ivReserve);

            rlDetails = (RelativeLayout) itemView.findViewById(R.id.rlDetails);
        }

        @Override
        public void onClick(View v) {
            Ride ride = (Ride) rootView.getTag();
            if (ride != null) {
                Intent i = new Intent(mContext, RideDetailActivity.class);
                i.putExtra("ride", ride);
                mContext.startActivity(i);
            }
        }
    }

    private void addOnClickToDetailsActivity(final Ride ride, View curView, final View ivProfile, final View tvFullName) {
        curView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDetailsActivity(ride, ivProfile, tvFullName);
            }
        });
    }
}
