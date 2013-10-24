/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proximus.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dshaw
 */
@Entity
@Index(members={"eventDate","macAddress", "device"}, unique="true")
@Table(name = "bluetooth_dwell")
public class BluetoothDwell {
    
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss z");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id = 0L;
    @NotNull
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @ManyToOne
    private Device device;
    @Column(name = "mac_address")
    private String macAddress;
    @Column (name = "friendly_name")
    private String friendlyName;
    @Column(name = "dwell_time")
    private Long dwellTimeMS;
    @ManyToOne
    @Index
    private Company company;
    @ManyToOne
    @Index
    private Campaign campaign;
    
    @Transient
    ArrayList<Date> timesSeen = new ArrayList<Date>();

    public void addSeen(Date d) {
        timesSeen.add(d);
    }

    public List<Date> getSeen() {
        return timesSeen;
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

    public Long getDwellTimeMS() {
        return dwellTimeMS;
    }

    public void setDwellTimeMS(Long dwellTimeMS) {
        this.dwellTimeMS = dwellTimeMS;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Campaign getCampaign()
    {
        return campaign;
    }

    public void setCampaign(Campaign campaign)
    {
        this.campaign = campaign;
    }

    public Company getCompany()
    {
        return company;
    }

    public void setCompany(Company company)
    {
        this.company = company;
    }
    
    
    public static String getCVSHeader()
    {
        return "event_date, mac_address, friendly_name, dwell_time, device, campaign, company";
    }

    public String toCVS()
    {
        String cvs = "";
        cvs += "\"" + BluetoothSend.dateFormat.format(this.eventDate) + "\",";
        cvs += "\"" + this.macAddress + "\",";
        cvs += "\"" + this.friendlyName + "\",";
        cvs += "\"" + this.dwellTimeMS + "\",";
         cvs += "\"" + (this.device != null?this.device.getName():"No Device") + "\",";
        cvs += "\"" + (this.campaign != null?this.campaign.getName():"No Campaign") + "\",";
        cvs += "\"" + (this.company != null?this.company.getName():"No Company") + "\"";
        return cvs;
    }
    
    public static String convertTime(long dwellMS) {
        long hours = TimeUnit.MILLISECONDS.toHours(dwellMS);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(dwellMS - TimeUnit.HOURS.toMillis(hours));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(dwellMS - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes));
        
        
        return String.format("%d hrs, %d min, %d sec",
                hours,
                minutes,
                seconds);
    }
    
    
    
    
}
