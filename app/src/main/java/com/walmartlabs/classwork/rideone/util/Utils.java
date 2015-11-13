package com.walmartlabs.classwork.rideone.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.parse.ParseObject;
import com.walmartlabs.classwork.rideone.models.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

public class Utils {

    public static final String DEFAULT_TIMEZONE = "America/Los_Angeles";

    //Check for internet connection
    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static void showNoInternet(Context context) {
        // Post a short message that app is operating in offline mode
        Toast toast = Toast.makeText(context, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public static boolean checkCallPermission(Context context) {
        int result = context.checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE);
        return result == PERMISSION_GRANTED;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static Date getNextHour() {
        Calendar c = Calendar.getInstance();
        c.set(MINUTE, 0);
        c.add(HOUR_OF_DAY, 1);
        return c.getTime();
    }

    public static TimeZone getCurrentTimeZone() {
        return TimeZone.getTimeZone(DEFAULT_TIMEZONE);
    }

    public static int[] getLocalHourAndMinute(Date time) {
        Calendar c = Calendar.getInstance(Utils.getCurrentTimeZone());
        c.setTime(time);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        return new int[] {hour, min};
    }

    /**
     *
     * @param timeString string formatted as "hour:minutes"
     * @return
     */
    public static Date parseHourAndMinute(String timeString) {
        if(isNullOrEmpty(timeString)) {
            return null;
        }

        String[] timeArray = timeString.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        int minute = Integer.parseInt(timeArray[1]);

        Calendar c = Calendar.getInstance(Utils.getCurrentTimeZone());
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return c.getTime();
    }

    public static String formatDuration(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }


    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

    public static <T> List<T> joinLists(List<T>... lists) {

        List<T> res = new ArrayList<>();
        for(List<T> list : lists) {
            res.addAll(list);
        }

        return res;
    }

    public static List<ParseObject> joinModelLists(List<? extends ParseObject>... lists) {

        List<ParseObject> res = new ArrayList<>();
        for(List<? extends ParseObject> list : lists) {
            res.addAll(list);
        }

        return res;
    }
}
