/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
@Table(name = "summary_bluetooth_daily")
public class BluetoothDaySummary implements Serializable {
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
    private Device device;
    @ManyToOne
    @Index
    private Campaign campaign;
    @Basic(optional = false)
    @Index
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @Column(name = "total_devices_seen")
    private Long totalDevicesSeen = 0L;
    @Column(name = "unique_devices_seen")
    private Long uniqueDevicesSeen = 0L;
    @Column(name = "unique_devices_supporting_bluetooth")
    private Long uniqueDevicesSupportingBluetooth = 0L;
    @Column(name = "unique_devices_accepting_push")
    private Long uniqueDevicesAcceptingPush = 0L;
    @Column(name = "unique_devices_downloading_content")
    private Long uniqueDevicesDownloadingContent = 0L;
    @Column(name = "total_content_downloads")
    private Long totalContentDownloads = 0L;

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

    public Long getTotalContentDownloads() {
        return totalContentDownloads;
    }

    public void setTotalContentDownloads(Long totalContentDownloads) {
        this.totalContentDownloads = totalContentDownloads;
    }

    public Long getTotalDevicesSeen() {
        return totalDevicesSeen;
    }

    public void setTotalDevicesSeen(Long totalDevicesSeen) {
        this.totalDevicesSeen = totalDevicesSeen;
    }

    public Long getUniqueDevicesAcceptingPush() {
        return uniqueDevicesAcceptingPush;
    }

    public void setUniqueDevicesAcceptingPush(Long uniqueDevicesAcceptingPush) {
        this.uniqueDevicesAcceptingPush = uniqueDevicesAcceptingPush;
    }

    public Long getUniqueDevicesDownloadingContent() {
        return uniqueDevicesDownloadingContent;
    }

    public void setUniqueDevicesDownloadingContent(Long uniqueDevicesDownloadingContent) {
        this.uniqueDevicesDownloadingContent = uniqueDevicesDownloadingContent;
    }

    public Long getUniqueDevicesSeen() {
        return uniqueDevicesSeen;
    }

    public void setUniqueDevicesSeen(Long uniqueDevicesSeen) {
        this.uniqueDevicesSeen = uniqueDevicesSeen;
    }

    public Long getUniqueDevicesSupportingBluetooth() {
        return uniqueDevicesSupportingBluetooth;
    }

    public void setUniqueDevicesSupportingBluetooth(Long uniqueDevicesSupportingBluetooth) {
        this.uniqueDevicesSupportingBluetooth = uniqueDevicesSupportingBluetooth;
    }
    
    public String getAcceptanceRate()
    {
        NumberFormat percent = new DecimalFormat("0.0#%");
        
        double val = 0;
        if ( getUniqueDevicesSeen() != 0 )
        {
            val = ( ( getUniqueDevicesAcceptingPush()* 1.0) /getUniqueDevicesSupportingBluetooth()  );
        }

        return percent.format(val);
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
        sb.append("getTotalDevicesSeen: ").append(getTotalDevicesSeen());
        sb.append("\n");
        sb.append("getUniqueDevicesSeen: ").append(getUniqueDevicesSeen());
        sb.append("\n");
        sb.append("getUniqueDevicesAcceptingPush: ").append(getUniqueDevicesAcceptingPush());
        sb.append("\n");
        sb.append("getUniqueDevicesDownloadingContent: ").append(getUniqueDevicesDownloadingContent());
        sb.append("\n");
        sb.append("getTotalContentDownloads: ").append(getTotalContentDownloads());
        sb.append("\n");
        return sb.toString();
    }

    
}
