package com.walmartlabs.classwork.rideone.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.adapters.RideListAdapter;
import com.walmartlabs.classwork.rideone.models.Filter;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.walmartlabs.classwork.rideone.models.Ride.COLUMN_AVAILABLE;
import static com.walmartlabs.classwork.rideone.models.User.COLUMN_ID;
import static com.walmartlabs.classwork.rideone.util.ParseUtil.ERR_RECORD_NOT_FOUND;

/**
 * Created by abalak5 on 11/8/15.
 */
public class RideListFragment extends Fragment {
    public RideListAdapter aRides;
    public List<Ride> rides;
    private ListView lvRides;
    ProgressBar progressBarFooter;

    private SwipeRefreshLayout swipeContainer;

    public static RideListFragment newInstance() {
        RideListFragment fragment = new RideListFragment();
       // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rides = new ArrayList<Ride>();
        aRides = new RideListAdapter(getActivity(), rides);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_list, container, false);
        lvRides = (ListView) view.findViewById(R.id.lvDrivers);
//        lvRides.setOnScrollListener(new EndlessScrollListener() {
//            @Override
//            public void onLoadMore(int totalItemCount) {
//
//            }
//        });

        lvRides.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //    Intent i = new Intent(getActivity(), DetailedViewActivity.class);
                //    startActivity(i);
            }
        });

      //  swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
/*        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sinceId = 1;
                maxId = 0;
                clear();
                fetchAndPopulateTimeline();
                swipeContainer.setRefreshing(false);
            }
        });*/

        // Inflate the footer
/*        View footer = getLayoutInflater(savedInstanceState).inflate(
                R.layout.footer_progress, null);
        // Find the progressbar within footer
        progressBarFooter = (ProgressBar)
                footer.findViewById(R.id.pbFooterLoading);
        // Add footer to ListView before setting adapter
        lvTweets.addFooterView(footer);*/
        lvRides.setAdapter(aRides);
        fetchAndPopulateRideList();

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchAndPopulateRideList();
            }
        });
//        getDummyTimeline();
        return view;
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void clear() {
        aRides.clear();
    }

//    public void getDummyTimeline() {
//        Ride ride = new Ride();
//        ride.setDate(Utils.getNextHour());
//        ride.setAvailable(true);
//        User driver = new User();
//        driver.setFirstName("Driver1");
//        ride.setDriver(driver);
//
//        ride.setSpots(2);
//        aRides.add(ride);
//    }

    public void fetchAndPopulateRideList() {
        ParseQuery<Ride> query = ParseQuery.getQuery(Ride.class);
        query.whereEqualTo(COLUMN_AVAILABLE, true);
       // query.whereGreaterThan("spotsLeft", 0);

        if(Filter.isFilterOn()) {
            query.whereGreaterThanOrEqualTo("spotsLeft", Filter.getSpots());
            query.whereEqualTo("start_loc", Filter.getStart());
            query.whereEqualTo("destination", Filter.getDestination());
        }

        //query.include("ride");
        query.findInBackground(new FindCallback<Ride>() {
            public void done(final List<Ride> rideList, ParseException e) {
                if (e != null && e.getCode() != ERR_RECORD_NOT_FOUND) {
                    Log.e(RideListFragment.class.getSimpleName(), "Failed to retrieve rideList ", e);
                    swipeContainer.setRefreshing(false);

                    //TODO: show Toast alert for network error
                }

                if (rideList == null || rideList.isEmpty()) {
                    aRides.clear();
                    swipeContainer.setRefreshing(false);

                    return;
                }

                Function<Ride, String> driverIdFromRide = new Function<Ride, String>() {
                    @Override
                    public String apply(Ride input) {
                        return input.getDriverId();
                    }
                };
                final List<String> driverIds = Lists.transform(rideList, driverIdFromRide);
                final Map<String, Ride> driverIdToRideMap = Maps.uniqueIndex(rideList, driverIdFromRide);

                ParseQuery.getQuery(User.class).whereContainedIn(COLUMN_ID, driverIds).findInBackground(new FindCallback<User>() {
                    @Override
                    public void done(List<User> driverList, ParseException e) {
                        if (e != null && e.getCode() != ERR_RECORD_NOT_FOUND) {
                            Log.e(RideListFragment.class.getSimpleName(), "Failed to retrieve driverList ", e);
                            //TODO: show Toast alert for network error
                            swipeContainer.setRefreshing(false);

                            return;
                        }

                        if (driverList == null || driverList.isEmpty()) {
                            String msg = "No drivers found for " + driverIds;
                            Log.e(RideListFragment.class.getSimpleName(), msg, new IllegalStateException(msg));
                            swipeContainer.setRefreshing(false);

                            return;
                        }

                        for (User driver : driverList) {
                            Ride ride = driverIdToRideMap.get(driver.getObjectId());
                            if (ride == null) {
                                String msg = "Ride is null for given driver " + driver.getObjectId();
                                Log.e(RideListFragment.class.getSimpleName(), msg, new IllegalStateException(msg));
                                continue;
                            }

                            ride.setDriver(driver);
                        }

                        aRides.clear();
                        aRides.addAll(rideList);
                        swipeContainer.setRefreshing(false);
                    }
                });



            }
        });
    }

    public JsonHttpResponseHandler getHandler() {
        JsonHttpResponseHandler handler =  new JsonHttpResponseHandler();
        return handler;
    }

    public void showProgressBar() {
        progressBarFooter.setVisibility(View.VISIBLE);
    }

    // Hide progress
    public void hideProgressBar() {
        progressBarFooter.setVisibility(View.GONE);
    }
}
