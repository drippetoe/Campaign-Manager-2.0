/**
 * Copyright (c) 2010-2013 Proximus Mobility LLC
 */
package com.proximus.startup.rulesengine;

import com.proximus.data.Brand;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.sms.Subscriber;
import com.proximus.location.client.LocationFinder;
import com.proximus.location.client.LocationFinderLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Gilberto Gaxiola
 */
public class SubscriberGeoLocationExecutorService {

    private ExecutorService executor;
    private final int SINGLE_BATCH = 10;
    private final int LIMIT_PER_MINUTE = 500;
    private final long MINUTE = 60 * 1000L;
    

    public SubscriberGeoLocationExecutorService(int numThreads) {
         executor = Executors.newFixedThreadPool(numThreads);
    }
    

    
    
     public void startJob(LocationFinderLocal finder, List<String> numbers, double geoFenceX, double geoFenceY, LocationDataManagerLocal locationMgr, SubscriberManagerLocal subscriberMgr, Brand brand, MobileOfferSettings mobileOfferSettings, List<GeoFence> geofences) throws InterruptedException {
        if (numbers == null || numbers.isEmpty()) {
            return;
        }
        int currIndex = 0;

        long currTime;
        long time = System.currentTimeMillis();
        int items = 0;
        while (currIndex + SINGLE_BATCH < numbers.size()) {
            currTime = System.currentTimeMillis() - time;
            //Checking if we have process the limit per minute before we can run another thread
            if (items > LIMIT_PER_MINUTE - SINGLE_BATCH) {
                //Checking if we already passed the Minute
                if (currTime > MINUTE) {
                    items = 0;
                    time = System.currentTimeMillis();
                } else {
                    Thread.sleep((MINUTE - currTime));
                }
            }
            
            
            
            Runnable worker = new SubscriberGeoLocationWorkerThread(finder, numbers.subList(currIndex, currIndex + SINGLE_BATCH), geoFenceX,geoFenceY,locationMgr, subscriberMgr,brand,mobileOfferSettings,geofences);
            executor.execute(worker);
            currIndex += SINGLE_BATCH;
            items += SINGLE_BATCH;

        }

        if (currIndex < numbers.size()) {
             currTime = System.currentTimeMillis() - time;
            //Checking if we have process the limit per minute before we can run another thread
            if (items > LIMIT_PER_MINUTE - SINGLE_BATCH) {
                //Checking if we already passed the Minute
                if (currTime > MINUTE) {
                    items = 0;
                } else {
                    Thread.sleep((MINUTE - currTime));
                }
            }
            List<String> subList = numbers.subList(currIndex, numbers.size());
            Runnable worker = new SubscriberGeoLocationWorkerThread(finder, subList, geoFenceX,geoFenceY,locationMgr, subscriberMgr,brand,mobileOfferSettings,geofences);
            executor.execute(worker);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException ex) {
        }
    }
    
    
    
}
