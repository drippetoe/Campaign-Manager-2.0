/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author ronald
 */
@Entity
@Table(name = "mobile_offer_send_log")
@XmlRootElement(name = "mobile_offer_send_log")
@XmlAccessorType(XmlAccessType.FIELD)
public class MobileOfferSendLog implements Serializable {

    public static final String STATUS_FAILED = "Delivery Failed";
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_DELIVERED = "Delivered";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Basic(optional = false)
    @Column(name = "event_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;
    @XmlTransient
    @Column(name = "status")
    private String status;
    @Column(name = "msisdn")
    protected String msisdn;
    @XmlTransient
    @ManyToOne
    private Subscriber subscriber;
    @ManyToOne
    private MobileOffer mobileOffer;
    @XmlTransient
    @ManyToOne
    private Company company;
    @XmlTransient
    @ManyToOne
    private Brand brand;
    @ManyToOne
    @XmlTransient
    private Property property;
    @Column(name = "geo_fence_id")
    private Long geofenceId;

    public MobileOfferSendLog() {
        this.eventDate = new Date();
        this.status = MobileOfferSendLog.STATUS_PENDING;
        this.id = 0L;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public MobileOffer getMobileOffer() {
        return mobileOffer;
    }

    public void setMobileOffer(MobileOffer mobileOffer) {
        this.mobileOffer = mobileOffer;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getGeofenceId() {
        return geofenceId;
    }

    public void setGeofenceId(Long geofenceId) {
        this.geofenceId = geofenceId;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
    
    
    

    @Override
    public String toString() {
        return "Mobile Offer Send Log: " + "[" + this.getId() + "] [" + this.getMsisdn() + "] [" + this.mobileOffer.getName() + "] [" + this.eventDate + "]";
    }
}
