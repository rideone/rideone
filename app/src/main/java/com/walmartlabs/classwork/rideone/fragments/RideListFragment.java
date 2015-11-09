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

import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.adapters.RidesAdapter;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abalak5 on 11/8/15.
 */
public class RideListFragment  extends Fragment {

    private RidesAdapter aRides;
    private List<User> users;
    private ListView lvRides;
    protected long maxId;
    protected long sinceId = 1;
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
        sinceId = 1;
        maxId = 0;
        users = new ArrayList<User>();
        aRides = new RidesAdapter(getActivity(), users);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rides_list, container, false);
        lvRides = (ListView) view.findViewById(R.id.lvRides);
        lvRides.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int totalItemCount) {

            }
        });

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
        fetchAndPopulateTimeline();
        return view;
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void addAll(List<User> tweets) {
        aRides.addAll(tweets);
    }

    public void clear() {
        aRides.clear();
        aRides.notifyDataSetChanged();
    }

    protected void fetchAndPopulateTimeline() {
        ParseQuery<User> query = ParseQuery.getQuery("User");
        query.findInBackground(new FindCallback<User>() {
            public void done(List<User> list, ParseException e) {
                if (e == null) {
                    users.addAll(list);
                    aRides.notifyDataSetChanged();
                    Log.d("score", "Retrieved " + users.toString());
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
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
