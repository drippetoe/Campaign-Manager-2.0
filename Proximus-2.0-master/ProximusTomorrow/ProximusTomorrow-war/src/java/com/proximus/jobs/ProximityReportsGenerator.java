/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jobs;

import com.proximus.data.*;
import com.proximus.data.report.BluetoothDaySummary;
import com.proximus.data.report.BluetoothFileSendSummary;
import com.proximus.data.report.UserProfileSummary;
import com.proximus.data.report.WifiDaySummary;
import com.proximus.manager.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author dshaw
 */
public class ProximityReportsGenerator extends Thread {
    
    private static final Logger logger = Logger.getLogger(ProximityReportsGenerator.class.getName());
    CompanyManagerLocal companyMgr;
    CampaignManagerLocal campaignMgr;
    DeviceManagerLocal deviceMgr;
    BluetoothReportManagerLocal btReportMgr;
    WifiLogManagerLocal wifiLogMgr;
    WifiRegistrationManagerLocal wifiRegMgr;
    UserProfileSummaryManagerLocal userProfileMgr;
    BluetoothDwellReportManagerLocal btDwellReportMgr;
    
    public ProximityReportsGenerator(BluetoothReportManagerLocal btReportMgr, WifiLogManagerLocal wifiReportMgr, WifiRegistrationManagerLocal wifiRegMgr,
            UserProfileSummaryManagerLocal userProfileMgr, CompanyManagerLocal companyMgr, CampaignManagerLocal campaignMgr, DeviceManagerLocal deviceMgr, BluetoothDwellReportManagerLocal btDwellReportMgr) {
        this.btReportMgr = btReportMgr;
        this.wifiLogMgr = wifiReportMgr;
        this.wifiRegMgr = wifiRegMgr;
        this.userProfileMgr = userProfileMgr;
        this.companyMgr = companyMgr;
        this.campaignMgr = campaignMgr;
        this.deviceMgr = deviceMgr;
        this.btDwellReportMgr = btDwellReportMgr;
    }
    
