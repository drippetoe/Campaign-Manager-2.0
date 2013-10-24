/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.cron;

import com.proximus.client.config.SystemWriter;
import com.proximus.client.modules.ProcessExecutor;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
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
public class LogCron implements StatefulJob
{

    private static final Logger logger = Logger.getLogger(LogCron.class.getName());
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        logger.debug("Inside LogCron.");
        JobDataMap data = context.getJobDetail().getJobDataMap();
        ProcessExecutor.stopLighttpd();
        SystemWriter.allowBluetoothLogging(false);
        Properties prop = new Properties();
        File workingFolder = new File(ClientURISettings.LOG_WORKING);
        File queueFolder = new File(ClientURISettings.LOG_QUEUE);
        File completedFolder = new File(ClientURISettings.LOG_COMPLETED);
        String[] logExtensions =
        {
            "log", "txt"
        };
        try
        {




            Collection<File> logFiles = FileUtils.listFiles(workingFolder, logExtensions, true);
            logger.debug("Found " + logFiles.size() + " log files in working.");
            for (File file : logFiles)
            {
                logger.debug("Moving log file " + file.getName());
                FileUtils.moveFileToDirectory(file, queueFolder, true);
            }

        } catch (IOException ex)
        {
            logger.error(ex);
        }
        logger.debug("Restarting services and logging.");
        SystemWriter.touchWifiLogProperties();
        SystemWriter.touchBluetoothLogProperties();
        SystemWriter.allowBluetoothLogging(true);
        ProcessExecutor.startLighttpd();

        try
        {
            Collection<File> logFiles = FileUtils.listFiles(completedFolder, logExtensions, true);
            for (File file : logFiles)
            {
                FileUtils.deleteQuietly(file);
            }
        } catch (Exception ex)
        {
            logger.error(ex);
        }
    }
}
