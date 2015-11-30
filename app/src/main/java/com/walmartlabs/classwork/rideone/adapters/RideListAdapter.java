package com.walmartlabs.classwork.rideone.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.RoundedTransformationBuilder;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.activities.HomeActivity;
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
public class RideListAdapter extends ArrayAdapter<Ride> {
    // View lookup cache
    private static class ViewHolder {
        public ImageView ivProfile;
        public TextView tvFullName;
        public TextView tvTime;
        public TextView tvSpotsAvailable;
        public TextView tvStartLoc;
        public TextView tvDestination;
        public Button btnReserve;
    }

    private HomeActivity context;

    public RideListAdapter(Context context, List<Ride> rides) {
        super(context, 0, rides);
        this.context = (HomeActivity) context;
    }

    // Translates a particular `Image` given a position
    // into a relevant row within an AdapterView
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final Ride ride = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ride_list_item, parent, false);
            viewHolder.ivProfile = (ImageView)convertView.findViewById(R.id.ivProfile);
            viewHolder.tvFullName = (TextView)convertView.findViewById(R.id.tvFullName);
            viewHolder.tvSpotsAvailable = (TextView)convertView.findViewById(R.id.tvSpotsAvailable);
            viewHolder.tvTime = (TextView)convertView.findViewById(R.id.tvTime);
            viewHolder.tvDestination = (TextView)convertView.findViewById(R.id.tvDestination);
            viewHolder.tvStartLoc = (TextView)convertView.findViewById(R.id.tvStartLoc);
            viewHolder.btnReserve = (Button)convertView.findViewById(R.id.btnReserve);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User userInfo = context.getUserInfo();
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
                context.reserveRideRequest(ride);

            }
        });

        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Populate data into the template view using the data object
        viewHolder.tvFullName.setText(Html.fromHtml(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName()));
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
                    .borderColor(Color.BLACK)
                    .cornerRadiusDp(10)
                    .oval(false)
                    .build();

            Picasso.with(getContext())
                    .load(profileImage.getUrl())
                    .transform(transformation)
                    .into(viewHolder.ivProfile);
            Log.d("position", Integer.toString(position));
        } else {
            //this is to solve stale image because of recycling views. when we scroll down the already inflated list item is re-used
            //so we see the same image that we see at position 0 for postiion 4.
            viewHolder.ivProfile.setImageResource(R.mipmap.ic_launcher);
            Log.d("position1", Integer.toString(position));
        }

        // Return the completed view to render on screen
        return convertView;
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
}
