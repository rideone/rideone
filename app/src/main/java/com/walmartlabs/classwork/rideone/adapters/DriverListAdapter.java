package com.walmartlabs.classwork.rideone.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by abalak5 on 10/21/15.
 */
public class DriverListAdapter extends ArrayAdapter<User> {
    // View lookup cache
    private static class ViewHolder {
        public ImageView ivProfile;
        public TextView tvFirstName;
        public TextView tvRelativeTimeStamp;
        public TextView tvSpotsAvailable;
    }

    public DriverListAdapter(Context context, List<User> objects) {
        super(context, 0, objects);
    }

    // Translates a particular `Image` given a position
    // into a relevant row within an AdapterView
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        final User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_driver, parent, false);
            viewHolder.ivProfile = (ImageView)convertView.findViewById(R.id.ivProfile);
            viewHolder.tvFirstName = (TextView)convertView.findViewById(R.id.tvFirstName);
            viewHolder.tvSpotsAvailable = (TextView)convertView.findViewById(R.id.tvSpotsAvailable);
            viewHolder.tvRelativeTimeStamp = (TextView)convertView.findViewById(R.id.tvRelativeTimeStamp);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate data into the template view using the data object
        viewHolder.tvFirstName.setText(Html.fromHtml(user.getFirstName()));
        Ride ride = (Ride) user.getParseObject("ride");
        if (ride != null) viewHolder.tvSpotsAvailable.setText(Html.fromHtml(String.valueOf(ride.getSpots())));
        viewHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        try {
            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            String currDateStr = df.format(Calendar.getInstance().getTime());
            Date currDate = df.parse(currDateStr);

/*            Date tweetDate = getTimeStamp(user.getCreatedAt());
            String timeStamp = getRelativeTimeStamp(currDate, tweetDate);
            viewHolder.tvRelativeTimeStamp.setText(timeStamp);*/
/*            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    //.borderWidthDp(0)
                    .cornerRadiusDp(10)
                    .oval(false)
                    .build();

            Picasso.with(getContext())
                    .load(Uri.parse(getUser().getProfileImageUrl()))
                *//*.placeholder(R.drawable.ic_nocover)*//*
                    .transform(transformation)
                    .into(viewHolder.ivProfile);*/
        } catch (ParseException e) {
            e.printStackTrace();
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
