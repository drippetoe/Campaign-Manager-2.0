/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author eric
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "campaign_file")
public class CampaignFile implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlAttribute(name = "id")
    private Long id;
    @OneToOne
    private CampaignType campaignType;
    @Size(max = 255)
    @Column(name = "server_path")
    private String serverPath;

    public CampaignFile() {
        id=0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CampaignType getCampaignType() {
        return campaignType;
    }

    public void setCampaignType(CampaignType campaignType) {
        this.campaignType = campaignType;
    }

    public Campaign getCampaign() {
        return this.campaignType.getCampaign();
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
}