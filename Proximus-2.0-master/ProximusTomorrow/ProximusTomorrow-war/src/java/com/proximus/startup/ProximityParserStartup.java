/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.startup;

import com.proximus.jobs.parsing.MoveFilesJob;
import com.proximus.jobs.parsing.ParserFactory;
import com.proximus.manager.*;
import com.proximus.manager.report.WifiDaySummaryManagerLocal;
import com.proximus.manager.sms.MobileOfferClickthroughManagerLocal;
import com.proximus.manager.sms.MobileOfferManagerLocal;
import com.proximus.manager.sms.PropertyManagerLocal;
import com.proximus.startup.warehouse.WifiDaySummaryCreator;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
@Singleton
@Startup
public class ProximityParserStartup implements ProximityParserEJBRemote {

    static final Logger logger = Logger.getLogger(ProximityParserStartup.class.getName());
    @EJB
    DeviceManagerLocal deviceMgr;
    @EJB
    CampaignManagerLocal campaignMgr;
    @EJB
    WifiLogManagerLocal wifiMgr;
    @EJB
    WifiRegistrationManagerLocal wifiRegMgr;
    @EJB
    BluetoothReportManagerLocal btReportMgr;
    @EJB
    BarcodeManagerLocal barcodeMgr;
    @EJB
    MobileOfferClickthroughManagerLocal clickthroughMgr;
    @EJB
    MobileOfferManagerLocal offerMgr;
    @EJB
    PropertyManagerLocal propertyMgr;
    @EJB
    FacebookUserLogManagerLocal facebookLogMgr;
    
     @EJB
    CompanyManagerLocal companyMgr;



    @EJB
    WifiLogManagerLocal wifiLogMgr;
    @EJB
    UserProfileSummaryManagerLocal userProfileMgr;
    @EJB
    BluetoothDwellReportManagerLocal btDwellReportMgr;
    @EJB
    WifiDaySummaryManagerLocal wifiDaySummaryMgr;

    @PostConstruct
    @Override
    public void start() {

        logger.info("Starting ProximityParserStarup");
        Thread factoryThread = new Thread(new ParserFactory(deviceMgr, campaignMgr, wifiMgr, wifiRegMgr, barcodeMgr, btReportMgr,
                clickthroughMgr, offerMgr, propertyMgr, facebookLogMgr), "Proximity Parser Thread");
        factoryThread.start();
        logger.info("Factory Thread started");
        Thread moveFilesThread = new Thread(new MoveFilesJob());
        moveFilesThread.start();
        logger.info("moveFilesThread started started");
        
        
        WifiDaySummaryCreator creator = new WifiDaySummaryCreator();
        creator.setBtDwellReportMgr(btDwellReportMgr);
        creator.setBtReportMgr(btReportMgr);
        creator.setCampaignMgr(campaignMgr);
        creator.setCompanyMgr(companyMgr);
        creator.setDeviceMgr(deviceMgr);
        creator.setUserProfileMgr(userProfileMgr);
        creator.setWifiDaySummaryMgr(wifiDaySummaryMgr);
        creator.setWifiLogMgr(wifiLogMgr);
        creator.setWifiRegMgr(wifiRegMgr);
        Thread startThread = new Thread(creator);
        startThread.start();
        
    }
}
