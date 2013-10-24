/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import com.proximus.data.config.CampaignShort;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ejohansson
 */
@XmlRootElement(name = "campaigns")
@XmlAccessorType(XmlAccessType.FIELD)
public final class Campaigns
{

    @XmlElement(name = "campaign")
    private List<Campaign> campaigns;

    public Campaigns()
    {
        this.campaigns = new ArrayList<Campaign>();
    }

    public Campaigns(List<Campaign> campaigns)
    {
        this.campaigns = campaigns;
    }
    
    public void addCampaign(Campaign campaign){
        this.campaigns.add(campaign);
    }

    public List<Campaign> getCampaigns()
    {
        return campaigns;
    }
    
    public List<CampaignShort> getShortCampaigns() {
        
        List<CampaignShort> result = new ArrayList<CampaignShort>();
        for (Campaign c : campaigns) {
            result.add(new CampaignShort(c.getId(), c.getName(), c.getLastModified()));
        }
        return result;   
    }

    public void setCampaigns(List<Campaign> campaigns)
    {
        this.campaigns = campaigns;
    }
    
    public Campaign getCampaign(Long id) {
        for(Campaign c : campaigns) {
            if(c.getId() == id) {
                return c;
            }
        }
        return null;
    }
    
    
    
    
}
