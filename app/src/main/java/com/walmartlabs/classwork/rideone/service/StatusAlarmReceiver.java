package com.walmartlabs.classwork.rideone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mkrish4 on 11/24/15.
 */
public class StatusAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = StatusCheckService.class.getName();
    public int count = 0;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, StatusCheckService.class);
        i.putExtra("foo", count++);
        i.putExtra("user", intent.getSerializableExtra("user"));
        context.startService(i);
    }
}