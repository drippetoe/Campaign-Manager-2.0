/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;


/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name="wifi_campaign")
@DiscriminatorValue("wifi")
@XmlRootElement(name = "wifi_campaign")
@XmlAccessorType(XmlAccessType.FIELD)
public class WifiCampaign extends CampaignType
{
    public static final int NO_INTERNET = 1;
    public static final int LIMITED = 2;
    public static final int HOTSPOT = 3;
    public static final int MOBILOZOPHY = 4;
    public static final int FACEBOOK  = 5;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "campaignType")
    @XmlElementWrapper(name = "hotspot_domains")
    @XmlElement(name = "domain")
    private List<HotspotDomain> hotspotDomains;
    @Column(name = "hot_spot_mode")
    @XmlAttribute(name = "hotspot_mode")
    private Integer hotSpotMode = 1;
    @Size(max = 255)
    @Column(name = "network_name")
    @XmlAttribute(name = "network_name")
    private String networkName;

    
    public WifiCampaign() {
        this.hotspotDomains = new ArrayList<HotspotDomain>();
        this.id = 0L;
        this.hotSpotMode = NO_INTERNET;
    }
    
    public List<HotspotDomain> getHotspotDomains()
    {
        if(hotspotDomains == null) {
            hotspotDomains = new ArrayList<HotspotDomain>();
        }
        return hotspotDomains;
    }

    public void setHotspotDomains(List<HotspotDomain> hotspotdomains)
    {
        this.hotspotDomains = hotspotdomains;
    }
    
    public void addHotspotDomain(HotspotDomain domain) {
        this.hotspotDomains.add(domain);
    }
    
    public int getHotSpotMode()
    {
        return hotSpotMode;
    }

    public void setHotSpotMode(int hotSpotMode)
    {
        this.hotSpotMode = hotSpotMode;
    }

    public String getNetworkName()
    {
        return networkName;
    }

    public void setNetworkName(String networkName)
    {
        this.networkName = networkName;
    }

    @Override
    public String getType() {
        return "wifi";
    }
}
