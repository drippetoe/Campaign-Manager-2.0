/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Ronald
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "hot_spot_domain")
@XmlRootElement(name = "hotspot_domain")
@XmlAccessorType(XmlAccessType.FIELD)
public class HotspotDomain implements Serializable {

    @XmlTransient
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;
    @Size(max = 255)
    @Column(name = "domain_name")
    @XmlAttribute(name = "name")
    private String domainName;
    @ManyToOne
    @XmlTransient
    private CampaignType campaignType;

    public CampaignType getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(CampaignType campaignType) {
        this.campaignType = campaignType;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HotspotDomain)) {
            return false;
        }
        HotspotDomain hotSpotDomain = (HotspotDomain) o;
        if(this.id == hotSpotDomain.id) {
            if(this.domainName != null && hotSpotDomain.domainName != null && this.domainName.equals(hotSpotDomain.domainName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.domainName;
    }
}
