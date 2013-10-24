/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.response;


import com.proximus.data.Actions;
import com.proximus.data.Campaigns;
import com.proximus.data.config.Config;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ejohansson
 */
@XmlRootElement(name = "serverResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerResponse
{
    private Config config;
    private Campaigns campaigns;
    private Actions actions;

    public ServerResponse()
    {
        this.config = new Config();
        this.campaigns = new Campaigns();
        this.actions = new Actions();
    }

    public Campaigns getCampaigns()
    {
        return campaigns;
    }

    public void setCampaigns(Campaigns camps)
    {
        this.campaigns = camps;
    }

    public Config getConfig()
    {
        return config;
    }

    public void setConfig(Config config)
    {
        this.config = config;
    }

    public Actions getActions()
    {
        return actions;
    }

    public void setActions(Actions actions)
    {
        this.actions = actions;
    }
    
    
    
}