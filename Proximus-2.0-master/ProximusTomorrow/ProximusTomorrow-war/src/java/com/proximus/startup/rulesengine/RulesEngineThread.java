/*
 * Copyright 2010-2012, Proximus Mobility, LLC.
 * 
 */
package com.proximus.startup.rulesengine;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import com.proximus.data.License;
import com.proximus.data.sms.LocationData;
import com.proximus.data.sms.*;
import com.proximus.data.util.DateUtil;
import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DayPartsManagerLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.ZipcodeToTimezoneManagerLocal;
import com.proximus.manager.sms.*;
import com.proximus.rest.sms.SMSPlatformRESTClient;
import com.proximus.util.JAXBUtil;
import com.proximus.util.ServerURISettings;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author dshaw
 */
public class RulesEngineThread extends Thread {

    static final Logger logger = Logger.getLogger(RulesEngineThread.class.getName());
    static final Integer millisecondsBetweenRuns = 2 * 60 * 1000;  // determines how often the thread runs
    static final SimpleDateFormat fsdf = new SimpleDateFormat(ServerURISettings.FILE_DATEFORMAT);
    CompanyManagerLocal companyMgr;
    BrandManagerLocal brandMgr;
    DMAManagerLocal dmaMgr;
    PropertyManagerLocal propertyMgr;
    MobileOfferManagerLocal mobileOfferMgr;
    SubscriberManagerLocal subscriberMgr;
    LocationDataManagerLocal locationDataMgr;
    DayPartsManagerLocal daypartMgr;
    MobileOfferSettingsManagerLocal mobileOfferSettingsMgr;
    MobileOfferSendLogManagerLocal mobileOfferSendLogMgr;
    GeoFenceManagerLocal geoFenceMgr;
    MobileOfferBalanceManagerLocal mobileOfferBalanceMgr;
    ZipcodeToTimezoneManagerLocal zipcodeToTimeZoneMgr;
    RetailerManagerLocal retailerMgr;
    LocaleManagerLocal localeMgr;
    Date lastRun;
    private long randomNum = System.currentTimeMillis();

    private RulesEngineThread() {
    }

    /**
     * RulesEngineThreadHolder is loaded on the first execution of
     * RulesEngineThread.getInstance() or the first access to
     * RulesEngineThreadHolder.INSTANCE, not before.
     */
    private static class RulesEngineThreadHolder {

        public static final RulesEngineThread instance = new RulesEngineThread();
    }

    public static RulesEngineThread getInstance() {
        return RulesEngineThreadHolder.instance;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            logger.log(Priority.FATAL, e);
        }

        lastRun = new Date(0L);

