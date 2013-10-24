/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.client.config;

import com.proximus.client.modules.Module;
import com.proximus.data.Campaign;
import com.proximus.data.Campaigns;
import com.proximus.data.config.CampaignShort;
import com.proximus.util.JAXBUtil;
import com.proximus.util.client.ClientURISettings;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 *
 * @author Gilberto Gaxiola
 */
public class ClientCampaign extends Observable {

    private static final Logger logger = Logger.getLogger(ClientCampaign.class.getName());
    Campaigns campaigns;
    List<Module> modules;

    public ClientCampaign() {
        modules = new ArrayList<Module>();
        new File(ClientURISettings.CONFIG_ROOT).mkdirs();
        this.campaigns = new Campaigns();
        File clientCampaigns = new File(ClientURISettings.CAMPAIGNS_FILE);
        if (!clientCampaigns.exists() && !clientCampaigns.isFile()) {
            try {
                logger.log(Priority.DEBUG, "File does not exist. Creating Campaigns File!");
                JAXBUtil.saveToFile(Campaigns.class, this.campaigns, ClientURISettings.CAMPAIGNS_FILE);
            } catch (Exception ex) {
                logger.log(Priority.ERROR, ex);
            }
        }
    }

    private ClientCampaign(Campaigns campaigns) {
        modules = new ArrayList<Module>();
        new File(ClientURISettings.CONFIG_ROOT).mkdirs();
        this.campaigns = campaigns;
        File clientCampaigns = new File(ClientURISettings.CAMPAIGNS_FILE);
        if (!clientCampaigns.exists() && !clientCampaigns.isFile()) {
            try {
                
                logger.log(Priority.INFO, "File does not exist. Creating Campaigns File!");
                JAXBUtil.saveToFile(Campaigns.class, this.campaigns, ClientURISettings.CAMPAIGNS_FILE);
            } catch (Exception ex) {
                logger.log(Priority.ERROR, ex);
            }
        }
    }

    public List<Campaign> getCampaigns() {
        return campaigns.getCampaigns();
    }
    
    
    public List<CampaignShort> getShortCampaigns() {
        return campaigns.getShortCampaigns();
    }

    public List<Campaign> getCampaigns(String type) {

        List<Campaign> typeCampaigns = new ArrayList<Campaign>();
        for (Campaign campaign : campaigns.getCampaigns()) {
            if(campaign.getCampaignType(type) != null) {
                typeCampaigns.add(campaign);
            }
        }
        return typeCampaigns;
    }

    public Campaign getCampaign(Long id) {
        return campaigns.getCampaign(id);
    }

    public void setCamps(Campaigns campaigns) {
        this.campaigns = campaigns;
    }

    public void addModule(Module module) {
        this.addObserver(module);
        modules.add(module);
    }

    public void addModules(List<Module> modsList) {
        for (Module module : modsList) {
            this.addObserver(module);
            modules.add(module);
        }
    }

    public void initModules() {
        for (Module module : modules) {
            module.init();
        }
    }

    public void loadConfiguration() {
        try {
            logger.log(Priority.INFO, "Loading Campaigns from:" + ClientURISettings.CAMPAIGNS_FILE);
            File clientCampaigns = new File(ClientURISettings.CAMPAIGNS_FILE);
            if (clientCampaigns.exists() && clientCampaigns.isFile()) {
                this.campaigns = JAXBUtil.loadFromFile(Campaigns.class, ClientURISettings.CAMPAIGNS_FILE);
                setChanged();
                notifyObservers(this.clone());
            } else {
                logger.log(Priority.INFO, "File does not exist. Creating Campaigns File!");
                JAXBUtil.saveToFile(Campaigns.class, this.campaigns, ClientURISettings.CAMPAIGNS_FILE);
            }
        } catch (Exception ex) {
            logger.log(Priority.ERROR, ex);

        }
    }

    public void saveConfiguration() {
        try {
            JAXBUtil.saveToFile(Campaigns.class, this.campaigns, ClientURISettings.CAMPAIGNS_FILE);
            setChanged();
            notifyObservers(this.clone());
        } catch (Exception ex) {
            logger.log(Priority.ERROR, ex);
         
        }
    }

    @Override
    protected Object clone() {
        return new ClientCampaign(this.campaigns);
    }
}
