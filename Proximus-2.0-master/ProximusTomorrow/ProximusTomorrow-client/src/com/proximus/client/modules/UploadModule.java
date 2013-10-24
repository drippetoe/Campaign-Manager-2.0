/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.modules;

import com.proximus.client.RESTClient;
import com.proximus.data.LogUploadAction;
import com.proximus.util.client.ApacheRESTUtil;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Ronald Williams
 */
public class UploadModule extends Module
{
    RESTClient restClient;
    private static final Logger logger = Logger.getLogger(UploadModule.class.getName());

    @Override
    public void init()
    {
        try {
            restClient = new RESTClient(this.config.getConfig().getKeepAlive());
        } catch (Exception e) {
            restClient = new RESTClient(3000);
        }

    }

    /**
     * Check for each UploadAction how many files to upload to the server 
     * @param params 
     */
    @Override
    public void runTask(Object... params)
    {
        logger.debug("Running Action in UploadModule");
        for (Object obj : this.actions.getActions()) {
            if (obj instanceof LogUploadAction) {
                
                LogUploadAction action = (LogUploadAction) obj;
                //List all files inside the logs dir and upload them one by one
                File dir = new File(ClientURISettings.LOG_QUEUE);
                File[] files = dir.listFiles();
                if (!action.isAll()) {
                    logger.debug("got log upload action: "+action.getCount());
                    files = getMostRecent(files, action.getCount());
                }
                for (File file : files) {
                    logger.debug("uploading file "+file.getName());
                    uploadFile(file);
                }
            }
        }
    }

    /**
     * helper method to get only a subset of the files to upload
     * @param src the entire list
     * @param count the subset count
     * @return 
     */
    private File[] getMostRecent(File[] src, int count)
    {
        Arrays.sort(src, new Comparator<File>()
        {
            @Override
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });
        if (count > src.length) {
            return src;
        }
        File[] result = new File[count];
        System.arraycopy(src, 0, result, 0, result.length);
        return result;
    }

    /**
     * actual POST to upload the file to server
     * @param file 
     */
    private void uploadFile(File file)
    {
        try {
            URI uploadURI = new URI(ClientURISettings.uploadUri);
            HttpResponse response = this.restClient.POSTRequest(uploadURI, ApacheRESTUtil.CreateFileUploadEntity(file));
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                try {
                FileUtils.moveFileToDirectory(file, new File(ClientURISettings.LOG_COMPLETED), true);
                } catch(FileExistsException fee) {
                    logger.log(Priority.WARN, "File: " + file.getName() + " was already on completed folder");
                    FileUtils.deleteQuietly(file);
                }
            } else {
                //Leave the file where it is we will try again next time;
                logger.log(Priority.ERROR, "Couldn't upload log to the server... Server Status: " + response.getStatusLine().getStatusCode());
            }
            EntityUtils.consume(response.getEntity());
        } catch (URISyntaxException ex) {
            logger.log(Priority.ERROR, ex);
        } catch (IOException ex) {
            logger.log(Priority.ERROR, ex);
        }
    }
}