        while (true) {

            if (System.currentTimeMillis() - lastRun.getTime() > millisecondsBetweenRuns) {
                lastRun = new Date();

                try {
                    List<Brand> brands = brandMgr.findAll();

                    for (Brand brand : brands) {

                        List<Company> companies = companyMgr.findCompaniesWithLicenseInBrand(License.LICENSE_GEOFENCE, brand);
                        MobileOfferSettings mos = mobileOfferSettingsMgr.findByBrand(brand);
                        if (mos == null) {
                            logger.info("Mobile Offer Settings Not Defined for Brand " + brand.getName() + " cannot proceed");
                            continue;
                        }

                        /**
                         * Step 0: Check if The Brand has exceeded its Messages
                         * and/or Ping quota
                         *
                         */
                        if (companies != null && !companies.isEmpty()) {
                            Long numberOfPingsSent;
                            if(mos.isLiveGeoLocation()) {
                                 numberOfPingsSent = locationDataMgr.getTotalLookupsInDateRangeByBrand(DateUtil.getFirstDayOfMonth(lastRun), DateUtil.getLastDayOfMonth(lastRun), brand, LocationData.STATUS_FOUND);
                            } else {
                                 numberOfPingsSent = locationDataMgr.getTotalLookupsInDateRangeByBrand(DateUtil.getFirstDayOfMonth(lastRun), DateUtil.getLastDayOfMonth(lastRun), brand, LocationData.STATUS_FOUND_FAKE);
                            }
                            Long numberOfMessagesSent = 0L;

                            for (Company company : companies) {
                                numberOfMessagesSent += mobileOfferSendLogMgr.getTotalMessagesSentByCompanyAndDateRange(company, DateUtil.getFirstDayOfMonth(lastRun), DateUtil.getLastDayOfMonth(lastRun));

                            }
                            if (numberOfMessagesSent >= mos.getMaxMessagesPerMonth() || numberOfPingsSent >= mos.getMaxLookupsPerMonth()) {
                                if (numberOfMessagesSent >= mos.getMaxMessagesPerMonth()) {
                                    logger.warn("Brand: " + brand.getName() + " already hit the Max Messages Quota of: " + mos.getMaxMessagesPerMonth());
                                }
                                if (numberOfPingsSent >= mos.getMaxLookupsPerMonth()) {
                                    logger.warn("Brand: " + brand.getName() + " already hit the Max Number of Lookup Requests of: " + mos.getMaxLookupsPerMonth());
                                }
                                continue; //Skip this Brand
                            }

                            /**
                             * Step 1: get all active mobile offers
                             */
                            logger.info("*** Brand " + brand.getName() + " RulesEngine STEP 1: get active mobile offers");
                            ActiveMobileOfferLocatorThread mobileOfferThread = new ActiveMobileOfferLocatorThread(brand, companies, mobileOfferMgr, mobileOfferSendLogMgr, mobileOfferBalanceMgr, mobileOfferSettingsMgr, localeMgr);
                            mobileOfferThread.start();

                            while (!mobileOfferThread.isComplete()) {
                                Thread.sleep(1000 * 10);
                            }

                            if (mobileOfferThread.getActiveMobileOfferCount() == 0) {
                                logger.info("No offers are active at this time for " + brand.getName());
                                continue;
                            }

                            /**
                             * Step 2: determine if Dayparting suggests that it
                             * is time to send offers
                             */
                            logger.info("*** Brand " + brand.getName() + " RulesEngine STEP 2: checking dayparting");
                            ActiveDayPartLocatorThread daypartThread = new ActiveDayPartLocatorThread(brand, mos, mobileOfferThread.getActiveOffers(), zipcodeToTimeZoneMgr, retailerMgr);
                            daypartThread.start();

                            while (!daypartThread.isComplete()) {
                                Thread.sleep(10000); // sleep 10 seconds
                            }

                            List<MobileOffer> activeOffers = daypartThread.getAllActiveOffersList();

                            if (activeOffers == null || activeOffers.isEmpty()) {
                                logger.info("No dayparts are active at this time for " + brand.getName());
                                continue;
                            }

                            /**
                             * Step 3: determine any active subscribers
                             */
                            logger.info("*** Brand " + brand.getName() + " RulesEngine STEP 3: determine active subscribers");
                            ActiveSubscriberLocatorThread subscriberThread = new ActiveSubscriberLocatorThread(brand, subscriberMgr, locationDataMgr, mobileOfferSettingsMgr, mobileOfferSendLogMgr, geoFenceMgr);
                            subscriberThread.start();
                            while (!subscriberThread.isComplete()) {
                                Thread.sleep(10000);
                            }

                            List<Subscriber> eligibleSubscribers = subscriberThread.getEligibleSubscribers();
                            if (eligibleSubscribers == null || eligibleSubscribers.isEmpty()) {
                                logger.info("No subscribers are eligible for receiving a message at this time for " + brand.getName());
                                continue;
                            }

                            /**
                             * Step 4, combine active offers and eligible
                             * subscribers with offers
                             */
                            logger.info("*** Brand " + brand.getName() + " RulesEngine STEP 4: pair offers with subscribers");
                            SubscriberOfferPairingThread pairingThread = new SubscriberOfferPairingThread(brand, eligibleSubscribers, activeOffers, geoFenceMgr, localeMgr);
                            pairingThread.start();
                            while (!pairingThread.isComplete()) {
                                Thread.sleep(10000);
                            }

                            /*
                             * Step 5, send the offers If Mobile Offer is retail
                             * wide break it into the different Offers per
                             * Property that Eligible Subscribers are Paired
                             * with Otherwise use normal distribution list
                             */
                            logger.info("*** Brand " + brand.getName() + " RulesEngine STEP 5: send offers");
                            Map<Subscriber, Property> subscriberToPropertyMap = pairingThread.getSubscriberToPropertyMap();
                            Map<MobileOffer, List<Subscriber>> offerMap = pairingThread.getOfferMap();

                            List<Subscriber> allSubscribers = new ArrayList<Subscriber>();

                            for (Map.Entry<MobileOffer, List<Subscriber>> entry : offerMap.entrySet()) {
                                MobileOffer mobileOffer = entry.getKey();

                                // we send each offer to the matched subscribers, with the appropriate property
                                Map<Property, List<Subscriber>> propertySubscriberByOfferMap = new HashMap<Property, List<Subscriber>>();
                                List<Subscriber> list = entry.getValue();
                                for (Subscriber subscriber : list) {
                                    // safety check, one offer per subscriber per run
                                    if (allSubscribers.contains(subscriber)) {
                                        logger.error("Subscriber " + subscriber.getMsisdn() + " was in two offer lists, skipping the second");
                                        continue;
                                    } else {
                                        allSubscribers.add(subscriber);
                                    }

                                    // group the subscribers with an offer my the property, to handle retail-wide offers
                                    Property property = subscriberToPropertyMap.get(subscriber);
                                    List<Subscriber> offerSubList;
                                    if (propertySubscriberByOfferMap.containsKey(property)) {
                                        offerSubList = propertySubscriberByOfferMap.get(property);
                                        offerSubList.add(subscriber);
                                    } else {
                                        offerSubList = new ArrayList<Subscriber>();
                                        offerSubList.add(subscriber);
                                    }
                                    propertySubscriberByOfferMap.put(property, offerSubList);
                                }

                                // now we have the offer, with the subscribers grouped by property in the propertySubscriberByOfferMap.  Send each
                                for (Map.Entry<Property, List<Subscriber>> propSubList : propertySubscriberByOfferMap.entrySet()) {
                                    Property property = propSubList.getKey();
                                    List<Subscriber> subscribersForOffer = propSubList.getValue();
                                    String webHash = property.getWebHash();
                                    sendOfferToSubscribers(subscribersForOffer, mobileOffer, webHash, brand, mos, property);
                                }

                            }
                        }
                    }
                } catch (EJBException ejberr) {
                    logger.warn("This RulesEngineStartup Thread is getting killed because EJB have not been properly injected");
                    break;

                } catch (Exception err) {
                    // nothing causes the exception to leave this entity
                    logger.fatal("Error in RulesEngineStarup", err);
                }
            }

            try {
                // at the end of each cycle, sleep
                logger.debug("RULES ENGINE THREAD SLEEPING");
                Thread.sleep(1000 * 30); // sleep a minimum time
            } catch (InterruptedException e) {
                logger.fatal(e);
            }

        }

