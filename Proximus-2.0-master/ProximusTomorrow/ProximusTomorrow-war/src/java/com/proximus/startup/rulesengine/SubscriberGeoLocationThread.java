package com.proximus.startup.rulesengine;

import com.proximus.data.*;
import com.proximus.data.sms.*;
import com.proximus.data.util.DateUtil;
import com.proximus.location.client.FakeLocationFinder;
import com.proximus.location.client.LocationFinder;
import com.proximus.location.client.LocationFinderLocal;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.ZipcodeToTimezoneManagerLocal;
import com.proximus.manager.sms.*;
import com.proximus.util.TimeConstants;
import com.proximus.util.TimeZoneUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

/**
 * This thread simply locates subscribers whose location is out of date and logs
 * their location
 *
 * @author dshaw
 */
public class SubscriberGeoLocationThread extends Thread {

    private static class SubscriberGeoLocationThreadHolder {

        public static final SubscriberGeoLocationThread instance = new SubscriberGeoLocationThread();
    }

    public static SubscriberGeoLocationThread getInstance() {
        return SubscriberGeoLocationThreadHolder.instance;
    }
    static final Integer millisecondsBetweenRuns = 5 * 60 * 1000;  // 5 minutes
    BrandManagerLocal brandMgr;
    CompanyManagerLocal companyMgr;
    GeoLocationSettingsManagerLocal geoLocationSettingsMgr;
    MobileOfferSettingsManagerLocal mobileOfferSettingsMgr;
    MobileOfferSendLogManagerLocal mobileOfferSendLogMgr;
    ZipcodeToTimezoneManagerLocal zipcodeToTimezoneMgr;
    SubscriberManagerLocal subscriberMgr;
    LocationDataManagerLocal locMgr;
    MobileOfferManagerLocal mobileOfferMgr;
    MobileOfferBalanceManagerLocal mobileOfferBalanceMgr;
    LocaleManagerLocal localeMgr;
    GeoFenceManagerLocal geoFenceMgr;
    private static final Logger logger = Logger.getLogger(SubscriberGeoLocationThread.class.getName());
    private static final long SLEEP_TIME = 1000 * 60 * 3; //Should be  5 minutes
    Date lastRun;

    public SubscriberGeoLocationThread() {
    }

    /**
     * This function returns the "linear" day of the month, normalizing all
     * months to 31 days for the purpse of calculating "free" lookups left. If
     * you are in a month with less than 31 days, at some point you have extra
     * lookups per subscriber. This function helps allocate them on the proper
     * day of the month
     *
     * @param date
     * @return
     */
    private int getLinearDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int today = calendar.get(Calendar.DAY_OF_MONTH);

