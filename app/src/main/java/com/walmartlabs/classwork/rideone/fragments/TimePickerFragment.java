package com.walmartlabs.classwork.rideone.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.walmartlabs.classwork.rideone.util.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by dmaskev on 11/8/15.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String ARG_TIME_HOUR = "time_hour";
    public static final String ARG_TIME_MIN = "time_min";

    private TimePickerDialog.OnTimeSetListener onTimeListener;

    public static TimePickerFragment createInstance(Integer hour, Integer minute) {
        TimePickerFragment fragment = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME_HOUR, hour);
        args.putSerializable(ARG_TIME_MIN, minute);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.onTimeListener = (TimePickerDialog.OnTimeSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TimePickerDialog.OnTimeSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Integer hour = (Integer) getArguments().getSerializable(ARG_TIME_HOUR);
        Integer min = (Integer) getArguments().getSerializable(ARG_TIME_MIN);

        return new TimePickerDialog(getActivity(), this.onTimeListener, hour, min,
                DateFormat.is24HourFormat(getActivity()));
    }

//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        // Do something with the time chosen by the user
//    }
}