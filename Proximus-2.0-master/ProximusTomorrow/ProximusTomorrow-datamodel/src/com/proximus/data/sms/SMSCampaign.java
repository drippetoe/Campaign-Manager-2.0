/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Company;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "sms_campaign")
public class SMSCampaign implements Serializable {

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
    @Column(name = "message")
    private String message;
    @Basic(optional = false)
    @Column(name = "keyword")
    private String keyword;
    @Basic(optional = false)
    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Basic(optional = false)
    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Size(max = 15)
    @Column(name = "days_of_week")
    private String dayOfWeek;  //Comma separated values using one-letter acronym U,M,T,W,R,F,S (Sunday,Monday,Tuesday...etc)
    @Size(max = 10)
    @Column(name = "start_time")
    private String startTime; //HH:mm
    @Size(max = 10)
    @Column(name = "end_time")
    private String endTime; //HH:mm
    @Basic(optional = false)
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;
    @Basic(optional = false)
    @Column(name = "active")
    private Boolean active = false;
    @Basic(optional = false)
    @Column(name = "deleted")
    private Boolean deleted = false;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "sms_campaign_geofence", joinColumns =
    @JoinColumn(name = "sms_campaign_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "geofence_id", referencedColumnName = "id"))
    private List<GeoFence> geoFences;
    
    @ManyToOne
    private Company company;
    
    @Transient
    private SimpleDateFormat sdf;

    public SMSCampaign() {
        id = 0L;
        geoFences = new ArrayList<GeoFence>();
        startDate = new Date();
        // 4 weeks ahead by default
        endDate = new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 28));
        startTime = "00:00";
        endTime = "23:59";
        deleted = false;
        sdf = new SimpleDateFormat("HH:mm");
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    public List<GeoFence> getGeoFences() {
        return geoFences;
    }
   public void addGeoFence(GeoFence g) {
        this.geoFences.add(g);
    }

    public void removeGeoFence(GeoFence g) {
        this.geoFences.remove(g);
    }

    public void setGeoFences(List<GeoFence> geoFences) {
        this.geoFences = geoFences;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
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
        this.name = name;
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

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTimeDate(Date endTimeDate) {
        this.endTimeConvertFromDate(endTimeDate);
    }

    public void setStartTimeDate(Date startTimeDate) {
        this.startTimeConvertFromDate(startTimeDate);
    }

    @Override
    public String toString() {
        return this.name;
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
        long finalTime = this.endDate.getTime() + endHour + endMin;

        return (new Date().getTime() - finalTime) > 0;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SMSCampaign other = (SMSCampaign) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.startDate != other.startDate && (this.startDate == null || !this.startDate.equals(other.startDate))) {
            return false;
        }
        if (this.endDate != other.endDate && (this.endDate == null || !this.endDate.equals(other.endDate))) {
            return false;
        }
        if ((this.dayOfWeek == null) ? (other.dayOfWeek != null) : !this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        if ((this.startTime == null) ? (other.startTime != null) : !this.startTime.equals(other.startTime)) {
            return false;
        }
        if ((this.endTime == null) ? (other.endTime != null) : !this.endTime.equals(other.endTime)) {
            return false;
        }
        if (this.lastModified != other.lastModified && (this.lastModified == null || !this.lastModified.equals(other.lastModified))) {
            return false;
        }
        return true;
    }
}
