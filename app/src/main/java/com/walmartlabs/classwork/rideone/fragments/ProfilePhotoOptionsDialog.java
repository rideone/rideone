package com.walmartlabs.classwork.rideone.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.activities.RegisterUserActivity;

/**
 * Created by abalak5 on 11/24/15.
 */

public class ProfilePhotoOptionsDialog extends DialogFragment {

    public interface ProfilePhotoUploadListener {
        void onLaunchCamera();
        void selectImageFromGallery();
    }

    public ProfilePhotoOptionsDialog() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ProfilePhotoOptionsDialog newInstance() {
        ProfilePhotoOptionsDialog frag = new ProfilePhotoOptionsDialog();
        Bundle args = new Bundle();
        //args.putBoolean("rideRequested", rideRequested);
       // frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final RegisterUserActivity activity = (RegisterUserActivity) getActivity();
        final View view = inflater.inflate(R.layout.fragment_upload_options, container);
        Button btnTakePhoto = (Button) view.findViewById(R.id.btnTakePhoto);
        Button btnFromLib = (Button) view.findViewById(R.id.btnFromLib);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onLaunchCamera();
                dismiss();
            }
        });

        btnFromLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.selectImageFromGallery();
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
