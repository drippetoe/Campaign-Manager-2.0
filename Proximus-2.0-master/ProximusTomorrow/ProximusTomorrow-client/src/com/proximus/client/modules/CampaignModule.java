/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.modules;

import com.proximus.client.RESTClient;
import com.proximus.data.Campaign;
import com.proximus.data.CampaignType;
import com.proximus.util.ZipUnzip;
import com.proximus.util.client.ApacheRESTUtil;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * 
 * @author Gilberto Gaxiola
 */
public class CampaignModule extends Module
{
    private static final Logger logger = Logger.getLogger(CampaignModule.class.getName());
    private boolean isInit = false;
    private RESTClient restClient;
    Collection<Campaign> current = new ArrayList<Campaign>();
    HashMap<Long, Campaign> currentMap = new HashMap<Long, Campaign>();
    
    /**
     * This method will load the campaigns from
     * its campaign config file
     * if the files do not exist it will download them
     */
    @Override
    public void init()
    {
        restClient = new RESTClient(30000);
        current = new ArrayList<Campaign>();
        if (this.clientCampaign != null) {
            List<Campaign> camps = this.clientCampaign.getCampaigns();
            for (Campaign c : camps) {
                //Put campaign in hash map
                //Check if it exists in filesystem
                //Download if it doesn't            
                current.add(c);
                currentMap.put(c.getId(), c);
                checkFileSystemIntegrity(c);
            }
        }
        isInit = true;
    }

    @Override
    public void runTask(Object... params)
    {
        if (isInit) {
            new File(ClientURISettings.CAMPAIGNS_ROOT_DIR).mkdirs();
            //TO REMOVE subtract(local, remote)
            Collection<Campaign> toRemove = CollectionUtils.subtract(current, this.clientCampaign.getCampaigns());
            for (Campaign campaign : toRemove) {
                String dirPath = ClientURISettings.CAMPAIGNS_ROOT_DIR + "/" + campaign.getId();
                try {
                    FileUtils.deleteDirectory(new File(dirPath));
                } catch (IOException ex) {
                    logger.log(Priority.ERROR, ex);
                }

            }
            
            //TO ADD subtract(remote, local)
            Collection<Campaign> toAdd = CollectionUtils.subtract(this.clientCampaign.getCampaigns(), current);
            for (Campaign campaign : toAdd) {
                List<CampaignType> campTypes = campaign.getCampaignTypes();
                for (CampaignType ct : campTypes) {
                    this.downloadFile(campaign, ct);
                }
            }            
            //SAME
            List<Campaign> fromServer = this.clientCampaign.getCampaigns();
            //Although campaign metadata may be the same.... file checksum could have changed
            //This collection we hold the elements from the Server and we will check for FileSystemIntegrity
            Collection<Campaign> same = CollectionUtils.intersection(fromServer, current);
            for(Campaign s : same) {
                checkFileSystemIntegrity(s);
            }
            
            //SAME colletion + ADD collection
            current.clear();
            currentMap.clear();
            for(Campaign c : same) {
                current.add(c);
                currentMap.put(c.getId(), c);
            }          
            for(Campaign c : toAdd) {
                current.add(c);
                currentMap.put(c.getId(), c);
            }

        }
    }

    /**
     * Check the FileSystemIntegrity
     * @param c 
     */
    public void checkFileSystemIntegrity(Campaign c)
    {
        List<CampaignType> campTypes = c.getCampaignTypes();
        for (CampaignType ct : campTypes) {
            String name = "camp" + c.getId();
            if (c.getName() != null) {
                name = c.getName();
            }
            String filename = c.getId() + "/" + ct.getType() + "/" + name.replaceAll("[^0-9a-zA-Z]", "") + ".zip";
            File f = new File(ClientURISettings.CAMPAIGNS_ROOT_DIR + "/" + filename);
            if (!f.exists()) {
                File parent = new File(f.getParent());
                if (parent.exists() && (parent.listFiles().length > 0)) {
                    for (File file : parent.listFiles()) {
                        file.delete();
                    }
                }
                this.downloadFile(c, ct);
            } else {
                //Check if same metadata
                Campaign toCheck = currentMap.get(c.getId());
                if(!toCheck.compareFileChecksum(c)) {
                    try {
                        FileUtils.deleteDirectory(new File(f.getParent()));
                    } catch (IOException ex) {
                        logger.log(Priority.ERROR, ex);
                    }
                    this.downloadFile(c, ct);
                }
            }
        } //end for loop
    } //end method

    
    
    
    /**
     * Call download api
     * @param c
     * @param ct 
     */
    public void downloadFile(Campaign c, CampaignType ct)
    {

        URI downloadUri;
        HttpResponse response;
        try {
            System.out.println("camp id: " + c.getId());
            System.out.println("camp name: " + c.getName());
            String name = "camp" + c.getId();
            if (c.getName() != null) {
                name = c.getName();
            }

            String filename = c.getId() + "/" + ct.getType() + "/" + name.replaceAll("[^0-9a-zA-Z]", "") + ".zip";
            downloadUri = new URI(ClientURISettings.downloadUri + "/" + filename);
            response = this.restClient.GETRequest(downloadUri);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.log(Priority.ERROR, "Couldn't download the files");
                logger.log(Priority.ERROR, "Response Status Code: " + response.getStatusLine());
                EntityUtils.consume(response.getEntity());
                return;
            }
            File file = new File(ClientURISettings.CAMPAIGNS_ROOT_DIR + "/" + filename);
            ApacheRESTUtil.downloadFile(response, file);
            if (FilenameUtils.getExtension(filename).equalsIgnoreCase("zip")) {
                ZipUnzip.Unzip(file.getParent(), file.getName());
            }
            EntityUtils.consume(response.getEntity());
        } catch (Exception ex) {
            logger.log(Priority.ERROR, ex);
        } 
    }
}