        switch (daysInMonth) {
            case 31:
                return today;
            case 28:
                if (today > 17) {
                    return today + 2;
                } else if (today > 8) {
                    return today + 1;
                }
            default:
                if (today > 14) {
                    return today + 1;
                } else {
                    return today;
                }
        }
    }

    @Override
    public void run() {

        LocationFinderLocal finder;

        lastRun = new Date(0L);

        // run forever
        while (true) {

            if (System.currentTimeMillis() - lastRun.getTime() > millisecondsBetweenRuns) {
                lastRun = new Date();
                logger.info("SubscriberGeoLocationThread: Last Run is: " + lastRun);
                try {
                    List<Brand> brands = brandMgr.findAll();
                    for (Brand brand : brands) {
                        List<Company> companies = companyMgr.findCompaniesWithLicenseInBrand(License.LICENSE_GEOFENCE, brand);
                        if (companies == null || companies.isEmpty()) {
                            continue; //This Brand doesn't have Companies with GeoFence Licensing;
                        }
                        MobileOfferSettings mobileOfferSettings = mobileOfferSettingsMgr.findByBrand(brand);
                        GeoLocationSettings geoLocationSettings = geoLocationSettingsMgr.findByBrand(brand);
                        ZipcodeToTimezone tz;
                        if (mobileOfferSettings == null) {
                            logger.warn("Brand " + brand.getName() + ": Mobile Offer Settings not defined, no Lookup possible");
                            continue;
                        }

                        if (geoLocationSettings == null) {
                            logger.warn("Brand " + brand.getName() + ": GeoLocation Settings not defined, no Lookup possible");
                            continue;
                        }

                        if ((mobileOfferSettings.getGeoLocationZipCode() == null)
                                || mobileOfferSettings.getGeoLocationZipCode().isEmpty()) {
                            logger.warn("No defined zipcode for Brand " + brand.getName() + ", using default (30308)");
                            tz = zipcodeToTimezoneMgr.getByZipcode("30308");
                        } else {
                            tz = zipcodeToTimezoneMgr.getByZipcode(mobileOfferSettings.getGeoLocationZipCode());
                        }

                        // step 1, see if geolocation dayparting is active
                        boolean activeDaypart = false;
                        List<DayParts> dayParts = geoLocationSettings.getDayParts();
                        for (DayParts dayPart : dayParts) {
                            dayPart.initialize();
                            String localeStartTime = (tz != null) ? TimeZoneUtil.dayPartFromLocaleToUTC(dayPart.getStartTime(), tz.getTimezone(), tz.isUsesDaylightSavings()) : dayPart.getStartTime();
                            String localeEndTime = (tz != null) ? TimeZoneUtil.dayPartFromLocaleToUTC(dayPart.getEndTime(), tz.getTimezone(), tz.isUsesDaylightSavings()) : dayPart.getEndTime();
                            activeDaypart = TimeConstants.isWithinTimeAndRange(lastRun, localeStartTime, localeEndTime, dayPart.getDaysOfWeek());
                            if (activeDaypart) {
                                logger.debug("Found active daypart " + dayPart + " for Brand " + brand.getName());
                                break;
                            }
                        }

                        if (!activeDaypart) {
                            logger.info("Geolocation Dayparting inactive for " + brand.getName());
                            continue;
                        }

                        /**
                         * Check if there are any active Mobile Offers Currently
                         */
                        ActiveMobileOfferLocatorThread mobileOfferThread = new ActiveMobileOfferLocatorThread(brand, companies, mobileOfferMgr, mobileOfferSendLogMgr, mobileOfferBalanceMgr, mobileOfferSettingsMgr, localeMgr);
                        mobileOfferThread.start();

                        while (!mobileOfferThread.isComplete()) {
                            Thread.sleep(1000 * 10);
                        }

                        if (mobileOfferThread.getActiveMobileOfferCount() == 0) {
                            logger.info("No offers are active at this time for " + brand.getName() + " no need to do Lookups");
                            continue;
                        }

                        Long lookupsCompletedThisMonth;
                        
                        if(mobileOfferSettings.isLiveGeoLocation()) {
                            lookupsCompletedThisMonth = locMgr.getTotalLookupsInDateRangeByBrand(DateUtil.getFirstDayOfMonth(lastRun), DateUtil.getLastDayOfMonth(lastRun), brand, LocationData.STATUS_FOUND);
                        } else {
                            lookupsCompletedThisMonth = locMgr.getTotalLookupsInDateRangeByBrand(DateUtil.getFirstDayOfMonth(lastRun), DateUtil.getLastDayOfMonth(lastRun), brand, LocationData.STATUS_FOUND_FAKE);
                        }


                        if (lookupsCompletedThisMonth >= mobileOfferSettings.getMaxLookupsPerMonth()) {
                            logger.warn("Brand: " + brand.getName() + " already hit the Max Number of Lookups: " + mobileOfferSettings.getMaxLookupsPerMonth());
                            continue; //Skip this company
                        }

                        List<Subscriber> subscribers = subscriberMgr.findSubscribersNeedingLocation(brand);


                        if (subscribers != null && !subscribers.isEmpty()) {
                            long totalSubscribersForCompany = subscriberMgr.findOptedInCountByBrand(brand);
                            int lookupsPerSubscriberPerMonth = mobileOfferSettings.getMaxLookupsPerMonthPerSubscriber();

                            long totalLookupsAllowedPerMonth = lookupsPerSubscriberPerMonth * totalSubscribersForCompany;
                            long totalLookupsAllowedToDate = Math.round(lookupsPerSubscriberPerMonth / 31.0 * getLinearDayOfMonth(lastRun) * totalSubscribersForCompany);
                            long freeLookupsAvailable = totalLookupsAllowedToDate - lookupsCompletedThisMonth;

                            logger.debug("TotalSubs: " + totalSubscribersForCompany
                                    + "; LookupsAllowedPerMo: " + totalLookupsAllowedPerMonth
                                    + "; LookupsAllowedToDate: " + totalLookupsAllowedToDate
                                    + "; LookupsUsedToDate: " + lookupsCompletedThisMonth
                                    + "; FreeLookups: " + freeLookupsAvailable);

                            List<String> numbers = new ArrayList<String>();
                            logger.debug("List of Subscribers Needing Location: ");
                            for (Subscriber subscriber : subscribers) {
                                if (subscriber.getStatus().equalsIgnoreCase(Subscriber.STATUS_OPT_IN_COMPLETE)) {
                                    long numLookupsThisMonth = locMgr.getTotalSuccessfulLookupsInDateRangeByMsisdn(DateUtil.getFirstDayOfMonth(lastRun), DateUtil.getLastDayOfMonth(lastRun), subscriber.getMsisdn());
                                    long numMessagesReceivedThisMonth = mobileOfferSendLogMgr.getTotalMessagesSentBySubscriber(lastRun, subscriber);

                                    if (numMessagesReceivedThisMonth >= mobileOfferSettings.getMaxMessagesPerSubscriberPerMonth()) {
                                        logger.debug("MSISDN " + subscriber.getMsisdn()
                                                + " has received " + numMessagesReceivedThisMonth
                                                + " of " + mobileOfferSettings.getMaxMessagesPerSubscriberPerMonth()
                                                + " messages this month and will not be located");
                                    } else if (numLookupsThisMonth >= mobileOfferSettings.getMaxLookupsPerMonthPerSubscriber()) {

                                        if (freeLookupsAvailable > 0) {
                                            freeLookupsAvailable = freeLookupsAvailable - 1;

                                            logger.debug("MSISDN " + subscriber.getMsisdn()
                                                    + " has exeeded the maximum lookups of "
                                                    + mobileOfferSettings.getMaxLookupsPerMonthPerSubscriber()
                                                    + " with " + numLookupsThisMonth
                                                    + ", Using a free lookup,"
                                                    + freeLookupsAvailable + " free lookups remaining");
                                            numbers.add(subscriber.getMsisdn());

                                        } else {
                                            logger.warn("MSISDN " + subscriber.getMsisdn() + " has exceeded the maximum lookups of " + mobileOfferSettings.getMaxLookupsPerMonthPerSubscriber() + " with " + numLookupsThisMonth + " and will not be located");
                                        }
                                    } else {
                                        logger.debug("Location msisdn: " + subscriber.getMsisdn() + " with next_lookup_request of: " + subscriber.getNextLookupRequest());
                                        numbers.add(subscriber.getMsisdn());
                                    }
                                }
                            }

                            List<LocationData> results;
                            if (mobileOfferSettings.isLiveGeoLocation()) {
                                finder = new LocationFinder();
                                logger.debug("LIVE gelocation: " + brand.getName());
                            } else {
                                finder = new FakeLocationFinder();
                                logger.debug("FAKE gelocation: " + brand.getName());
                            }
                            List<GeoFence> geoFences = geoFenceMgr.getGeoFencesByBrand(brand);
                            
                            //FIXME: BIGGEST CHANGE UP FOR CODE REVIEW:
                            //TODO
                            //CHANGING THE synchFind call to an Executor Service
                            //finder.synchFind(numbers, 0, 0, locMgr, subscriberMgr, brand, mobileOfferSettings, geoFences);
                            
                            // //THIS IS THE EXECUTOR CODE
                            //Running 5 threads
                          //  System.out.println("Working with: " + numbers.size() + " number to locate");
                            long start = System.currentTimeMillis();
                            
                            SubscriberGeoLocationExecutorService executorService = new SubscriberGeoLocationExecutorService(5);
                            executorService.startJob(finder, numbers, 0, 0, locMgr, subscriberMgr, brand, mobileOfferSettings, geoFences);
                            long end = System.currentTimeMillis() - start;
                            
                         //   System.out.println("Time it took: " + end +  " ms");
                            
                        } else {
                            logger.info("No subscribers need location for Brand " + brand.getName());
                        }
                    }
                } catch (EJBException ejbex) {
                    logger.fatal("BIG ERROR EJBException don't know why? checking message: ", ejbex);
                    logger.fatal("EXITING THREAD");
                    return;
                } catch (Exception err) {
                    logger.fatal("BIG ERROR FATAL EXCEPTION message: ", err);
                }
            }

            try {
                // at the end of each cycle, sleep
                Thread.sleep(SLEEP_TIME); // sleep a minimum of 3 minutes
            } catch (InterruptedException e) {
                logger.fatal(e);
            }
        }
    }

    public void setBrandMgr(BrandManagerLocal brandMgr) {
        this.brandMgr = brandMgr;
    }

    public void setGeoLocationSettingsMgr(GeoLocationSettingsManagerLocal geoLocationSettingsMgr) {
        this.geoLocationSettingsMgr = geoLocationSettingsMgr;
    }

    public void setLocMgr(LocationDataManagerLocal locMgr) {
        this.locMgr = locMgr;
    }

    public void setMobileOfferSettingsMgr(MobileOfferSettingsManagerLocal mobileOfferSettingsMgr) {
        this.mobileOfferSettingsMgr = mobileOfferSettingsMgr;
    }

    public void setSubscriberMgr(SubscriberManagerLocal subscriberMgr) {
        this.subscriberMgr = subscriberMgr;
    }

    public void setZipcodeToTimezoneMgr(ZipcodeToTimezoneManagerLocal zipcodeToTimezoneMgr) {
        this.zipcodeToTimezoneMgr = zipcodeToTimezoneMgr;
    }

    public MobileOfferSendLogManagerLocal getMobileOfferSendLogMgr() {
        return mobileOfferSendLogMgr;
    }

    public void setMobileOfferSendLogMgr(MobileOfferSendLogManagerLocal mobileOfferSendLogMgr) {
        this.mobileOfferSendLogMgr = mobileOfferSendLogMgr;
    }

    public MobileOfferBalanceManagerLocal getMobileOfferBalanceMgr() {
        return mobileOfferBalanceMgr;
    }

    public void setMobileOfferBalanceMgr(MobileOfferBalanceManagerLocal mobileOfferBalanceMgr) {
        this.mobileOfferBalanceMgr = mobileOfferBalanceMgr;
    }

    public MobileOfferManagerLocal getMobileOfferMgr() {
        return mobileOfferMgr;
    }

    public void setMobileOfferMgr(MobileOfferManagerLocal mobileOfferMgr) {
        this.mobileOfferMgr = mobileOfferMgr;
    }

    public LocaleManagerLocal getLocaleMgr() {
        return localeMgr;
    }

    public void setLocaleMgr(LocaleManagerLocal localeMgr) {
        this.localeMgr = localeMgr;
    }

    public void setCompanyMgr(CompanyManagerLocal companyMgr) {
        this.companyMgr = companyMgr;
    }

    public GeoFenceManagerLocal getGeoFenceMgr() {
        return geoFenceMgr;
    }

    public void setGeoFenceMgr(GeoFenceManagerLocal geoFenceMgr) {
        this.geoFenceMgr = geoFenceMgr;
    }
}
