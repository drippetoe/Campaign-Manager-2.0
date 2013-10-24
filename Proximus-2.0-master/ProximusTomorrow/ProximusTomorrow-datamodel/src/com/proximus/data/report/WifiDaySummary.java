/**
 * Copyright (c) 2010-2013 Proximus Mobility LLC
 */
package com.proximus.data.report;

import com.proximus.data.Campaign;
import com.proximus.data.Company;
import com.proximus.data.Device;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dshaw
 */
@Entity
@Table(name = "wifi_day_summary")
public class WifiDaySummary implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;
    @ManyToOne
    @Index
    private Company company;
    @ManyToOne
    @Index
    private Campaign campaign;
    @ManyToOne
    @Index
    private Device device;
    @Basic(optional = false)
    @Index
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date eventDate;
    @Column(name = "unique_users")
    private Long uniqueUserCount = 0L; // count of unique users
    @Column(name = "total_requests")
    private Long totalRequests = 0L; // count of all
    @Column(name = "total_page_views")
    private Long totalPageViews = 0L; // count of (html, htm, php) requests
    @Column(name = "successful_page_views")
    private Long successfulPageViews = 0L; // requests for (html, php) with http status 200
    
    public WifiDaySummary() {
        this.id = 0L;
    }

    public Double getAveragePageRequestsPerUser() {
        if ( totalPageViews == null || uniqueUserCount == null || uniqueUserCount == 0 )
            return 0D;
        return totalPageViews*1.0/uniqueUserCount;
    }
    
    public Double getAverageSuccessfulPageRequestsPerUser() {
        if ( successfulPageViews == null || uniqueUserCount == null || uniqueUserCount == 0 )
            return 0D;
        return successfulPageViews*1.0/uniqueUserCount;
    }

    public Double getAverageRequestsPerUser() {
        if ( totalRequests == null || uniqueUserCount == null || uniqueUserCount == 0 )
            return 0D;
        return totalRequests*1.0/uniqueUserCount;
    }
    
    public String getAveragePageRequestsPerUserStr()
    {
        NumberFormat percent = new DecimalFormat("0.0#");
        return percent.format(getAveragePageRequestsPerUser());
    }
    
    public String getAverageSuccessfulPageRequestsPerUserStr()
    {
        NumberFormat percent = new DecimalFormat("0.0#");
        return percent.format(getAverageSuccessfulPageRequestsPerUser());
    }
    
    public String getAverageRequestsPerUserStr()
    {
        NumberFormat percent = new DecimalFormat("0.0#");
        return percent.format(getAverageRequestsPerUser());
    }

    public Long getSuccessfulPageViews() {
        return successfulPageViews;
    }

    public void setSuccessfulPageViews(Long successfulPageViews) {
        this.successfulPageViews = successfulPageViews;
    }

    public Long getTotalPageViews() {
        return totalPageViews;
    }

    public void setTotalPageViews(Long totalPageViews) {
        this.totalPageViews = totalPageViews;
    }

    public Long getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(Long totalRequests) {
        this.totalRequests = totalRequests;
    }

    public Long getUniqueUserCount() {
        return uniqueUserCount;
    }

    public void setUniqueUserCount(Long uniqueUsers) {
        this.uniqueUserCount = uniqueUsers;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Event date: ").append(eventDate);
        sb.append("\n");
        if ( company != null )
        {
            sb.append("Company: ").append(company.getName());
            sb.append(" (").append(company.getId()).append(")");
            sb.append("\n");
        }
        if ( campaign != null )
        {
            sb.append("Campaign: ").append(campaign.getId());
            sb.append("\n");
        }
        sb.append("getUniqueUsers: ").append(getUniqueUserCount());
        sb.append("\n");
        sb.append("getTotalRequests: ").append(getTotalRequests());
        sb.append("\n");
        sb.append("getTotalPageViews: ").append(getTotalPageViews());
        sb.append("\n");
        sb.append("getSuccessfulPageViews: ").append(getSuccessfulPageViews());
        sb.append("\n");
        return sb.toString();
    }
    
}
