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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * This Entity will hold the history of previously assigned properties for each device
 * That way we can ensure configProperty to hold one and only one key-value unique pairing per device
 * @author Gilberto Gaxiola
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "config_property_history")
public class ConfigPropertyHistory implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    private Long device_Id;
    @Size(max = 255)
    @Column(name = "prop_key")
    private String propKey;
    @Size(max = 255)
    @Column(name = "prop_value")
    private String propValue;
    @Column(name = "last_modified")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastModified;
   

    public ConfigPropertyHistory() {
        id = 0L;
        
    }
    public ConfigPropertyHistory(String key, String value)
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

    public Long getDevice_Id()
    {
        return device_Id;
    }

    public void setDevice_Id(Long device_Id)
    {
        this.device_Id = device_Id;
    }

 
}
