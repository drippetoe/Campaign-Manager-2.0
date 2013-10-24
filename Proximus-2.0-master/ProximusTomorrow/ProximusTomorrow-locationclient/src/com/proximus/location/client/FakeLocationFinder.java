package com.proximus.location.client;


import com.proximus.data.Brand;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.sms.Subscriber;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;

/**
 * Generate a random location for each number, hopefully in or near Georgia
 *
 * @author dshaw
 */
public class FakeLocationFinder implements LocationFinderLocal  {

    Random rand = new Random(System.currentTimeMillis());
    // Georgia is 32.9605° N, 83.1132° W
    final double MIN_LAT = 30.62;
    final double MAX_LAT = 34.78;
    final double MIN_LON = -81;
    final double MAX_LON = -85;
    //Making easier the random value
    final int MIN_LAT_INT = 30;
    final int MAX_LAT_INT = 35;
    final int MIN_LON_INT = 81;
    final int MAX_LON_INT = 85;
    
    private static final Logger logger = Logger.getLogger(FakeLocationFinder.class.getName());

    @Override
    public void synchFind(List<String> numbers, double geoFenceX, double geoFenceY, LocationDataManagerLocal locationMgr, SubscriberManagerLocal subscriberMgr, Brand brand, MobileOfferSettings mobileOfferSettings, List<GeoFence> geofences) {
        List<LocationData> fakeResult = new ArrayList<LocationData>();

        for (String number : numbers) {
            LocationData data = locationMgr.findLastBySubscriber(subscriberMgr.findByMsisdn(number));
            if (data != null) {
                LocationData fakeData = new LocationData();
                fakeData.setLatitude(data.getLatitude());
                fakeData.setLongitude(data.getLongitude());
                fakeData.setMsisdn(number);
                fakeData.setEventDate(new Date());
                fakeData.setStatus(LocationData.STATUS_FOUND_FAKE);
                fakeResult.add(fakeData);
            } else {
                double randomLat = rand.nextInt(MAX_LAT_INT - MIN_LAT_INT + 1) + MIN_LAT + rand.nextFloat();
                int randVal = rand.nextInt(MAX_LON_INT - MIN_LON_INT + 1);
                double randomLon = -1 * (randVal + MAX_LON_INT + rand.nextFloat());

                LocationData fakeData = new LocationData();
                fakeData.setLatitude(randomLat);
                fakeData.setLongitude(randomLon);
                fakeData.setEventDate(new Date());
                fakeData.setMsisdn(number);
                fakeData.setStatus(LocationData.STATUS_FOUND_FAKE);

                fakeResult.add(fakeData);
            }
        }
        saveToDatabase(fakeResult, subscriberMgr, locationMgr, brand, mobileOfferSettings,geofences);
    }
    
    
    
    /**
     * Making saves every 10 responses so we don't hit a bottleneck
     * @param results
     * @param subscriberMgr
     * @param locMgr
     * @param brand
     * @param mobileOfferSettings 
     */
    private void saveToDatabase(List<LocationData> results, SubscriberManagerLocal subscriberMgr, LocationDataManagerLocal locMgr, Brand brand, MobileOfferSettings mobileOfferSettings, List<GeoFence> geofences) {
        for (LocationData locationData : results) {
            if (locationData.getMsisdn() != null) {
                Subscriber subscriber = subscriberMgr.findByMsisdn(locationData.getMsisdn());
                if (subscriber == null) {
                    //This is legacy location data 
                    logger.error("We don't know why number exists in location Data and not in Subscribers | " + locationData.getMsisdn());
                } else {
                    locationData.setBrand(brand);
                    locMgr.create(locationData);
                    //Update Subscriber's Next Lookup if next_lookup_request is null or the newest locationData eventDate() is more recent that the subcriber's next_ping_request
                    //To avoid doing more than one location by the min_time_between_lookups
                    if (subscriber.getNextLookupRequest() == null || locationData.getEventDate().getTime() >= subscriber.getNextLookupRequest().getTime()) {
                        subscriber.setNextLookupRequest(new Date(System.currentTimeMillis() + mobileOfferSettings.getMinLookupWaitTime()));
                        logger.info("Located subscriber " + subscriber.getCarrier() + " " + subscriber.getMsisdn() + " won't lookup until " + DateUtil.formatTimestampForWeb(subscriber.getNextLookupRequest()));
                        subscriberMgr.update(subscriber);
                    }
                }
            } else {
                logger.fatal("location data with null MSISDN returned");
            }
        }
    }

    @Override
    public double distanceCalc(double latitudeOne, double longitudeOne, double latitudeTwo, double longitudeTwo) {
        return new LocationFinder().distanceCalc(latitudeOne, longitudeOne, latitudeTwo, longitudeTwo);
    }
}
