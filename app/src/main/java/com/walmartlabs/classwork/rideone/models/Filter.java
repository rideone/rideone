package com.walmartlabs.classwork.rideone.models;

/**
 * Created by abalak5 on 10/18/15.
 */
public class Filter {
    private static int spots;
    private static String start;
    private static String destination;
    private static String timeRange;
    private static boolean filterOn;

    public static String getTimeRange() {
        return timeRange;
    }

    public static void setTimeRange(String timeRange) {
        Filter.timeRange = timeRange;
    }

    public static int getSpots() {
        return spots;
    }

    public static void setSpots(int spots) {
        Filter.spots = spots;
    }

    public static String getStart() {
        return start;
    }

    public static void setStart(String start) {
        Filter.start = start;
    }

    public static String getDestination() {
        return destination;
    }

    public static void setDestination(String destination) {
        Filter.destination = destination;
    }

    public static boolean isFilterOn() {
        return filterOn;
    }

    public static void setFilterOn(boolean filterOn) {
        Filter.filterOn = filterOn;
    }

}
