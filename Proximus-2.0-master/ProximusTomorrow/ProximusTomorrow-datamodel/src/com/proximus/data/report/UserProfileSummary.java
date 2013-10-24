/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.report;

import com.proximus.data.Company;
import com.proximus.data.Device;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ronald
 */
@Entity
@Index(members = {"phone", "macAddress", "firstSeen", "lastSeen", "company", "device"}, unique = "true")
@Table(name = "user_profile_summary")
public class UserProfileSummary implements Serializable {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss z");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;
    @Index
    @Column(name = "friendly_name")
    private String friendlyName;
    @Basic(optional = false)
    @NotNull
    @Index
    @Column(name = "mac_address")
    private String macAddress;
    @NotNull
    @Column(name = "first_seen")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date firstSeen;
    @NotNull
    @Column(name = "last_seen")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastSeen;
    @Column(name = "mac_count")
    private Long macCount = 0L;
    @Column(name = "dwell_time")
    private Long dwellTime = 0L;
    @ManyToOne
    @Index
    private Company company;
    @ManyToOne
    protected Device device;

    public static String formatTimestampForWeb(Date date) {
        final String TIMESTAMP_FMT = "dd-MMMM-yyyy HH:mm:ss";
        return new SimpleDateFormat(TIMESTAMP_FMT).format(date);
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Date firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Long getMacCount() {
        return macCount;
    }

    public void setMacCount(Long macCount) {
        this.macCount = macCount;
    }

    public Long getDwellTime() {
        return dwellTime;
    }

    public void setDwellTime(Long dwellTime) {
        this.dwellTime = dwellTime;
    }

    public void addToMacCount(long count) {
        this.macCount += count;
    }

    public void addToDwellTime(long dwellTime) {
        this.dwellTime += dwellTime;
    }

    public String getFirstSeenFmt() {
        if (this.firstSeen == null) {
            return "Never";
        } else {
            return UserProfileSummary.formatTimestampForWeb(firstSeen);
        }

    }
    
    public Double getAverageDwellTime() {
        return (double)this.dwellTime/(double)this.macCount;
    }

    public String getLastSeenFmt() {
        if (this.lastSeen == null) {
            return "Never";
        } else {
            return UserProfileSummary.formatTimestampForWeb(lastSeen);
        }
    }

    @Override
    public String toString() {
        return "UserProfileSummary{" + "friendlyName=" + friendlyName + ", macAddress=" + macAddress + ", firstSeen=" + firstSeen
                + ", lastSeen=" + lastSeen + ", macCount=" + macCount + '}';
    }
}