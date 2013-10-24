/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.cron;

import com.proximus.client.config.ClientCampaign;
import com.proximus.client.config.SystemWriter;
import com.proximus.client.modules.ProcessExecutor;
import com.proximus.data.Campaign;
import com.proximus.data.HotspotDomain;
import com.proximus.data.WifiCampaign;
import com.proximus.util.TimeConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/**
 *
 * @author Gilberto Gaxiola
 */
public class WifiActiveCampaignCron implements StatefulJob
{
    private static final Logger logger = Logger.getLogger(WifiActiveCampaignCron.class.getName());
    //Map key to get the campaigns
    public static final String CAMPAIGN_KEY = WifiActiveCampaignCron.class.getName();
    //current active campaign
    public static Campaign currActiveCampaign = null;

    /**
     * Empty constructor needs to be defined for Quartz Scheduler
     */
    public WifiActiveCampaignCron()
    {
    }

    /**
     * This method will be triggered by ClientScheduler
     * No change will be done if the current active campaign is still under the date and time range
     * If it fails then It will find the pool of Active and valid campaigns and choose accordingly
     * By the most recent (campaign's lastModified)
     * This method ONLY CHECKS WIFI campaigns
     * @param context
     * @throws JobExecutionException 
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        ClientCampaign clientCampaign = (ClientCampaign) data.get(WifiActiveCampaignCron.CAMPAIGN_KEY);
        if (clientCampaign == null) {
            ProcessExecutor.LED(1, false);
            logger.info("No ClientCampaign found can't choose active campaign");
            return;
        }
        //Checking if current active campaign is still valid
        if (isCurrentCampaignValid(clientCampaign)) {
            if(ProcessExecutor.statusLighttpd().contains("false")) {
                logger.log(Priority.DEBUG, "Lighttpd was not running... starting it again "); 
                ProcessExecutor.startLighttpd();
            }
            return;
        }
        
        
        ProcessExecutor.stopLighttpd();
        //Current Campaign is not valid anymore..
        //Checking the pool of candidate campaigns that may be active
        List<Campaign> poolOfActive = new ArrayList<Campaign>();
        List<Campaign> list = clientCampaign.getCampaigns("WIFI");

        for (Campaign c : list) {
            if (this.isWithinTimeAndRange(c)) {
                poolOfActive.add(c);
            }
        }
        String activeId = "";
        long lastModified = System.currentTimeMillis();
        Campaign activeCamp = new Campaign();
        //choosing the most recently modified campaign to be the active
        for (Campaign c : poolOfActive) {
            if (c.getLastModified().getTime() < lastModified) {
                activeId = c.getId() + "";
                lastModified = c.getLastModified().getTime();
                activeCamp = c;
            }
        }



        if (activeId.isEmpty()) {
            if(currActiveCampaign != null) {
                ProcessExecutor.LED(1, false);
                logger.log(Priority.WARN, "No Active Campaign's found defaulting to proximus index page");
                currActiveCampaign = null;  
                SystemWriter.makeDefaultIndexPage();
                SystemWriter.makeDefaultLighttpdConfFile();
                ProcessExecutor.changeSSID("\"Proximus Mobility\"");
                logger.log(Priority.DEBUG, "Changing SSID to Proximus-Mobility");
                logger.log(Priority.DEBUG, "Restarting Lighttpd");
                ProcessExecutor.restartLighttpd();
            }
            return;
        }

        currActiveCampaign = activeCamp;

        logger.log(Priority.DEBUG, "Changing webroot for campaign: " + currActiveCampaign.getName() + "-" + currActiveCampaign.getId());

        WifiCampaign campt = currActiveCampaign.getWifiCampaign();
        if (campt != null && campt.getNetworkName() != null && !campt.getNetworkName().isEmpty()) {
            ProcessExecutor.changeSSID("\""+campt.getNetworkName()+"\"");
            logger.log(Priority.DEBUG, "Changing SSID to: " + campt.getNetworkName());
        }
        SystemWriter.changeActiveCampaign(activeId);
        activateCaptivePortal();
        SystemWriter.touchWifiLogProperties();
        logger.log(Priority.DEBUG, "Wifi Log Properties now changed");
        ProcessExecutor.restartLighttpd();
        ProcessExecutor.LED(1, true);

    }

    /**
     * activate the captive portal settings based on the active Wi-Fi campaign
     */
    private void activateCaptivePortal()
    {

        if (currActiveCampaign == null) {
            return;
        }

        WifiCampaign wc = currActiveCampaign.getWifiCampaign();

        if (wc != null) {
            if (wc.getHotSpotMode() == WifiCampaign.NO_INTERNET) {
                ProcessExecutor.startCaptivePortal("closed");
            } else {
                if (wc.getHotSpotMode() == WifiCampaign.LIMITED) {
                    ProcessExecutor.startCaptivePortal("closed");

                    List<HotspotDomain> domains = wc.getHotspotDomains();
                    for (HotspotDomain domain : domains) {
                        ProcessExecutor.addClosedPortalDomain(domain.getDomainName());
                    }
                } else {
                    if (wc.getHotSpotMode() == WifiCampaign.HOTSPOT) {
                        ProcessExecutor.startCaptivePortal("open");
                    }
                }
            }
        }
    }

    /**
     * Checking if the current Campaign is still active and valid
     * @return 
     */
    private boolean isCurrentCampaignValid(ClientCampaign clientCampaign)
    {
        //We don't have an active Campaign or the current one is not part of this device
        if (currActiveCampaign == null || clientCampaign.getCampaign(currActiveCampaign.getId()) == null) {
            return false;
        }
        //Now checking if the current campaign is still in the date and time range
        Campaign fromServer = clientCampaign.getCampaign(currActiveCampaign.getId());

        if (this.isWithinTimeAndRange(fromServer)) {
            logger.log(Priority.DEBUG, "Current Active Campaign is still Valid NO NEED TO CHANGE IT");
            if(!fromServer.equals(currActiveCampaign)) {
                ProcessExecutor.restartLighttpd();
            }
            currActiveCampaign = fromServer;
            return true;
        }
        return false;

    }

    /**
     * helper method that will check Campaign's Date and Time Range vs now()
     * @param c
     * @return 
     */
    private boolean isWithinTimeAndRange(Campaign c)
    {
        Calendar cal = Calendar.getInstance();
        if (TimeConstants.dateWithinRange(cal.getTime(), c.getStartDate(), c.getEndDate())) {
            List<Integer> daysOfWeek = TimeConstants.getDaysOfWeek(c.getDayOfWeek());
            int currDay = cal.get(Calendar.DAY_OF_WEEK);
            for (int day : daysOfWeek) {
                if (day == currDay) {
                    String hour = "" + cal.get(Calendar.HOUR_OF_DAY);
                    String minute = "" + cal.get(Calendar.MINUTE);
                    String hourMinute = (hour.length() == 1 ? "0" + hour : hour) + ":" + (minute.length() == 1 ? "0" + minute : minute);
                    if (TimeConstants.inTimeRange(hourMinute, (c.getStartTime() + "-" + c.getEndTime()))) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
