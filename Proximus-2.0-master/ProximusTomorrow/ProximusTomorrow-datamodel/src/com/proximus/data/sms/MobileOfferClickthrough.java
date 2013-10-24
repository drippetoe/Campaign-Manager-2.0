/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "mobile_offer_clickthrough")
public class MobileOfferClickthrough implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @ManyToOne
    private MobileOffer mobileOffer;
    @ManyToOne
    private Property property;
    @Size(max = 5)
    @Column(name = "http_status")
    private String httpStatus;
    @Size(max = 255)
    @Column(name = "server_name")
    private String serverName;
    @Size(max = 255)
    @Column(name = "user_agent")
    private String userAgent;
    @Size(max = 255)
    @Column(name = "browser")
    private String browser;
    @Size(max = 255)
    @Column(name = "browser_group")
    private String browserGroup;
    @Size(max = 255)
    @Column(name = "browser_version")
    private String browserVersion;
    @Size(max = 255)
    @Column(name = "os")
    private String operatingSystem;
    @Size(max = 255)
    @Column(name = "os_group")
    private String operatingSystemGroup;
    @Size(max = 255)
    @Column(name = "mac_address")
    private String macAddress;
    @Size(max = 255)
    @Column(name = "request_url")
    private String requestUrl;

    public MobileOfferClickthrough() {
        this.id = 0L;
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

    public MobileOffer getMobileOffer() {
        return mobileOffer;
    }

    public void setMobileOffer(MobileOffer mobileOffer) {
        this.mobileOffer = mobileOffer;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
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

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
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

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
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
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MobileOfferClickthrough other = (MobileOfferClickthrough) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
