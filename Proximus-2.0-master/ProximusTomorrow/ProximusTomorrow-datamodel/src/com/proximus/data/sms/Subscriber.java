/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Brand;
import com.proximus.data.Company;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
@Table(name = "subscriber")
@XmlRootElement(name = "subscriber")
@XmlAccessorType(XmlAccessType.FIELD)
public class Subscriber implements Serializable {

    private static final long serialVersionUID = 1L;
    /*
     * Statuses that we use for opting in a subscriber to be tracked
     */
    public static final String GENDER_MALE = "MALE";
    public static final String GENDER_FEMALE = "FEMALE";
    public static final String STATUS_CARRIER_UNSUPPORTED = "CARRIER_UNSUPPORTED";
     public static final String STATUS_SMS_UNSUPPORTED = "SMS_UNSUPPORTED";
    public static final String STATUS_TRACKING_PENDING = "TRACKING_PENDING";
    public static final String STATUS_OPT_IN_COMPLETE = "OPT_IN_COMPLETE";
    public static final String STATUS_OPT_OUT = "OPT_OUT";
    public static final String STATUS_SMS_OPT_IN_PENDING = "SMS_OPT_IN_PENDING";
    public static final String STATUS_TRACKING_FAILED = "TRACKING_FAILED";
    public static final String STATUS_CANCELLATION_FAILED = "CANCELLATION_FAILED";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "registration_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date registrationDate;
    @XmlTransient
    @Column(name = "sms_opt_in_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date smsOptInDate;
    @XmlTransient
    @Column(name = "sms_opt_out_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date smsOptOutDate;
    @XmlTransient
    @Column(name = "tracking_opt_in_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date trackingOptInDate;
    @XmlTransient
    @Column(name = "tracking_opt_out_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date trackingOptOutDate;
    @XmlTransient
    @Column(name = "next_lookup_request")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date nextLookupRequest;
    @Column(name = "msisdn", unique = true)
    protected String msisdn;
    @Column(name = "email")
    private String email;
    @Column(name = "zipcode")
    private String zipcode;
    @Column(name = "gender")
    private String gender;
    @Column(name = "status")
    private String status;
    @XmlTransient
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "subscriber_category", joinColumns =
    @JoinColumn(name = "subscriber_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "category_id", referencedColumnName = "id"))
    private List<Category> categories;
    @XmlTransient
    @ManyToOne
    private Keyword keyword;
    @XmlTransient
    @ManyToOne
    private Company company;
    
    @XmlTransient
    @ManyToOne
    private Brand brand;
    @XmlTransient
    @ManyToOne
    private Carrier carrier;
    @XmlTransient
    @Column(name = "distance_away_from_geo_fence")
    private Double distanceAwayFromGeoFence;
    @XmlTransient
    @Column(name = "current_closest_geo_fence_id")
    private Long currentClosestGeoFenceId;
    @XmlTransient
    @Column(name = "locaid_status")
    private String locaidStatus;
    @ManyToMany
    @JoinTable(name = "subscriber_locale", joinColumns =
    @JoinColumn(name = "subscriber_id", referencedColumnName = "id"), inverseJoinColumns =
    @JoinColumn(name = "locale_id", referencedColumnName = "id"))
    @XmlTransient
    private List<Locale> locales;
    @XmlTransient
    @Column(name = "synched_with_locaid")
    private boolean synchedWithLocaid;
    
    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "opt_in_property")
    private Property optInProperty;

    public Subscriber() {
        this.id = 0L;
        this.synchedWithLocaid = true;
    }

    public Subscriber(Date registrationDate, String phoneNumber, Boolean smsOptIn, Boolean trackingOptIn, Carrier carrier) {
        this.id = 0L;
        this.synchedWithLocaid = true;
        this.registrationDate = registrationDate;
        this.msisdn = phoneNumber;
        this.carrier = carrier;
    }

    public Subscriber(String phoneNumber, Carrier carrier) {
        this.id = 0L;
        this.synchedWithLocaid = true;
        this.msisdn = phoneNumber;
        this.carrier = carrier;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Carrier getCarrier() {
        return carrier;
    }

    public void setCarrier(Carrier carrier) {
        this.carrier = carrier;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocaidStatus() {
        return locaidStatus;
    }

    public void setLocaidStatus(String locaidStatus) {
        this.locaidStatus = locaidStatus;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
    
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getSmsOptInDate() {
        return smsOptInDate;
    }

    public void setSmsOptInDate(Date smsOptInDate) {
        this.smsOptInDate = smsOptInDate;
    }

    public Date getSmsOptOutDate() {
        return smsOptOutDate;
    }

    public void setSmsOptOutDate(Date smsOptOutDate) {
        this.smsOptOutDate = smsOptOutDate;
    }

    public Date getTrackingOptInDate() {
        return trackingOptInDate;
    }

    public void setTrackingOptInDate(Date trackingOptInDate) {
        this.trackingOptInDate = trackingOptInDate;
    }

    public Date getTrackingOptOutDate() {
        return trackingOptOutDate;
    }

    public void setTrackingOptOutDate(Date trackingOptOutDate) {
        this.trackingOptOutDate = trackingOptOutDate;
    }

    public List<Category> getCategories() {
        if (this.categories == null) {
            this.categories = new ArrayList<Category>();
        }
        return this.categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void addCategory(Category category) {
        getCategories().add(category);
    }

    public void removeCategory(Category category) {
        if (getCategories().contains(category)) {
            getCategories().remove(category);
        }
    }

    public void clearCategories() {
        this.categories = new ArrayList<Category>();
    }

    public Double getDistanceAwayFromGeoFence() {
        return distanceAwayFromGeoFence;
    }

    public void setDistanceAwayFromGeoFence(Double distanceAwayFromGeoFence) {
        this.distanceAwayFromGeoFence = distanceAwayFromGeoFence;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
    }

    public boolean isSynchedWithLocaid() {
        return synchedWithLocaid;
    }

    public void setSynchedWithLocaid(boolean synchedWithLocaid) {
        this.synchedWithLocaid = synchedWithLocaid;
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public void setLocales(List<Locale> locales) {
        this.locales = locales;
    }

    public void addLocale(Locale locale) {
        if (this.locales == null) {
            this.locales = new ArrayList<Locale>();
        }
        this.locales.add(locale);
    }

    public void clearLocale() {
        this.locales = new ArrayList<Locale>();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Subscriber other = (Subscriber) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "[" + this.msisdn + "|" + this.getStatus() + "]";
    }

    public Date getNextLookupRequest() {
        return nextLookupRequest;
    }

    public void setNextLookupRequest(Date nextLookupRequest) {
        this.nextLookupRequest = nextLookupRequest;
    }

    public Long getCurrentClosestGeoFenceId() {
        return currentClosestGeoFenceId;
    }

    public void setCurrentClosestGeoFenceId(Long currentClosestGeoFenceId) {
        this.currentClosestGeoFenceId = currentClosestGeoFenceId;
    }

    public Property getOptInProperty() {
        return optInProperty;
    }

    public void setOptInProperty(Property optInProperty) {
        this.optInProperty = optInProperty;
    }
    
    
}
