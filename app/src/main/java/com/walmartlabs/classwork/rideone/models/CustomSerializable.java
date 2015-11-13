package com.walmartlabs.classwork.rideone.models;

/**
 * Created by dmaskev on 11/12/15.
 */


import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Map;

public interface CustomSerializable<T extends ParseObject> extends Serializable {
    T flush();
    T rebuild();
    Map<String, Object> getFields();

}
