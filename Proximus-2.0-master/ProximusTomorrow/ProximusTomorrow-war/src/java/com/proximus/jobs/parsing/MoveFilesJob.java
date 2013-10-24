/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.jobs.parsing;

import com.proximus.util.ServerURISettings;
import java.io.File;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gilberto Gaxiola
 */
public class MoveFilesJob implements Runnable
{
    private Logger logger = Logger.getLogger(MoveFilesJob.class.getName());
    private final long THIRTY_SECONDS = 30000;
    private final long FIVE_MINUTES = 300000;
    private final long ONE_MINUTE = 60000;
    private final long TWO_MINUTES = 120000;
    private boolean isInit = false;

    @Override
    public void run()
    {
        try {
            Thread.sleep(THIRTY_SECONDS);
        } catch (InterruptedException ex) {
            logger.log(Priority.ERROR, ex);
        }
        while (true) {
            File logdir = new File(ServerURISettings.LOG_WORKING);
            if (logdir.exists() && logdir.isDirectory()) {
                File[] listFiles = logdir.listFiles();
                if (listFiles.length > 0) {
                    long currTime = new Date().getTime();
                    //Move folders older than certain period time
                    for (File file : listFiles) {
                        if (currTime - file.lastModified() > TWO_MINUTES) {
                            try {
                                File destFile = new File(ServerURISettings.LOG_QUEUE + "/" + file.getName());
                                File errorFile = new File(ServerURISettings.LOG_ERROR + "/" + file.getName());
                                
                                if ( !destFile.exists() )
                                    FileUtils.moveFile(file, destFile);
                                else
                                {
                                    logger.error("File " + destFile.getName() + " already exists, moving to " + errorFile.getAbsolutePath());
                                    
                                    if ( errorFile.exists() )
                                    {
                                        errorFile.delete();
                                    }
                                    FileUtils.moveFile(file, errorFile);
                                }
                            } catch(Exception e) {
                                logger.log(Priority.ERROR, e);
                            }
                        }
                    }
                }

            }
            try {
                Thread.sleep(ONE_MINUTE);
            } catch (InterruptedException ex) {
                logger.log(Priority.ERROR, ex);
            }
        }
    }
}
