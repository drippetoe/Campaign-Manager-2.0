/**
 * Copyright (c) 2010-2013 Proximus Mobility LLC
 */
package com.proximus.startup.warehouse;

import com.proximus.data.*;
import com.proximus.data.report.BluetoothDaySummary;
import com.proximus.data.report.BluetoothFileSendSummary;
import com.proximus.data.report.UserProfileSummary;
import com.proximus.data.report.WifiDaySummary;
import com.proximus.manager.*;
import com.proximus.manager.report.WifiDaySummaryManagerLocal;
import java.util.*;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

/**
 *
 * @author Gilberto Gaxiola
 */
public class WifiDaySummaryCreator extends Thread {

    private static final Logger logger = Logger.getLogger(WifiDaySummaryCreator.class.getName());
    @EJB
    CompanyManagerLocal companyMgr;
    @EJB
    CampaignManagerLocal campaignMgr;
    @EJB
    DeviceManagerLocal deviceMgr;
    @EJB
    BluetoothReportManagerLocal btReportMgr;
    @EJB
    WifiLogManagerLocal wifiLogMgr;
    @EJB
    WifiRegistrationManagerLocal wifiRegMgr;
    @EJB
    UserProfileSummaryManagerLocal userProfileMgr;
    @EJB
    BluetoothDwellReportManagerLocal btDwellReportMgr;
    @EJB
    WifiDaySummaryManagerLocal wifiDaySummaryMgr;

    /**
     * Creates the previous day wifi summary into the warehouse
     */
    
    public void run() {
        while (true) {
            try {
                /*
                 * This thread runs for every companym and for every campaign
                 * generates one row per day of each type, describing the
                 * aggregate of that day's reports
                 */
                GregorianCalendar cal = new GregorianCalendar();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                Date today = cal.getTime();
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
                Date yesterday = cal.getTime();
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
                Date twoDaysAgo = cal.getTime();
                
                List<Date> dateRange = new ArrayList<Date>();
                dateRange.add(twoDaysAgo);
                dateRange.add(yesterday);
                dateRange.add(today);
                generateStatisticsForDateRange(dateRange, companyMgr.findCompaniesWithLicense(License.LICENSE_PROXIMITY));
                generateUserProfile(companyMgr.findCompaniesWithLicense(License.LICENSE_PROXIMITY));
                Thread.sleep(1000 * 60 * 60 * 1); // don't run continuously, 1 per hour might be good, or maybe sleeps between companies  
            } catch (Exception err) {
                if(err instanceof EJBException) {
                    logger.fatal("WE GOT US AN EJB EXCEPTION KILLING THIS THREAD");
                    return;
                }
                logger.fatal("ReportsGenerator Exception", err);
                try {
                    Thread.sleep(1000 * 60 * 1);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        }
    }

    public void generateStatisticsForDateRange(List<Date> dateRange, List<Company> allProximityCompanies) {

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

                                wifiSummary.setSuccessfulPageViews(wifiLogMgr.getSuccessfulPageViews(company, theDate, campaign, device));
                                wifiSummary.setTotalPageViews(wifiLogMgr.getTotalPageViews(company, theDate, campaign, device));
                                wifiSummary.setTotalRequests(wifiLogMgr.getTotalRequests(company, theDate, campaign, device));
                                wifiSummary.setUniqueUserCount(wifiLogMgr.getUniqueUserCount(company, theDate, campaign, device));
                                wifiDaySummaryMgr.create(wifiSummary);
                            } else {
                                wifiSummary.setSuccessfulPageViews(wifiLogMgr.getSuccessfulPageViews(company, theDate, campaign, device));
                                wifiSummary.setTotalPageViews(wifiLogMgr.getTotalPageViews(company, theDate, campaign, device));
                                wifiSummary.setTotalRequests(wifiLogMgr.getTotalRequests(company, theDate, campaign, device));
                                wifiSummary.setUniqueUserCount(wifiLogMgr.getUniqueUserCount(company, theDate, campaign, device));
                                wifiDaySummaryMgr.update(wifiSummary);
                            }

                            //logger.warn("Id: " + wifiSummary.getId() + " " + wifiSummary.toString());
                        }
                    }
                }
            }
        }
    }

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

    public BluetoothDwellReportManagerLocal getBtDwellReportMgr() {
        return btDwellReportMgr;
    }

    public void setBtDwellReportMgr(BluetoothDwellReportManagerLocal btDwellReportMgr) {
        this.btDwellReportMgr = btDwellReportMgr;
    }

    public BluetoothReportManagerLocal getBtReportMgr() {
        return btReportMgr;
    }

    public void setBtReportMgr(BluetoothReportManagerLocal btReportMgr) {
        this.btReportMgr = btReportMgr;
    }

    public CampaignManagerLocal getCampaignMgr() {
        return campaignMgr;
    }

    public void setCampaignMgr(CampaignManagerLocal campaignMgr) {
        this.campaignMgr = campaignMgr;
    }

    public CompanyManagerLocal getCompanyMgr() {
        return companyMgr;
    }

    public void setCompanyMgr(CompanyManagerLocal companyMgr) {
        this.companyMgr = companyMgr;
    }

    public DeviceManagerLocal getDeviceMgr() {
        return deviceMgr;
    }

    public void setDeviceMgr(DeviceManagerLocal deviceMgr) {
        this.deviceMgr = deviceMgr;
    }

    public UserProfileSummaryManagerLocal getUserProfileMgr() {
        return userProfileMgr;
    }

    public void setUserProfileMgr(UserProfileSummaryManagerLocal userProfileMgr) {
        this.userProfileMgr = userProfileMgr;
    }

    public WifiLogManagerLocal getWifiLogMgr() {
        return wifiLogMgr;
    }

    public void setWifiLogMgr(WifiLogManagerLocal wifiLogMgr) {
        this.wifiLogMgr = wifiLogMgr;
    }

    public WifiRegistrationManagerLocal getWifiRegMgr() {
        return wifiRegMgr;
    }

    public void setWifiRegMgr(WifiRegistrationManagerLocal wifiRegMgr) {
        this.wifiRegMgr = wifiRegMgr;
    }

    public WifiDaySummaryManagerLocal getWifiDaySummaryMgr() {
        return wifiDaySummaryMgr;
    }

    public void setWifiDaySummaryMgr(WifiDaySummaryManagerLocal wifiDaySummaryMgr) {
        this.wifiDaySummaryMgr = wifiDaySummaryMgr;
    }
    
    
}
