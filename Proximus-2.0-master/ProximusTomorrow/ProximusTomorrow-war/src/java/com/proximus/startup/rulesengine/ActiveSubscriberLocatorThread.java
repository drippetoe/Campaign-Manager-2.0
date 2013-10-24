package com.proximus.startup.rulesengine;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.GeoFence;
import com.proximus.data.sms.MobileOfferSendLog;
import com.proximus.data.sms.MobileOfferSettings;
import com.proximus.data.sms.Subscriber;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.sms.GeoFenceManagerLocal;
import com.proximus.manager.sms.MobileOfferSendLogManagerLocal;
import com.proximus.manager.sms.MobileOfferSettingsManagerLocal;
import com.proximus.manager.sms.SubscriberManagerLocal;
import com.proximus.util.server.GeoFenceResponse;
import com.proximus.util.server.GeoUtil;
import java.util.*;
import org.apache.log4j.Logger;
import org.primefaces.model.map.LatLng;

/**
 *
 * @author dshaw
 */
public class ActiveSubscriberLocatorThread extends AbstractRulesEngineThread {

    static final Logger logger = Logger.getLogger(ActiveSubscriberLocatorThread.class.getName());
    private List<Subscriber> eligibleSubscribers;
    MobileOfferSendLogManagerLocal mobileOfferSendLogMgr;
    LocationDataManagerLocal locationDataMgr;
    SubscriberManagerLocal subscriberMgr;
    GeoFenceManagerLocal geoFenceMgr;
    MobileOfferSettingsManagerLocal mobileOfferSettingsMgr;
    //Rate of Change between the Distance Upper Bounds
    //public static final double TIME_PENALTY_IN_MS = (MobileOfferSettings.MAX_PING_INTERVAL - MobileOfferSettings.MIN_PING_INTERVAL) / (GeoUtil.MAX_UPPERBOUND_MILES - GeoUtil.MIN_UPPERBOUND_MILES);

    public ActiveSubscriberLocatorThread(Brand brand, SubscriberManagerLocal subscriberMgr, LocationDataManagerLocal locationDataMgr, MobileOfferSettingsManagerLocal mobileOfferSettingsMgr, MobileOfferSendLogManagerLocal mobileOfferSendLogMgr, GeoFenceManagerLocal geoFenceMgr) {

        super(brand);
        this.subscriberMgr = subscriberMgr;
        this.mobileOfferSendLogMgr = mobileOfferSendLogMgr;
        this.locationDataMgr = locationDataMgr;
        this.mobileOfferSettingsMgr = mobileOfferSettingsMgr;
        this.geoFenceMgr = geoFenceMgr;
    }

    @Override
    public void run() {

        eligibleSubscribers = new ArrayList<Subscriber>();
        logger.debug("ActiveSubscriberLocatorThread: Running");

        try {
            MobileOfferSettings mobileOfferSettings = mobileOfferSettingsMgr.findByBrand(brand);
            List<Company> companies = brand.getCompanies();
            List<GeoFence> allGeoFences = new ArrayList<GeoFence>();
            List<Subscriber> allSubscribers = subscriberMgr.findAllByBrandAndOptIn(brand);
            

            for (Company company : companies) {
                List<GeoFence> fences = geoFenceMgr.getGeoFencesByCompany(company);
                
                if (fences != null) {
                    allGeoFences.addAll(fences);
                }
                
            }
            

            /**
             * `
             * Start with all subscribers that are opted in Filter out those
             * with:
             *
             * 1) Those who have been sent more than "MAX SENT" this calendar
             * month 2) Those whose "LAST SENT" is newer than the min time 3)
             * Those whose distance from any geofence is more than the max
             * distance based on time
             */
            if (allSubscribers == null || allSubscribers.isEmpty()) {
                logger.info("No subscribers opted in for Brand " + brand.getName());
                this.setComplete(true);
                return;
            }

            long currTime = System.currentTimeMillis();
            for (Subscriber subscriber : allSubscribers) {

                //logger.log(Level.INFO, "Checking if subscriber: " + subscriber.getMsisdn() + " is eligible or not");
                if (!eligibleToSendMessage(subscriber, mobileOfferSettings)) {
                    //logger.log(Level.INFO, "NOT ELIGIBLE!");
                    continue;
                }

                LocationData lastLoc = locationDataMgr.findLastBySubscriber(subscriber);
                if (lastLoc == null) {
                    continue;
                }
                if (locationDataHasExpired(lastLoc, mobileOfferSettings, currTime)) {
                    continue;
                }

                LatLng location = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
                GeoFenceResponse response = GeoUtil.CalculateClosestGeoFence(mobileOfferSettings, location, allGeoFences);
                subscriber.setDistanceAwayFromGeoFence(response.getDistance());
                subscriber.setCurrentClosestGeoFenceId(response.getGeoFence().getId());
                lastLoc.setCurrentClosestGeoFenceId(response.getGeoFence().getId());
                lastLoc.setDistanceAwayFromGeoFence(response.getDistance());
                locationDataMgr.update(lastLoc);

                //Adding sensible default
                if (subscriber.getNextLookupRequest() == null) {
                    subscriber.setNextLookupRequest(new Date(currTime));
                }

                //CHANGING LOOKUP REQUEST LOGIC

                if (response.isIsWithinFence()) {
                    
                    logger.debug("Subscriber " + subscriber.getMsisdn() + " is within geofence " + subscriber.getCurrentClosestGeoFenceId() + " and eligible to receive a message");
                    eligibleSubscribers.add(subscriber);
                } else {
                    logger.debug("Subscriber " + subscriber.getMsisdn() + " is outside its closest geofence " + subscriber.getCurrentClosestGeoFenceId() + " and NOT eligible to receive a message");
                    Date d;
                    long timeOff;
                    if (subscriber.getNextLookupRequest().getTime() <= currTime) {
                        if (response.getDistance() >= mobileOfferSettings.getMaxDistance()) {
                            timeOff = calculateTimeToWait(mobileOfferSettings, mobileOfferSettings.getMaxDistance());
                        } else {
                            timeOff = calculateTimeToWait(mobileOfferSettings, response.getDistance());
                            timeOff = Math.max(timeOff, mobileOfferSettings.getMinLookupWaitTime());
                        }
                        d = new Date(System.currentTimeMillis() + timeOff);

                        subscriber.setNextLookupRequest(d);
                    }
                }
                subscriberMgr.update(subscriber); // update either way
            }
        } catch (Exception err) {
            logger.fatal("Uncaught error in ActiveSubscriberLocatorThread", err);
        }
        this.setComplete(true);
    }

