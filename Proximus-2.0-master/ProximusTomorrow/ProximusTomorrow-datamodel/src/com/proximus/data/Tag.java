/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author eric johansson
 */
@Entity
@DiscriminatorValue("GENERIC")
@Table(name = "tag")
public class Tag implements Serializable {

    public static final String TAG_TYPE_CAMPAIGN = "campaign";
    public static final String TAG_TYPE_REPORT = "report";
    public static String CAMPAIGN_TAG = "campaign_tag";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "type")
    private String type;
    @OneToMany(mappedBy = "tag")
    private List<Device> devices;
    @ManyToMany(mappedBy = "tags")
    private List<Campaign> campaigns;
    @ManyToOne
    @Index
    private Company company;

    public Tag() {
        this.id = 0L;
        this.devices = new ArrayList<Device>();
        this.type = TAG_TYPE_CAMPAIGN;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public Integer getDeviceCount() {
        if (devices != null) {
            return devices.size();
        }
        return 0;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public void addDevice(Device d) {
        this.devices.add(d);
    }

    public void removeDevice(Device d) {
        this.devices.remove(d);
    }

    public void addCampaign(Campaign c) {
        this.campaigns.add(c);
    }

    public void removeCampaign(Campaign c) {
        this.campaigns.remove(c);
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public Integer getCampaignCount() {
        if (campaigns != null) {
            return campaigns.size();
        }
        return 0;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Tag other = (Tag) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.company != other.company && (this.company == null || !this.company.equals(other.company))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 89 * hash + (this.company != null ? this.company.hashCode() : 0);
        return hash;
    }
}
