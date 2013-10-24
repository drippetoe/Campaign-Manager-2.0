/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "campaign_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING, length = 20)
@DiscriminatorValue("default")
@XmlRootElement(name = "campaigntype")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CampaignType implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlTransient
    protected Long id;
    @ManyToOne
    @XmlTransient
    protected Campaign campaign;
    @Size(max = 255)
    @Column(name = "checksum")
    @XmlAttribute(name = "checksum")
    protected String checksum;

    public CampaignType()
    {
        this.id = 0L;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public abstract String getType();

    public Campaign getCampaign()
    {
        return this.campaign;
    }

    public void setCampaign(Campaign campaign)
    {
        this.campaign = campaign;
    }
    
    public String getChecksum()
    {
        return checksum;
    }

    public void setChecksum(String checksum)
    {
        this.checksum = checksum;
    }

    
    
}
