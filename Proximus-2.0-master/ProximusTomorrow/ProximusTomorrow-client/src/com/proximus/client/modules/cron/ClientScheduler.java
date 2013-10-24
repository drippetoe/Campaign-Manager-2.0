/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.modules.cron;

import com.proximus.client.bluetooth.dreamplug.BluetoothDeviceDiscovery;
import com.proximus.client.cron.BluetoothActiveCampaignCron;
import com.proximus.client.cron.LogCron;
import com.proximus.client.cron.WifiActiveCampaignCron;
import com.proximus.client.modules.Module;
import java.text.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ClientScheduler extends Module
{
    private static final Logger logger = Logger.getLogger(ClientScheduler.class.getName());
    //CRON TIME CONSTANTS
    public static final String FIFTEEN_SECONDS = "0/15 * * * * ?";
    public static final String THIRTY_SECONDS = "0/30 * * * * ?";
    public static final String ONE_MINUTE = "0 0/1 * * * ?";
    public static final String FIVE_MINUTES = "0 0/5 * * * ?";
    //WIFI SCHED
    private static final String JOB_WIFI_ACTIVE = "wifiActiveJob";
    private static final String GROUP_WIFI = "groupWifi";
    //BT SCHED
    private static final String JOB_BLUETOOTH_ACTIVE = "BTActiveJob";
    private static final String GROUP_BLUETOOTH = "groupBT";
    //LOG SCHED
    private static final String JOB_LOG_ACTIVE = "LogActiveJob";
    private static final String GROUP_LOG = "groupLog";
    //TRIGGER NAMES
    private static final String CRON_WIFI_TRIGGER = "cronWifi";
    private static final String CRON_BT_TRIGGER = "cronBT";
    private static final String CRON_LOG_TRIGGER = "cronLog";
    private static final String GROUP_TRIGGER = "groupTrigger";
    private SchedulerFactory sf = new StdSchedulerFactory();
    private boolean isInit = false;
    private boolean isLogInit = false;
    private Scheduler sched;

    public ClientScheduler()
    {

    }

    /**
     * Starting the Scheduler with the specific cron time
     * @param cron 
     */
    public void run(String cron)
    {
        if (isInit) {
            try {
                sched = sf.getScheduler();
                //Wifi Job
                JobDetail wifiActiveCampaignJobDetail = new JobDetail(JOB_WIFI_ACTIVE, GROUP_WIFI, WifiActiveCampaignCron.class);
                wifiActiveCampaignJobDetail.getJobDataMap().put(WifiActiveCampaignCron.CAMPAIGN_KEY, this.clientCampaign);
                CronTrigger ct = new CronTrigger(CRON_WIFI_TRIGGER, GROUP_TRIGGER, cron);
                sched.scheduleJob(wifiActiveCampaignJobDetail, ct);

                //bluetooth Job
                JobDetail bluetoothActiveCampaignJobDetail = new JobDetail(JOB_BLUETOOTH_ACTIVE, GROUP_BLUETOOTH, BluetoothActiveCampaignCron.class);
                bluetoothActiveCampaignJobDetail.getJobDataMap().put(BluetoothActiveCampaignCron.CAMPAIGN_KEY, this.clientCampaign);
                CronTrigger btct = new CronTrigger(CRON_BT_TRIGGER, GROUP_TRIGGER, cron);
                sched.scheduleJob(bluetoothActiveCampaignJobDetail, btct);
                sched.start();
            } catch (Exception ex) {
                logger.log(Priority.FATAL, "Error initalizing Scheduler on ClientScheduler: " + ex);
            }
        }
    }

    public void stop() throws SchedulerException
    {
        if (!sched.isShutdown()) {
            sched.shutdown();
        }
    }

    @Override
    public void runTask(Object... params)
    {
        if (isInit && this.clientCampaign != null && sched != null) {
            try {
                JobDetail jd;
                jd = sched.getJobDetail(JOB_WIFI_ACTIVE, GROUP_WIFI);
                jd.getJobDataMap().put(WifiActiveCampaignCron.CAMPAIGN_KEY, this.clientCampaign);
                JobDetail btjd = sched.getJobDetail(JOB_BLUETOOTH_ACTIVE, GROUP_BLUETOOTH);
                btjd.getJobDataMap().put(BluetoothActiveCampaignCron.CAMPAIGN_KEY, this.clientCampaign);
                sched.addJob(jd, true);
                sched.addJob(btjd, true);
            } catch (SchedulerException ex) {
                logger.log(Priority.ERROR, ex);
            }
        }
    }

    @Override
    public void init()
    {
        isInit = true;
        logger.log(Priority.DEBUG, "Client Scheduler Initialized!");
    }

    public void runLog(String cron)
    {
        logger.debug("initalizing LogCron");
        if (!isLogInit) {
            if (sched != null) {
                try {
                    //logging Job
                    JobDetail logJobDetail = new JobDetail(JOB_LOG_ACTIVE, GROUP_LOG, LogCron.class);
                    CronTrigger logct = new CronTrigger(CRON_LOG_TRIGGER, GROUP_LOG, cron);
                    sched.scheduleJob(logJobDetail, logct);
                    isLogInit = true;
                } catch (SchedulerException ex) {
                    logger.log(Priority.ERROR, ex);
                } catch (ParseException ex) {
                    logger.log(Priority.ERROR, ex);
                }
            }
        } else {
            try {
                if (sched != null) {
                    JobDetail logJd = sched.getJobDetail(JOB_LOG_ACTIVE, GROUP_LOG);
                    CronTrigger logct = (CronTrigger) sched.getTrigger(CRON_LOG_TRIGGER, GROUP_LOG);
                    //If the cron expenssions hasn't change just add the job
                    if (cron.equals(logct.getCronExpression())) {
                        sched.addJob(logJd, true);
                    } else {
                        logct.setCronExpression(cron);
                        sched.deleteJob(JOB_LOG_ACTIVE, GROUP_LOG);
                        sched.scheduleJob(logJd, logct);
                    }
                }
            } catch (ParseException ex) {
                logger.log(Priority.ERROR, ex);
            } catch (SchedulerException ex) {
                logger.log(Priority.ERROR, ex);
            }
        }
    }

}
