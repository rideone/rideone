package com.walmartlabs.classwork.rideone.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.activities.HomeActivity;
import com.walmartlabs.classwork.rideone.models.Ride;

/**
 * Created by abalak5 on 10/23/15.
 */
public class ReserveRideDialog extends DialogFragment {

    private static Ride currentRide;
    public interface ReserveRideListener {
        void reserveRideRequest(Ride ride);
    }


    public ReserveRideDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ReserveRideDialog newInstance(Ride ride, boolean rideRequested) {
        ReserveRideDialog frag = new ReserveRideDialog();
        Bundle args = new Bundle();
        args.putBoolean("rideRequested", rideRequested);
        frag.setArguments(args);
        currentRide = ride;
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_reserve_ride, container);

        final Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        final Button btnOk = (Button) view.findViewById(R.id.btnOK);
        TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);

        if(getArguments().getBoolean("rideRequested")) {
            tvMessage.setText("You have already reserved a ride. Do you wish to cancel and re-book this ride?");
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity activity = (HomeActivity) getActivity();
                activity.reserveRideRequest(currentRide);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Fetch arguments from bundle and set title
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

}
