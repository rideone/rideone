package com.walmartlabs.classwork.rideone.util;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.walmartlabs.classwork.rideone.models.CustomSerializable;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dmaskev on 11/12/15.
 */
public class ParseUtil {
    public static final int ERR_RECORD_NOT_FOUND = 101;

    public static void saveInBatch(List<? extends ParseObject> models, final SaveCallback callback) {
        final AtomicInteger count = new AtomicInteger(models.size());
        final List<ParseException> exceptions = Collections.synchronizedList(new ArrayList<ParseException>());

        for(final ParseObject model : models) {
            model.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(ParseUtil.class.getSimpleName(), String.format("Failed saving model %s %s", model.getClassName(), model.getObjectId()), e);
                        exceptions.add(e);
                    }

                    int left = count.addAndGet(-1);
                    if(left == 0) {
                        //TODO: concatenate all errors into one
                        if(!exceptions.isEmpty()) {
                            e = exceptions.get(0);
                        }
                        callback.done(e);
                    }

                }
            });
        }

    }



}
