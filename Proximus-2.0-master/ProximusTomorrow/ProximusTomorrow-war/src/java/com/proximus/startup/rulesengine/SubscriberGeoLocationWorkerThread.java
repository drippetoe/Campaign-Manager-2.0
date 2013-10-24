/**
 * Copyright (c) 2010-2013 Proximus Mobility LLC
 */
package com.proximus.startup.rulesengine;

import com.proximus.data.Brand;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.location.client.LocationFinderLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.util.List;

/**
 *
 * @author Gilberto Gaxiola
 */
public class SubscriberGeoLocationWorkerThread implements Runnable {

    LocationFinderLocal finder;
    List<String> numbers;
    double geoFenceX;
    double geoFenceY;
    LocationDataManagerLocal locationMgr;
    SubscriberManagerLocal subscriberMgr;
    Brand brand;
    MobileOfferSettings mobileOfferSettings;
    List<GeoFence> geofences;

    public SubscriberGeoLocationWorkerThread(LocationFinderLocal finder, List<String> numbers, double geoFenceX, double geoFenceY, LocationDataManagerLocal locationMgr, SubscriberManagerLocal subscriberMgr, Brand brand, MobileOfferSettings mobileOfferSettings, List<GeoFence> geofences) {
        this.finder = finder;
        this.numbers = numbers;
        this.geoFenceX = geoFenceX;
        this.geoFenceY = geoFenceY;
        this.locationMgr = locationMgr;
        this.subscriberMgr = subscriberMgr;
        this.brand = brand;
        this.mobileOfferSettings = mobileOfferSettings;
        this.geofences = geofences;
        
    }

    @Override
    public void run() {
        finder.synchFind(numbers, 0, 0, locationMgr, subscriberMgr, brand, mobileOfferSettings, geofences);
    }
}
