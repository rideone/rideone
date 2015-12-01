package com.walmartlabs.classwork.rideone.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.activities.HomeActivity;
import com.walmartlabs.classwork.rideone.activities.RideDetailActivity;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by abalak5 on 10/21/15.
 */
public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.VH> {
    private HomeActivity mContext;
    private List<Ride> mRides;

    public RideListAdapter(Context context, List<Ride> rides) {
        mRides = rides;
        mContext = (HomeActivity) context;
    }

    public static Date getTimeStamp(String date) throws ParseException {
        final String FORMAT_TIME="EEE MMM dd HH:mm:ss z yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(FORMAT_TIME);
        sf.setTimeZone(TimeZone.getTimeZone("GMT-04:00"));
        sf.setLenient(true);
        return sf.parse(date);
    }

    public static String getRelativeTimeStamp(Date currDate, Date tweetDate) {

        String timeStamp = "";
        long difference = currDate.getTime() - tweetDate.getTime();

        long seconds = 1000;
        long minutes = seconds * 60;
        long hours = minutes * 60;
        long days = hours * 24;

        long elapsedDays = difference / days ;
        timeStamp += elapsedDays > 0 && timeStamp.equalsIgnoreCase("") ? Long.toString(elapsedDays) + "d" : "";
        difference = difference % days;

        long elapsedHours = difference / hours;
        timeStamp += elapsedHours > 0 && timeStamp.equalsIgnoreCase("") ? Long.toString(elapsedHours) + "h" : "";
        difference = difference % hours;

        long elapsedMinutes = difference / minutes;
        timeStamp += elapsedMinutes > 0 && timeStamp.equalsIgnoreCase("") ? Long.toString(elapsedMinutes) + "m" : "";
        difference = difference % minutes;

        long elapsedSeconds = difference / seconds;
        timeStamp += elapsedSeconds > 0 && timeStamp.equalsIgnoreCase("") ? Long.toString(elapsedSeconds) + "s" : "";

        return timeStamp;
    }

    // Inflate the view based on the viewType provided.
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_list_item, parent, false);
        return new VH(itemView, mContext);
    }

    // Display data at the specified position
    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        final Ride ride = mRides.get(position);

        User userInfo = mContext.getUserInfo();
        String userId = userInfo.getObjectId();
        if(ride.getDriverId().equalsIgnoreCase(userId)) {
            viewHolder.btnReserve.setVisibility(INVISIBLE);
        } else {
            viewHolder.btnReserve.setVisibility(VISIBLE);
        }

        //user has requested a ride or has been confirmed
        String rideIdOfUser = userInfo.getRideId() != null ? userInfo.getRideId() : null;
        if (rideIdOfUser != null && rideIdOfUser.equalsIgnoreCase(ride.getObjectId())) {
            String status = (userInfo.getStatus().equals(User.Status.WAIT_LIST)) ? "Requested" : "Reserved";
            viewHolder.btnReserve.setText(status);
            viewHolder.btnReserve.setEnabled(false);
        } else {
            viewHolder.btnReserve.setText("Reserve");
            viewHolder.btnReserve.setEnabled(true);
        }

        viewHolder.btnReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                context.openReserveRideDialog(ride);
                mContext.reserveRideRequest(ride);

            }
        });

        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, RideDetailActivity.class);
                ride.getDriver().flush();
                i.putExtra("ride", ride.flush());
                mContext.startActivity(i);
            }
        });

        //why do this?
        viewHolder.rootView.setTag(ride);
        // Populate data into the template view using the data object
        viewHolder.tvFullName.setText(Html.fromHtml(ride.getDriver().getFullName()));
        viewHolder.tvDestination.setText("to: " + ride.getDestination());

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
        if (profileImage != null) {

            Transformation transformation = new RoundedTransformationBuilder()
                    .borderWidthDp(1)
                    .cornerRadiusDp(50)
                    .oval(false)
                    .build();

            Picasso.with(mContext)
                    .load(profileImage.getUrl())
                    .fit()
                    .transform(transformation)
                    .into(viewHolder.ivProfile);
        } else {
            //this is to solve stale image because of recycling views. when we scroll down the already inflated list item is re-used
            //so we see the same image that we see at position 0 for postiion 4.
            viewHolder.ivProfile.setImageResource(R.mipmap.ic_profile_image);
        }
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
        public Button btnReserve;
        public Context mContext;

        public VH(View itemView, final Context context) {
            super(itemView);
            rootView = itemView;
            ivProfile = (ImageView)itemView.findViewById(R.id.ivProfile);
            tvFullName = (TextView)itemView.findViewById(R.id.tvFullName);
            tvSpotsAvailable = (TextView)itemView.findViewById(R.id.tvSpotsAvailable);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
            tvDestination = (TextView)itemView.findViewById(R.id.tvDestination);
            tvStartLoc = (TextView)itemView.findViewById(R.id.tvStartLoc);
            btnReserve = (Button)itemView.findViewById(R.id.btnReserve);
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

}
