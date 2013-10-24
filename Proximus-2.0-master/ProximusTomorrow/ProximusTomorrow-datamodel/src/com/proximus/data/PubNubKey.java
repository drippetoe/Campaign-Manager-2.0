/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import com.proximus.data.sms.MobileOfferSettings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

/**
 *
 * @author dshaw
 */
@Entity
@Table(name = "pubnub_key")
@XmlRootElement(name = "pubNubKey")
@XmlAccessorType(XmlAccessType.FIELD)
public class PubNubKey implements Serializable {

    public static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlTransient
    private Long id;
    @Size(max = 255)
    @Column(name = "channel", length = 255)
    @XmlAttribute(name = "channel")
    private String channel;
    @Size(max = 64)
    @Column(name = "pubkey", length = 64)
    @XmlAttribute(name = "pubkey")
    private String publishKey;
    @Size(max = 64)
    @Column(name = "subkey", length = 64)
    @XmlAttribute(name = "subkey")
    private String subscribeKey;
    @Size(max = 64)
    @Column(name = "secret", length = 64)
    @XmlAttribute(name = "secret")
    private String secret;
    @ManyToOne
    @XmlTransient
    private Company company;
    @ManyToMany(mappedBy = "pubNubKeys")
    @XmlTransient
    private List<Campaign> campaigns;
    @ManyToMany
    @JoinTable(name = "pubnub_mobile_offer_settings", joinColumns =
    @JoinColumn(name = "pubnub_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "mobile_offer_settings_id", referencedColumnName = "id"))
    @XmlTransient
    private List<MobileOfferSettings> mobileOfferSettings;

    public PubNubKey() {
        this.id = 0L;
        campaigns = new ArrayList<Campaign>();
        mobileOfferSettings = new ArrayList<MobileOfferSettings>();
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Campaign> getCampaigns() {
        if(campaigns == null) {
            campaigns = new ArrayList<Campaign>();
        }
        return campaigns;
    }

    public void setCampaigns(List<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MobileOfferSettings> getMobileOfferSettings() {
        if(mobileOfferSettings == null) {
            mobileOfferSettings = new ArrayList<MobileOfferSettings>();
        }
        return mobileOfferSettings;
    }

    public void setMobileOfferSettings(List<MobileOfferSettings> mobileOfferSettings) {
        this.mobileOfferSettings = mobileOfferSettings;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

   

    public String getPublishKey() {
        return publishKey;
    }

    public void setPublishKey(String publishKey) {
        this.publishKey = publishKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getSubscribeKey() {
        return subscribeKey;
    }

    public void setSubscribeKey(String subscribeKey) {
        this.subscribeKey = subscribeKey;
    }

    @Override
    public String toString() {
        return this.channel;
    }
    
    public void addCampaign(Campaign c) {
        getCampaigns().add(c);
    }
    
    public void removeCampaign(Campaign c) {
        if(getCampaigns().contains(c)) {
            getCampaigns().remove(c);
        }
    }
    
     public void addMobileOfferSettings(MobileOfferSettings mos) {
        getMobileOfferSettings().add(mos);
    }
    
    public void removeMobileOfferSettings(MobileOfferSettings mos) {
        if(getMobileOfferSettings().contains(mos)) {
            getMobileOfferSettings().remove(mos);
        }
    }
}
