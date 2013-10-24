/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "campaign")
@XmlRootElement(name = "campaign")
@XmlAccessorType(XmlAccessType.FIELD)
public class Campaign implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    @XmlAttribute(name = "id")
    protected Long id = 0L;
    @Size(max = 255)
    @Column(name = "name")
    @XmlAttribute(name = "name")
    protected String name;
    @Size(max = 255)
    @Column(name = "checksum")
    @XmlAttribute(name = "checksum")
    protected String checksum;
    @Basic(optional = false)
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    @XmlAttribute(name = "start_date")
    @XmlJavaTypeAdapter(DateFormatterAdapter.class)
    protected Date startDate;
    @Basic(optional = false)
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    @XmlAttribute(name = "end_date")
    @XmlJavaTypeAdapter(DateFormatterAdapter.class)
    protected Date endDate;
    @Size(max = 15)
    @Column(name = "days_of_week")
    @XmlAttribute(name = "days_of_week")
    protected String dayOfWeek;  //Comma separated values using one-letter acronym U,M,T,W,R,F,S (Sunday,Monday,Tuesday...etc)
    @Size(max = 10)
    @Column(name = "start_time")
    @XmlAttribute(name = "start_time")
    protected String startTime; //HH:mm
    @Size(max = 10)
    @Column(name = "end_time")
    @XmlAttribute(name = "end_time")
    protected String endTime; //HH:mm
    @Basic(optional = false)
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlAttribute(name = "last_modified")
    @XmlJavaTypeAdapter(DateFormatterAdapter.class)
    protected Date lastModified;
    @Basic(optional = false)
    @Column(name = "active")
    @XmlAttribute(name = "active")
    protected Boolean active = false;
    @Basic(optional = false)
    @Column(name = "deleted")
    @XmlAttribute(name = "deleted")
    protected Boolean deleted = false;
    @Column(name = "generated_content")
    @XmlAttribute(name = "generated_content")
    protected Boolean generatedContent = false;
    @ManyToOne
    @XmlTransient
    protected Company company;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "campaign")
    @XmlElementRefs({
        @XmlElementRef(name = "wifi_campaign", type = WifiCampaign.class),
        @XmlElementRef(name = "bluetooth_campaign", type = BluetoothCampaign.class)
    })
    private List<CampaignType> campaignTypes;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "campaign_tag", joinColumns =
    @JoinColumn(name = "campaign_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    @XmlTransient
    protected List<Tag> tags;
    @XmlTransient
    @Transient
    private SimpleDateFormat sdf;
    @ManyToMany
    @JoinTable(name = "pubnub_campaign", joinColumns =
    @JoinColumn(name = "campaign_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "pubnub_id", referencedColumnName = "id"))
    @XmlElementWrapper(name = "pubNubKeys")
    @XmlElement(name = "pubNubKey")
    private List<PubNubKey> pubNubKeys;

    public Campaign() {
        id = 0L;
        campaignTypes = new ArrayList<CampaignType>();
        tags = new ArrayList<Tag>();
        pubNubKeys = new ArrayList<PubNubKey>();
        startDate = new Date();
        // 4 weeks ahead by default
        endDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 28));
        startTime = "00:00";
        endTime = "23:59";
        deleted = false;
        sdf = new SimpleDateFormat("HH:mm");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag t) {
        this.tags.add(t);
    }

    public void removeTag(Tag t) {
        this.tags.remove(t);
    }

    public List<Device> getDevices() {
        List<Device> devices = new ArrayList<Device>();
        for (Tag tag : getTags()) {
            List<Device> tagDevices = tag.getDevices();
            for (Device device : tagDevices) {
                if (!devices.contains(device)) {
                    devices.add(device);
                }
            }

        }
        return devices;
    }

    @Override
    public String toString() {
        if (this.getCompany() == null) {
            return "[" + this.name + "(" + this.id + ") " + "]";
        }
        return "[" + this.name + "(" + this.id + ") " + this.getCompany().toString() + "]";
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name.replaceAll(",", "");
        } else {
            this.name = name;
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Check if a campaign has expired
     *
     * @return
     */
    public boolean isExpired() {
        if (this.endTime == null) {
            this.endTime = "23:59";
        }

        String[] end = this.endTime.split(":");
        Long endHour = Long.parseLong(end[0]) * (1000 * 60 * 60);
        Long endMin = Long.parseLong(end[1]) * (1000 * 60);
        Long thisEndTime = this.endDate.getTime() + endHour + endMin;

        return (new Date().getTime() - thisEndTime) > 0;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        hash += (this.name != null ? this.name.hashCode() : 0);
        hash += (this.dayOfWeek != null ? this.dayOfWeek.hashCode() : 0);
        hash += (this.endDate != null ? this.endDate.hashCode() : 0);
        hash += (this.endTime != null ? this.endTime.hashCode() : 0);
        hash += (this.lastModified != null ? this.lastModified.hashCode() : 0);
        hash += (this.startDate != null ? this.startDate.hashCode() : 0);
        hash += (this.startTime != null ? this.startTime.hashCode() : 0);
        return hash;
    }

    public List<CampaignType> getCampaignTypes() {
        return campaignTypes;
    }

    public void setCampaignTypes(List<CampaignType> campaignTypes) {
        this.campaignTypes = campaignTypes;
    }

    public void updateCampaignType(CampaignType ct) {
        List<CampaignType> cts = this.campaignTypes;
        for (CampaignType campaignType : cts) {
            if (campaignType.getId().equals(ct.getId())) {
                this.campaignTypes.remove(campaignType);
                this.campaignTypes.add(ct);
            }
        }
    }

    public void removeCampaignType(CampaignType ct) {
        if (this.campaignTypes.contains(ct)) {
            this.campaignTypes.remove(ct);
        }
    }

    public void addCampaignType(CampaignType ct) {
        //remove duplicate campaign type
        removeCampaignType(ct);
        this.campaignTypes.add(ct);
    }

    public WifiCampaign getWifiCampaign() {
        for (CampaignType ct : campaignTypes) {
            if (ct instanceof WifiCampaign) {
                return (WifiCampaign) ct;
            }
        }
        return null;
    }

    public BluetoothCampaign getBluetoothCampaign() {
        for (CampaignType ct : campaignTypes) {
            if (ct instanceof BluetoothCampaign) {
                return (BluetoothCampaign) ct;
            }
        }
        return null;
    }

    public CampaignType getCampaignType(String type) {
        for (CampaignType ct : this.campaignTypes) {
            if (ct.getType().equalsIgnoreCase(type)) {
                return ct;
            }
        }
        return null;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /*
     * Compare metadata
     */
    public String calculageMetaDataChecksum() {
        String tempCheck = "";
        tempCheck += this.id != null ? this.id : 0;
        tempCheck += this.name != null ? this.name : 0;
        tempCheck += this.dayOfWeek != null ? this.dayOfWeek : 0;
        tempCheck += this.endDate != null ? this.endDate.toString() : 0;
        tempCheck += this.endTime != null ? this.endTime : 0;
        tempCheck += this.lastModified != null ? this.lastModified.toString() : 0;
        tempCheck += this.startDate != null ? this.startDate.toString() : 0;
        tempCheck += this.startTime != null ? this.startTime : 0;
        String result = tempCheck.hashCode() % Long.MAX_VALUE + "";
        return result;
    }

    @Override
    public boolean equals(Object campObj) {
        Campaign camp;

        if (campObj instanceof Campaign) {
            camp = (Campaign) campObj;
        } else {
            return false;
        }
        return compareMetaDataChecksum(camp);
    }

    /**
     * Compare campaign's files
     *
     * @return
     */
    public String calculateFileChecksum() {
        String tempCheck = "";
        for (CampaignType ct : this.getCampaignTypes()) {
            tempCheck += ct.getChecksum();
        }
        return (tempCheck.hashCode() % Long.MAX_VALUE) + "";
    }

    /**
     * Only Metadata checksum
     *
     * @param c
     * @return
     */
    public boolean compareMetaDataChecksum(Campaign c) {
        return this.calculageMetaDataChecksum().compareTo(c.calculageMetaDataChecksum()) == 0;
    }

    /**
     * Only files checksum
     *
     * @param c
     * @return
     */
    public boolean compareFileChecksum(Campaign c) {
        return this.calculateFileChecksum().compareTo(c.calculateFileChecksum()) == 0;
    }

    /**
     * Both Metadata and Files Checksum
     *
     * @return
     */
    public String calculateChecksum() {
        String tempCheck = this.calculageMetaDataChecksum() + this.calculateFileChecksum();
        return (tempCheck.hashCode() % Long.MAX_VALUE) + "";
    }

    /**
     * Compare Files for metadata and files
     *
     * @return
     */
    public boolean comparecChecksum(Campaign c) {
        return compareFileChecksum(c) && compareMetaDataChecksum(c);
    }

    private void startTimeConvertFromDate(Date startDate) {
        if (startDate == null) {
            startTime = "00:00";
        } else {
            startTime = sdf.format(startDate);
        }
    }

    private void endTimeConvertFromDate(Date endDate) {
        if (endDate == null) {
            endTime = "23:59";
        } else {
            endTime = sdf.format(endDate);
        }
    }

    public Date getStartTimeDate() throws ParseException {
        if (startTime == null || startTime.isEmpty()) {
            return sdf.parse("00:00");
        } else {
            return sdf.parse(startTime);
        }
    }

    public Date getEndTimeDate() throws ParseException {
        if (endTime == null || endTime.isEmpty()) {
            return sdf.parse("23:59");
        } else {
            return sdf.parse(endTime);
        }
    }

    public void setEndTimeDate(Date endTimeDate) {
        this.endTimeConvertFromDate(endTimeDate);
    }

    public void setStartTimeDate(Date startTimeDate) {
        this.startTimeConvertFromDate(startTimeDate);
    }

    public Boolean getGeneratedContent() {
        return generatedContent;
    }

    public void setGeneratedContent(Boolean generatedContent) {
        this.generatedContent = generatedContent;
    }
    
    

    public List<PubNubKey> getPubNubKeys() {
        return pubNubKeys;
    }

    public void setPubNubKeys(List<PubNubKey> pubNubKeys) {
        this.pubNubKeys = pubNubKeys;
    }

    public void addPubNubKey(PubNubKey pbk) {
        this.pubNubKeys.add(pbk);
    }

    public void removePubNubKey(PubNubKey pbk) {
        this.pubNubKeys.remove(pbk);
    }

    private static class DateFormatterAdapter extends XmlAdapter<String, Date> {

        private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        @Override
        public Date unmarshal(final String v) throws Exception {
            return dateFormat.parse(v);
        }

        @Override
        public String marshal(final Date v) throws Exception {
            return dateFormat.format(v);
        }
    }
}