        logger.fatal("Outside of RulesEngineThread Loop. This only happens if EJBs were not initialized");

    }

    /**
     * Actual Sending calls to KAZE API
     *
     * @param subscribersForOffer
     * @param mobileOffer
     * @param webHash
     * @param company
     * @param companySettings
     * @throws Exception
     */
    private void sendOfferToSubscribers(List<Subscriber> subscribersForOffer, MobileOffer mobileOffer, String webHash, Brand brand, MobileOfferSettings settings, Property property) throws Exception {
        KazePacket kp = new KazePacket();
        SMSPlatformRESTClient smsClient = new SMSPlatformRESTClient();
        /**
         * KazePacket generation
         */
        for (Subscriber subscriber : subscribersForOffer) {
            /**
             * 1 Create MobileOfferSendLog for each subscriber
             */
            MobileOfferSendLog mosl = new MobileOfferSendLog();
            mosl.setBrand(brand);
            mosl.setCompany(property.getCompany());
            mosl.setEventDate(new Date());
            mosl.setMobileOffer(mobileOffer);
            mosl.setSubscriber(subscriber);
            mosl.setMsisdn(subscriber.getMsisdn());
            mosl.setProperty(property);
            mosl.setGeofenceId(subscriber.getCurrentClosestGeoFenceId());
            if (!settings.isLiveGeoLocation()) {
                mosl.setStatus(MobileOfferSendLog.STATUS_DELIVERED);
            } else {
                mosl.setStatus(MobileOfferSendLog.STATUS_PENDING);
            }

            /**
             * 2 Persist send log
             */
            logger.debug("KazePacket: Persisting sendlog.");
            mosl = mobileOfferSendLogMgr.update(mosl);
            if (mosl.getId() == 0) {
                logger.fatal("Something went wrong trying to persist mobile_offer_send_log");
            }
            /**
             * 3. add it to the KazePacket
             */
            logger.debug("KazePacket: Adding sendlog " + mosl.getId());
            kp.addMobileOfferSendLog(mosl);
        }

        if (subscribersForOffer != null && !subscribersForOffer.isEmpty()) {
            if (mobileOffer != null && mobileOffer.getLogName() != null && subscribersForOffer != null) {
                logger.info("Sending offer " + mobileOffer.getLogName() + " to " + subscribersForOffer.size() + " subscribers: " + subscribersForOffer);
            }

            //FAKE SENDS!
            if (!settings.isLiveGeoLocation()) {
                sendFake(subscribersForOffer, mobileOffer, webHash, settings, property);          
            } else { //REAL SENDING OF MESSAGES
                long campId = -1L;
                int distributionListId = -1;

                try {
                    distributionListId = smsClient.createSubscriberList(subscribersForOffer, mobileOffer);
                } catch (Exception e) {
                    logger.fatal("Create SubscriberList failed on Kaze SMS client Call", e);
                    return;
                }
                try {
                    campId = smsClient.createMobileOffer(settings, mobileOffer, webHash, distributionListId, property);
                } catch (Exception e) {
                    //TODO GET BETTER LOGGING and catch error correctly
                    logger.fatal("Create MobileOffer failed on Kaze SMS client Call", e);
                    return;
                }

                if (campId != -1L) {

                    //SUCCESS WE CAN MODIFY BALANCE AND NEXTLOOKUP REQUEST
                    //Modifying Balancer
                    MobileOfferMonthlyBalance balancer = mobileOfferBalanceMgr.findByMobileOfferAndMonth(mobileOffer, lastRun);
                    Long balance = balancer.getBalance();
                    balance += subscribersForOffer.size();
                    balancer.setBalance(balance);
                    mobileOfferBalanceMgr.update(balancer);

                    Date currTime = new Date();
                    Date d = new Date(currTime.getTime() + settings.getMinTimeBetweenMessages());
                    for (Subscriber s : subscribersForOffer) {
                        s.setNextLookupRequest(d);
                        subscriberMgr.update(s);
                    }


                    /**
                     * 4. Set the campaign id from kaze
                     */
                    logger.debug("KazePacket: Got campaign id = " + campId + " from kaze.");
                    kp.setCampaignId(campId);
                    /**
                     * 5. Write to file
                     */
                    String fileName = ServerURISettings.KAZE_PACKET_FOLDER + ServerURISettings.OS_SEP + "kaze_outbound_" + campId + ".log";
                    kp.setFileName(fileName);

                    logger.debug("KazePacket: Writing data to file: " + kp.getFileName());
                    if (!new File(ServerURISettings.KAZE_PACKET_FOLDER).exists()) {
                        new File(ServerURISettings.KAZE_PACKET_FOLDER).mkdirs();
                    }
                    JAXBUtil.saveToFile(KazePacket.class, kp, kp.getFileName());
                } else {
                    logger.fatal("KazePacket: CampaignId " + kp.getCampaignId() + " Invalid");
                }
            }
        }
    }

    public void setCompanyMgr(CompanyManagerLocal companyMgr) {
        this.companyMgr = companyMgr;
    }

    public void setDaypartMgr(DayPartsManagerLocal daypartMgr) {
        this.daypartMgr = daypartMgr;
    }

    public void setDmaMgr(DMAManagerLocal dmaMgr) {
        this.dmaMgr = dmaMgr;
    }

    public void setLocationDataMgr(LocationDataManagerLocal locationDataMgr) {
        this.locationDataMgr = locationDataMgr;
    }

    public void setMobileOfferMgr(MobileOfferManagerLocal mobileOfferMgr) {
        this.mobileOfferMgr = mobileOfferMgr;
    }

    public void setMobileOfferSendLogMgr(MobileOfferSendLogManagerLocal mobileOfferSendLogMgr) {
        this.mobileOfferSendLogMgr = mobileOfferSendLogMgr;
    }

    public void setMobileOfferBalanceMgr(MobileOfferBalanceManagerLocal mobileOfferBalanceMgr) {
        this.mobileOfferBalanceMgr = mobileOfferBalanceMgr;
    }

    public void setMobileOfferSettingsMgr(MobileOfferSettingsManagerLocal mobileOfferSettingsMgr) {
        this.mobileOfferSettingsMgr = mobileOfferSettingsMgr;
    }

    public void setPropertyMgr(PropertyManagerLocal propertyMgr) {
        this.propertyMgr = propertyMgr;
    }

    public void setSubscriberMgr(SubscriberManagerLocal subscriberMgr) {
        this.subscriberMgr = subscriberMgr;
    }

    public BrandManagerLocal getBrandMgr() {
        return brandMgr;
    }

    public void setBrandMgr(BrandManagerLocal brandMgr) {
        this.brandMgr = brandMgr;
    }

    public void setGeoFenceMgr(GeoFenceManagerLocal geoFenceMgr) {
        this.geoFenceMgr = geoFenceMgr;
    }

    public LocaleManagerLocal getLocaleMgr() {
        return localeMgr;
    }

    public void setLocaleMgr(LocaleManagerLocal localeMgr) {
        this.localeMgr = localeMgr;
    }

    public ZipcodeToTimezoneManagerLocal getZipcodeToTimeZoneMgr() {
        return zipcodeToTimeZoneMgr;
    }

    public void setZipcodeToTimeZoneMgr(ZipcodeToTimezoneManagerLocal zipcodeToTimeZoneMgr) {
        this.zipcodeToTimeZoneMgr = zipcodeToTimeZoneMgr;
    }

    public RetailerManagerLocal getRetailerMgr() {
        return retailerMgr;
    }

    public void setRetailerMgr(RetailerManagerLocal retailerMgr) {
        this.retailerMgr = retailerMgr;
    }

    private void sendFake(List<Subscriber> subscribersForOffer, MobileOffer mobileOffer, String webHash, MobileOfferSettings mos, Property property) {

        //Dynamically adding the webHash
        String realOffer = null;
        if (webHash != null && mos.getOfferUrl() != null && !mos.getOfferUrl().isEmpty()) {
            realOffer = mobileOffer.getOfferWithWebHash(mos.getOfferUrl(), webHash);
            if (realOffer == null) {
                realOffer = mobileOffer.getCleanOfferText();
            }
        }

        //Dynamically getting the Property Address
        realOffer = realOffer.replaceAll(Property.ADDRESS_REGEX, property.getAddress());
        realOffer = realOffer.replaceAll(Property.PROPERTY_REGEX, property.getName());

        logger.info("Sending FAKE Offer (" + mobileOffer.getId() + ") " + mobileOffer.getName() + " of length: " + realOffer.length() + " with text: [" + realOffer + "]");
        logger.debug("Launch on: " + mobileOffer.getStartDate());
        logger.debug("Expires on: " + mobileOffer.getEndDate());
        logger.info("To Users:");
        for (Subscriber subscriber : subscribersForOffer) {
            logger.info("\t" + subscriber.getMsisdn());
        }

        logger.info("******** FAKE SENDER COMPLETE *******");

        //SUCCESS WE CAN MODIFY BALANCE AND NEXTLOOKUP REQUEST
        //Modifying Balancer
        MobileOfferMonthlyBalance balancer = mobileOfferBalanceMgr.findByMobileOfferAndMonth(mobileOffer, lastRun);
        Long balance = balancer.getBalance();
        balance += subscribersForOffer.size();
        balancer.setBalance(balance);
        mobileOfferBalanceMgr.update(balancer);

        Date currTime = new Date();
        Date d = new Date(currTime.getTime() + mos.getMinTimeBetweenMessages());
        for (Subscriber s : subscribersForOffer) {
            s.setNextLookupRequest(d);
            subscriberMgr.update(s);
        }
    }
}
