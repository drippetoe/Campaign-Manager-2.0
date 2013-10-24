/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.location.client;

import com.proximus.data.Brand;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.util.List;

/**
 *
 * @author dshaw
 */
public interface LocationFinderLocal {
    public void synchFind(List<String> numbers, double geoFenceX, double geoFenceY, LocationDataManagerLocal locationMgr, SubscriberManagerLocal subscriberMgr, Brand brand, MobileOfferSettings mobileOfferSettings, List<GeoFence> geofences);
    public double distanceCalc(double latitudeOne, double longitudeOne, double latitudeTwo, double longitudeTwo);
}
