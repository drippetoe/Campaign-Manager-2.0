/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "config_property", uniqueConstraints = @UniqueConstraint(columnNames = {"device_id", "prop_key", "prop_value"}))
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigProperty implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlTransient
    private Long id;
    @ManyToOne
    @XmlTransient
    private Device device;
    @ManyToOne
    @XmlTransient
    private CampaignType campaignType;
    @Size(max = 255)
    @Column(name = "prop_key")
     @XmlAttribute(name = "prop_key")
    private String propKey;
    @Size(max = 255)
    @Column(name = "prop_value")
    @XmlAttribute(name = "prop_value")
    private String propValue;
    @Column(name = "last_modified")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @XmlTransient
    private Date lastModified;
   

    public ConfigProperty() {
        id = 0L;
        
    }
    public ConfigProperty(String key, String value)
    {
        id = 0L;
        this.propKey = key;
        this.propValue = value;
    }

    
    public String getPropKey()
    {
        return propKey;
    }

    public void setPropKey(String key)
    {
        this.propKey = key;
    }

    public String getPropValue()
    {
        return propValue;
    }

    public void setPropValue(String value)
    {
        this.propValue = value;
    }

    public Date getLastModified()
    {
        return lastModified;
    }

    public void setLastModified(Date lastModified)
    {
        this.lastModified = lastModified;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Device getDevice()
    {
        return device;
    }

    public void setDevice(Device device)
    {
        this.device = device;
    }

    public CampaignType getCampaignType()
    {
        return campaignType;
    }

    public void setCampaignType(CampaignType campaignType)
    {
        this.campaignType = campaignType;
    }
    
    
    
    
    
    
}