    public List<Subscriber> getEligibleSubscribers() {
        return eligibleSubscribers;
    }

    public static Long calculateTimeToWait(MobileOfferSettings mobileOfferSettings, double distance) {


        if (distance >= mobileOfferSettings.getMaxDistance()) {
            return mobileOfferSettings.getMaxLookupWaitTime();
        } else {
            //I.E Linear Equation y = mx + b
            //m = slope = TIME_PENALTY_IN_MS
            //y = MIN_PING_INTERVAL
            Double x = distance;

            Double timePenaltySlope = (mobileOfferSettings.getMaxLookupWaitTime() - mobileOfferSettings.getMinLookupWaitTime()) / (mobileOfferSettings.getMaxDistance() * 1.0);

            Double result = (timePenaltySlope * x) + mobileOfferSettings.getMinLookupWaitTime();
            return result.longValue();
        }

    }

    /**
     * If check returns true it means THIS SUBSCRIBER IS ELIGIBLE
     *
     * This method evaluates Rules 1 and 2 of the filtering system
     *
     * RULE #1: Max messages Per Month RULE #2: Min Time Between Messages
     *
     * @param s
     * @return
     */
    private boolean eligibleToSendMessage(Subscriber subscriber, MobileOfferSettings mobileOfferSettings) {

        List<MobileOfferSendLog> sentThisMonth = mobileOfferSendLogMgr.findByMonthAndSubscriber(runStart, subscriber);
        MobileOfferSendLog lastSent = mobileOfferSendLogMgr.findMostRecentBySubscriber(subscriber);

        // RULE #1: Max messages Per Month
        if (sentThisMonth != null && !sentThisMonth.isEmpty()) {
            if (sentThisMonth.size() >= mobileOfferSettings.getMaxMessagesPerSubscriberPerMonth()) {
                logger.debug("Subscriber " + subscriber + " has already received the max messages this month");
                return false;
            }
        }

        // RULE #2: Min Time Between Messages
        if (lastSent != null) {
            long nextEligibleTime = lastSent.getEventDate().getTime() + mobileOfferSettings.getMinTimeBetweenMessages();
            if (nextEligibleTime > runStart.getTime()) {
                long timeOffDays = (mobileOfferSettings.getMinTimeBetweenMessages()) / (1000 * 60 * 60 * 24);
                logger.debug("Subscriber received a message within the last " + timeOffDays + " days and is ineligible");
                return false;
            }
        }

        return true;
    }

    private boolean locationDataHasExpired(LocationData lastLoc, MobileOfferSettings mobileOfferSettings, long currTime) {
        long minLookupWaitTime = mobileOfferSettings.getMinLookupWaitTime() * mobileOfferSettings.getExpirationMultiplier();
        if (lastLoc != null) {
            long timeBetween = currTime - lastLoc.getEventDate().getTime();
            if (timeBetween < minLookupWaitTime) {
                return false;
            }
            logger.debug("Location data has expired for subscriber " + lastLoc.getMsisdn() + " (" + timeBetween + " < " + minLookupWaitTime + ")");
        }
        
        return true;
    }

    public static void main(String[] args) {
        MobileOfferSettings settings = new MobileOfferSettings();
        settings.setMinLookupWaitTime(15 * 60 * 1000L);  // 15 mins
        settings.setMaxLookupWaitTime(24 * 60 * 60 * 1000L); // 8 hours
        settings.setMaxDistance(300L); // 300 miles
        settings.setExpirationMultiplier(10L);
        double[] distances = {0.0, 20.0, 60.0, 100.0, 150.0, 170.0, 250.0, 300.0, 500.0};
        for (double d : distances) {
            Long result = calculateTimeToWait(settings, d);
            System.out.println("Wait time at distance " + d + " (min): " + result / (60 * 1000.0));

            Long timeOff = 0L;
            if (d >= settings.getMaxDistance()) {
                timeOff = calculateTimeToWait(settings, settings.getMaxDistance());
            } else {
                timeOff = calculateTimeToWait(settings, d);
                timeOff = Math.max(timeOff, settings.getMinLookupWaitTime());
            }
            Date da = new Date(System.currentTimeMillis() + timeOff);
            System.out.println("Next lookup time at distance " + d + " is " + da);
        }
    }
}