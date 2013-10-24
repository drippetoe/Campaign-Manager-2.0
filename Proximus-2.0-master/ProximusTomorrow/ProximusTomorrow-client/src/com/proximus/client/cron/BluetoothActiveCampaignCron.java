/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.cron;

import com.proximus.client.Main;
import com.proximus.client.bluetooth.dreamplug.BluetoothDeviceDiscovery;
import com.proximus.client.config.ClientCampaign;
import com.proximus.client.config.SystemWriter;
import com.proximus.client.modules.ProcessExecutor;
import com.proximus.data.BluetoothCampaign;
import com.proximus.data.Campaign;
import com.proximus.util.TimeConstants;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
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
public class BluetoothActiveCampaignCron implements StatefulJob {

    private static final Logger logger = Logger.getLogger(BluetoothActiveCampaignCron.class.getName());
    public static final String CAMPAIGN_KEY = BluetoothActiveCampaignCron.class.getName();
    public static final String BLUETOOTH_DEPLOY_KEY = BluetoothDeviceDiscovery.class.getCanonicalName();
    public static Campaign currActiveCampaign = null;

    public BluetoothActiveCampaignCron() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.debug("Debugging BluetoothActiveCampaignCron");
        JobDataMap data = context.getJobDetail().getJobDataMap();
        ClientCampaign clientCampaign = (ClientCampaign) data.get(BluetoothActiveCampaignCron.CAMPAIGN_KEY);
        if (clientCampaign == null) {
            logger.warn("No ClientCampaign found can't choose active campaign");
            String activeName = "Bluetooth Offers";
            String result = ProcessExecutor.setBluetoothName("\""+activeName+"\"");
            logger.debug("ProcessExecutor.setBluetoothName(\"" + activeName + "\"): " + result);
            return;
        }

        logger.debug("Checking bluetooth campaigns");
        //Going over all bluetooth campaings and checking which files are valid to server content
        List<Campaign> poolOfActive = new ArrayList<Campaign>();
        List<Campaign> list = clientCampaign.getCampaigns("bluetooth");

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
            logger.log(Priority.WARN, "No Active Bluetooth Campaign");
            currActiveCampaign = null;
            SystemWriter.setCurrBluetoothCampaign("noActive");
            return;
        }

        if(activeCamp.equals(currActiveCampaign)){
            logger.debug("No new active campaign. Serving campaign "+activeCamp+" with BTName: "+activeCamp.getBluetoothCampaign().getFriendlyName());
            return;
        }
            
        
        currActiveCampaign = activeCamp;
        logger.debug("Changing bluetooth campaign: " + currActiveCampaign.getName() + "-" + currActiveCampaign.getId());
        SystemWriter.setCurrBluetoothCampaign("" + currActiveCampaign.getId());
        BluetoothCampaign btc = currActiveCampaign.getBluetoothCampaign();
        if (btc != null) {
            String activeName = btc.getFriendlyName();
            if (activeName == null || activeName.isEmpty()) {
                activeName = "Bluetooth Offers";
            }
            String result = ProcessExecutor.setBluetoothName("\""+activeName+"\"");
            logger.debug("ProcessExecutor.setBluetoothName(\"" + activeName + "\"): " + result);
        }
        SystemWriter.storeBluetoothClearCache(true);
        SystemWriter.touchBluetoothLogProperties();
    }

    private boolean isWithinDateRange(Campaign c) {
        Calendar cal = Calendar.getInstance();
        return TimeConstants.dateWithinRange(cal.getTime(), c.getStartDate(), c.getEndDate());
    }

    private boolean isWithinTimeAndRange(Campaign c) {
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

   private static void writePropertyFile(long id){
       Properties properties = new Properties();
       try{
           properties.setProperty("active_campaign", String.valueOf(id));
           properties.store(new FileWriter(new File(ClientURISettings.BT_CAMPAIGN_PROPERTIES_FILE)), "Loggin current campaign");
       }catch(IOException ex){
           logger.error(ex);
       }
   }

    
}