    /**
     * CODE BEING DEPRECTAED AS OF 11/19/2012
     * ProximityReportGenStartup We'll be deleted since we don't generate any more summaries for reporting
     * @deprecated
     */
    @Deprecated
    private void generateStatisticsForDateRange(List<Date> dateRange, List<Company> allProximityCompanies) {
        
        for (Date theDate : dateRange) {
            
            for (Company company : allProximityCompanies) {
                //logger.log(Level.INFO, "Processing company {0}", company.getName());
                List<Campaign> campaigns = campaignMgr.findAllByCompanyActive(company);
                for (Campaign campaign : campaigns) {
                    List<Device> devices = deviceMgr.findAllActive(company);
                    
                    if (devices != null) {
                        for (Device device : devices) {
                            //logger.log(Level.INFO, "Processing Company {0}, campaign {1}, device {2}", new Object[]{company.getId(), campaign.getId(), device.getId()});

                            // Bluetooth Retail
                            BluetoothDaySummary btSummary = btReportMgr.fetchBluetoothDaySummary(theDate, company, campaign, device);
                            if (btSummary == null) {
                                btSummary = new BluetoothDaySummary();
                                btSummary.setCompany(company);
                                btSummary.setCampaign(campaign);
                                btSummary.setDevice(device);
                                btSummary.setEventDate(theDate);
                            }
                            btSummary.setTotalDevicesSeen(btReportMgr.getTotalDevicesSeen(company, theDate, campaign, device));
                            btSummary.setUniqueDevicesAcceptingPush(btReportMgr.getUniqueDevicesAcceptingPush(company, theDate, campaign, device));
                            btSummary.setUniqueDevicesDownloadingContent(btReportMgr.getUniqueDevicesDownloadingContent(company, theDate, campaign, device));
                            btSummary.setUniqueDevicesSeen(btReportMgr.getUniqueDevicesSeen(company, theDate, campaign, device));
                            btSummary.setUniqueDevicesSupportingBluetooth(btReportMgr.getUniqueDevicesSupportingBluetooth(company, theDate, campaign, device));
                            btSummary.setTotalContentDownloads(btReportMgr.getTotalContentDownloads(company, theDate, campaign, device));
                            
                            logger.debug(btSummary.toString());
                            
                            btReportMgr.createOrUpdateBluetoothDaySummary(btSummary);

                            // Bluetooth File Send
                            List<BluetoothFileSendSummary> savedSummaries = btReportMgr.fetchBluetoothFileSendSummaries(theDate, company, campaign, device);
                            
                            List<BluetoothFileSendSummary> calculatedSummaries = btReportMgr.getBluetoothFileSendSummaryFromBluetoothSend(theDate, theDate, company, campaign, device);
                            for (BluetoothFileSendSummary calculatedEntry : calculatedSummaries) {
                                
                                BluetoothFileSendSummary entryToPersist = calculatedEntry;
                                
                                for (BluetoothFileSendSummary savedEntry : savedSummaries) {
                                    if (savedEntry.getFile().equals(calculatedEntry.getFile())) {
                                        // if there was already a persisted entry, update it
                                        entryToPersist = savedEntry;
                                        entryToPersist.setSendCount(calculatedEntry.getSendCount());
                                        break;
                                    }
                                }
                                
                                logger.debug(entryToPersist.toString());
                                btReportMgr.createOrUpdateBluetoothFileSendSummary(entryToPersist);
                            }

                            // Wi-Fi Retail
                            WifiDaySummary wifiSummary = wifiLogMgr.fetchWifiDaySummary(theDate, company, campaign, device);
                            if (wifiSummary == null) {
                                wifiSummary = new WifiDaySummary();
                                wifiSummary.setCompany(company);
                                wifiSummary.setCampaign(campaign);
                                wifiSummary.setDevice(device);
                                wifiSummary.setEventDate(theDate);
                            }
                            wifiSummary.setSuccessfulPageViews(wifiLogMgr.getSuccessfulPageViews(company, theDate, campaign, device));
                            wifiSummary.setTotalPageViews(wifiLogMgr.getTotalPageViews(company, theDate, campaign, device));
                            wifiSummary.setTotalRequests(wifiLogMgr.getTotalRequests(company, theDate, campaign, device));
                            wifiSummary.setUniqueUserCount(wifiLogMgr.getUniqueUserCount(company, theDate, campaign, device));
                            
                            logger.debug(wifiSummary.toString());
                            wifiLogMgr.createOrUpdateWifiDaySummary(wifiSummary);
                        }
                    }
                }
            }
        }
    }
    
    
    /**
     * CODE BEING DEPRECTAED AS OF 11/19/2012
     * ProximityReportGenStartup We'll be deleted since we don't generate any more summaries for reporting
     * @deprecated
     */
    @Deprecated
    private void generateUserProfile(List<Company> allProximityCompanies) {
        
        for (Company company : allProximityCompanies) {
            List<Device> devices = btDwellReportMgr.getDevicesWithBluetoothDwell(company);
            for (Device d : devices) {
                List<String> macAddresses = btDwellReportMgr.getDeviceMacAddresses(company);
                if (macAddresses != null) {
                    for (String mac : macAddresses) {
                        UserProfileSummary userProfile = userProfileMgr.fetchUserProfileSummary(mac, company, d);
                        if (userProfile != null) {
                            //do a lookup on bt_dwell from userProfile.last_seen to now()
                            Date lastDate = new Date();
                            //adding an extra second
                            lastDate.setTime(userProfile.getLastSeen().getTime() + 1000);
                            List<BluetoothDwell> btDwellRecords = btDwellReportMgr.getBluetoothDwellRecordsFrom(lastDate, new Date(), mac, company, d);
                            if (btDwellRecords != null) {
                                //aggregate data and update userProfile
                                userProfile.setLastSeen(btDwellRecords.get(btDwellRecords.size() - 1).getEventDate());
                                userProfile.addToMacCount(btDwellRecords.size());
                                for (BluetoothDwell btDwell : btDwellRecords) {
                                    userProfile.addToDwellTime(btDwell.getDwellTimeMS());
                                }
                                userProfileMgr.update(userProfile);
                            }
                        } else {
                            //We don't have a record for this macAddress
                            //go through all records on bt_dwell for this macAdddress

                            List<BluetoothDwell> btDwellRecords = btDwellReportMgr.getAllBluetoothDwellRecordsFrom(mac, company, d);
                            if (btDwellRecords != null) {
                                //aggregate data and create userProfile
                                userProfile = new UserProfileSummary();
                                userProfile.setCompany(company);
                                userProfile.setDevice(d);
                                userProfile.setMacAddress(mac);
                                userProfile.setFriendlyName(btDwellReportMgr.getDeviceFriendlyNames(mac, company));
                                userProfile.setFirstSeen(btDwellRecords.get(0).getEventDate());
                                userProfile.setLastSeen(btDwellRecords.get(btDwellRecords.size() - 1).getEventDate());
                                userProfile.addToMacCount(btDwellRecords.size());
                                for (BluetoothDwell btDwell : btDwellRecords) {
                                    userProfile.addToDwellTime(btDwell.getDwellTimeMS());
                                }
                                userProfileMgr.create(userProfile);
                            }
                        }
                    }
                }
                
                
            }
        }
    }
    
    
    @Override
    public void run() {
        //logger.log(Level.ALL, "ProximityReportsGenerator thread starting up: {0}", this.getName());
//        while (true) {
//            try {
//                Thread.sleep(1000 * 60 * 60 * 1); // don't run continuously, 1 per hour might be good, or maybe sleeps between companies
//
//                /*
//                 * This thread runs for every companym and for every campaign
//                 * generates one row per day of each type, describing the
//                 * aggregate of that day's reports
//                 */
//                GregorianCalendar cal = new GregorianCalendar();
//                cal.set(Calendar.HOUR_OF_DAY, 0);
//                cal.set(Calendar.MINUTE, 0);
//                cal.set(Calendar.SECOND, 0);
//                cal.set(Calendar.MILLISECOND, 0);
//                Date today = cal.getTime();
//                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
//                Date yesterday = cal.getTime();
//                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
//                Date twoDaysAgo = cal.getTime();
//                
//                List<Date> dateRange = new ArrayList<Date>();
//                dateRange.add(twoDaysAgo);
//                dateRange.add(yesterday);
//                dateRange.add(today);
//                generateStatisticsForDateRange(dateRange, companyMgr.findCompaniesWithLicense(License.LICENSE_PROXIMITY));
//                generateUserProfile(companyMgr.findCompaniesWithLicense(License.LICENSE_PROXIMITY));
//            } catch (InterruptedException ex) {
//                logger.fatal(ex);
//            } catch (Exception err) {
//                logger.fatal("ReportsGenerator Exception", err);
//            }
//        }
    }
}
