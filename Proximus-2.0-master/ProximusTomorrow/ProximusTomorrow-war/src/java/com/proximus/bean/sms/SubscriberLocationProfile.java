/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.bean.sms;

import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.GeoFence;

/**
 *
 * @author ronald
 */
public class SubscriberLocationProfile {

    private LocationData locationData;
    private GeoFence geoFence;

    public GeoFence getGeoFence() {
        return geoFence;
    }

    public void setGeoFence(GeoFence geoFence) {
        this.geoFence = geoFence;
    }

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }
}
