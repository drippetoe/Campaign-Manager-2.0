/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.jdo.annotations.Index;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Index(members = {"date", "macAddress", "requestUrl"}, unique = "true")
@Table(name = "wifi_log")
public class WifiLog implements Serializable {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss z");
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @NotNull
    @Index
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @ManyToOne
    @Index
    private Company company;
    @ManyToOne
    @Index
    private Campaign campaign;
    @ManyToOne
    private Device device;
    @Size(max = 5)
    @Index
    @Column(name = "http_status")
    private String httpStatus;
    @Size(max = 255)
    @Column(name = "server_name")
    private String serverName;
    @Size(max = 255)
    @Column(name = "user_agent")
    private String userAgent;
    @Size(max = 255)
    @Column(name = "user_agent_parsed")
    private String userAgentParsed;
    @Index
    @Size(max = 255)
    @Column(name = "browser")
    private String browser;
    @Index
    @Size(max = 255)
    @Column(name = "browser_group")
    private String browserGroup;
    @Index
    @Size(max = 255)
    @Column(name = "browser_version")
    private String browserVersion;
    @Index
    @Size(max = 255)
    @Column(name = "os")
    private String operatingSystem;
    @Size(max = 255)
    @Column(name = "os_group")
    private String operatingSystemGroup;
    @Size(max = 255)
    @Index
    @Column(name = "mac_address")
    private String macAddress;
    @Size(max = 255)
    @Column(name = "version")
    private String version;
    @Size(max = 255)
    @Index
    @Column(name = "request_url")
    private String requestUrl;
    @Transient
    private Long records;

    public WifiLog() {
        this.id = 0L;
    }

    public Campaign getCampaign() {
        return campaign;
    }
    
    private String getCampaignName()
    {
        return ( campaign != null ) ? campaign.getName() : "";
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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        if (macAddress == null || macAddress.isEmpty()) {
            this.macAddress = null;
        } else {
            this.macAddress = macAddress.toUpperCase().replaceAll(":", "");
        }
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        if (this.userAgent.length() > 255) {
            this.userAgent = this.userAgent.substring(0, 254);
            System.out.println("THE USER AGENT IS GREATER THAN 255 characters chopping it....");
        }
    }

    public String getUserAgentParsed() {
        return userAgentParsed;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getBrowserGroup() {
        return browserGroup;
    }

    public void setBrowserGroup(String browserGroup) {
        this.browserGroup = browserGroup;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getOperatingSystemGroup() {
        return operatingSystemGroup;
    }

    public void setOperatingSystemGroup(String operatingSystemGroup) {
        this.operatingSystemGroup = operatingSystemGroup;
    }

    public void setUserAgentParsed(String userAgentParsed) {
        this.userAgentParsed = userAgentParsed;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
    
    private String getDeviceName()
    {
        return ( device != null ) ? device.getName() : "";
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        if (requestUrl.length() >= 255) {
            this.requestUrl = requestUrl.substring(0, 254);
        } else {
            this.requestUrl = requestUrl;
        }
    }

    public Long getRecords() {
        return records;
    }

    public void setRecords(Long records) {
        this.records = records;
    }

    public String toCVS() {
        String cvs = "";
        cvs += "\"" + WifiLog.dateFormat.format(this.eventDate) + "\",";
        cvs += "\"" + this.getHttpStatus() + "\",";
        cvs += "\"" + this.getMacAddress() + "\",";
        cvs += "\"" + this.getRequestUrl() + "\",";
        cvs += "\"" + this.getServerName() + "\",";
        cvs += "\"" + this.getUserAgent() + "\",";
        cvs += "\"" + this.getBrowser() + "\",";
        cvs += "\"" + this.getBrowserGroup() + "\",";
        cvs += "\"" + this.getBrowserVersion() + "\",";
        cvs += "\"" + this.getOperatingSystem() + "\",";
        cvs += "\"" + this.getOperatingSystemGroup() + "\",";
        cvs += "\"" + this.getCampaignName() + "\",";
        cvs += "\"" + this.getDeviceName() + "\"";
        return cvs;
    }

    public static String getCVSHeader() {
        return "event_date, http_status, mac_address, request_url, server_name, user_agent, browser, browser_group, browser_version, os, os_group, campaign, proximus_device";
    }

    @Override
    public String toString() {
        String result = "WifiReport: Campaign Id: " + (this.campaign != null ? campaign.getId() : "unknown");
        result += " Mac Address: " + this.macAddress + " resource: " + this.serverName;
        result += " userAgent: " + this.userAgent + " httpStatus: " + this.httpStatus;
        return result;
    }
}
