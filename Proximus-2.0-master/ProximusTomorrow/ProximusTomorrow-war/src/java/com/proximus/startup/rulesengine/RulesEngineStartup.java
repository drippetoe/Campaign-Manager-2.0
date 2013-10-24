package com.proximus.startup.rulesengine;

import com.proximus.manager.CompanyManagerLocal;
import com.proximus.manager.DayPartsManagerLocal;
import com.proximus.manager.sms.LocationDataManagerLocal;
import com.proximus.manager.ZipcodeToTimezoneManagerLocal;
import com.proximus.manager.sms.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
@Singleton
@Startup
@LocalBean
public class RulesEngineStartup implements RulesEngineRemote {

    static final Logger logger = Logger.getLogger(RulesEngineStartup.class.getName());
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    BrandManagerLocal brandMgr;
    @EJB
    DMAManagerLocal dmaMgr;
    @EJB
    DayPartsManagerLocal daypartMgr;
    @EJB
    LocationDataManagerLocal locationDataMgr;
    @EJB
    LocationDataManagerLocal locationDataMgrTwo;
    @EJB
    MobileOfferManagerLocal mobileOfferMgr;
    @EJB
    MobileOfferBalanceManagerLocal mobileOfferBalanceMgr;
    @EJB
    MobileOfferSendLogManagerLocal mobileOfferSendLogMgr;
    @EJB
    MobileOfferSettingsManagerLocal mobileOfferSettingsMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    SubscriberManagerLocal subscriberMgr;
    @EJB
    SubscriberManagerLocal subscriberMgrTwo;
    @EJB
    GeoFenceManagerLocal geoFenceMgr;
    @EJB
    GeoLocationSettingsManagerLocal geoLocationSettingsMgr;
    @EJB
    ZipcodeToTimezoneManagerLocal zipcodeToTimezoneMgr;
    @EJB
    RetailerManagerLocal retailerMgr;
    @EJB
    LocaleManagerLocal localeMgr;
    RulesEngineThread rulesEngineThread;
    SubscriberGeoLocationThread geoLocationThread;

    @PostConstruct
    @Override
    public void start() {
        try {
            System.err.println("RulesEngineStartup: Sleeping 10 seconds before starting to allow injection to catch up");
            Thread.sleep(1 * 1000);
        } catch (InterruptedException ex) {
            logger.fatal("sleep exception", ex);

        }
        
        if (companyMgr == null )
        {
            System.err.println("Injection failed in RulesEngineStartup");
            logger.fatal("Injection failed in RulesEngineStartup");
            return;
        }
        
        logger.info("Starting RulesEngineThread");
        rulesEngineThread = RulesEngineThread.getInstance();
        rulesEngineThread.setCompanyMgr(companyMgr);
        rulesEngineThread.setDmaMgr(dmaMgr);
        rulesEngineThread.setDaypartMgr(daypartMgr);
        rulesEngineThread.setLocationDataMgr(locationDataMgr);
        rulesEngineThread.setMobileOfferMgr(mobileOfferMgr);
        rulesEngineThread.setMobileOfferBalanceMgr(mobileOfferBalanceMgr);
        rulesEngineThread.setMobileOfferSendLogMgr(mobileOfferSendLogMgr);
        rulesEngineThread.setMobileOfferSettingsMgr(mobileOfferSettingsMgr);
        rulesEngineThread.setPropertyMgr(propertyMgr);
        rulesEngineThread.setSubscriberMgr(subscriberMgr);
        rulesEngineThread.setGeoFenceMgr(geoFenceMgr);
        rulesEngineThread.setZipcodeToTimeZoneMgr(zipcodeToTimezoneMgr);
        rulesEngineThread.setRetailerMgr(retailerMgr);
        rulesEngineThread.setLocaleMgr(localeMgr);
        rulesEngineThread.setBrandMgr(brandMgr);
        rulesEngineThread.start();
        logger.info("Started RulesEngineThread");

        logger.info("Starting SubscriberGeoLocationThread");
        geoLocationThread = SubscriberGeoLocationThread.getInstance();
        geoLocationThread.setBrandMgr(brandMgr);
        geoLocationThread.setCompanyMgr(companyMgr);
        geoLocationThread.setLocaleMgr(localeMgr);
        geoLocationThread.setMobileOfferSettingsMgr(mobileOfferSettingsMgr);
        geoLocationThread.setSubscriberMgr(subscriberMgr);
        geoLocationThread.setLocMgr(locationDataMgr);
        geoLocationThread.setGeoLocationSettingsMgr(geoLocationSettingsMgr);
        geoLocationThread.setZipcodeToTimezoneMgr(zipcodeToTimezoneMgr);
        geoLocationThread.setMobileOfferSendLogMgr(mobileOfferSendLogMgr);
        geoLocationThread.setMobileOfferMgr(mobileOfferMgr);
        geoLocationThread.setMobileOfferBalanceMgr(mobileOfferBalanceMgr);
        geoLocationThread.setGeoFenceMgr(geoFenceMgr);
        geoLocationThread.start();
        logger.info("Started SubscriberGeoLocationThread Thread");
    }
}
