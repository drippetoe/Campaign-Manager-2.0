/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms;

import com.proximus.data.Brand;
import com.proximus.data.DayParts;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "mobile_offer_settings")
public class MobileOfferSettings implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    protected Long id = 0L;
    @Basic(optional = false)
    @Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModified;
    // all times in milliseconds, distance in miles
    @Column(name = "min_time_between_msgs") //-
    protected long minTimeBetweenMessages;
    @Column(name = "min_time_between_lookups") //-
    protected Long minLookupWaitTime;
    @Column(name = "expiration_multiplier")
    protected Long expirationMultiplier;
    @Column(name = "max_msgs_per_mo")
    protected Integer maxMessagesPerMonth;
    @Column(name = "max_msgs_per_sub")
    protected Integer maxMessagesPerSubscriberPerMonth;
    @Column(name = "max_time_between_lookups")
    protected Long maxLookupWaitTime;
    @Column(name = "max_lookups_per_mo")
    protected Integer maxLookupsPerMonth;
    @Column(name = "max_lookups_per_mo_per_sub")
    protected Integer maxLookupsPerMonthPerSubscriber;
    @Column(name = "max_distance")
    protected Long maxDistance = 300L;
    @Column(name="geofence_date_sizing_slope")
    protected Double geofenceDateSizingSlope = 0.0991;
    @Column(name="geofence_date_sizing_intercept")
    protected Double geofenceDateSizingIntercept = -0.0543;
    @Column(name = "offer_header")
    protected String offerHeader;
    @Column(name = "offer_url")
    protected String offerUrl;
    @Column(name = "master_campaign_id")
    protected String masterCampaignId;
    @Column(name = "help_custom_message", length = 160)
    protected String helpCustomMessage;
    @Column(name = "stop_custom_message", length = 160)
    protected String stopCustomMessage;
    @ManyToOne
    protected Brand brand;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mobile_offer_settings")
    List<DayParts> dayParts;
    @OneToOne
    private ShortCode shortCode;
    @Column(name = "live_geo_location")
    private boolean liveGeoLocation;
    @Column(name = "geolocation_zipcode", length=10)
    private String geoLocationZipCode;
    @Column(name = "mobile_offer_balance")
    private int mobileOfferBalance;
    @Transient
    private static final Long MIN_LOOKUP_INTERVAL = 1000 * 60 * 15L;

    public MobileOfferSettings() {
        id = 0L;
        dayParts = new ArrayList<DayParts>();
        dayParts.add(new DayParts());
        liveGeoLocation = false;


        minTimeBetweenMessages = 3 * 24 * 60 * 60 * 1000L;
        minLookupWaitTime = 1000 * 60 * 60L;
        expirationMultiplier = 4L;

        maxMessagesPerMonth = 10000;
        maxMessagesPerSubscriberPerMonth = 3;

        maxLookupWaitTime = 2 * 24 * 60 * 60 * 1000L;
        maxLookupsPerMonth = 5000;
        maxLookupsPerMonthPerSubscriber = 26;

        maxDistance = 300L;
        mobileOfferBalance = 10;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

   

    public long getMinTimeBetweenMessages() {
        return minTimeBetweenMessages;
    }

    public void setMinTimeBetweenMessages(long minTimeBetweenMessages) {
        this.minTimeBetweenMessages = minTimeBetweenMessages;
    }

    public List<DayParts> getDayParts() {
        return dayParts;
    }

    public void setDayParts(List<DayParts> dayParts) {
        this.dayParts = dayParts;
    }

    public void addDayPart() {
        if (this.dayParts == null) {
            this.dayParts = new ArrayList<DayParts>();
        }
        this.dayParts.add(new DayParts());

    }

    public void removeDayPart(DayParts daypart) {
        if (this.dayParts != null) {
            dayParts.remove(daypart);
        }
    }

    public Long getExpirationMultiplier() {
        return expirationMultiplier;
    }

    public void setExpirationMultiplier(Long expirationMultiplier) {
        this.expirationMultiplier = expirationMultiplier;
    }

    public Long getMaxDistance() {
        return maxDistance;
    }

    public Long getMaxLookupWaitTime() {
        return maxLookupWaitTime;
    }

    public void setMaxLookupWaitTime(Long maxLookupWaitTime) {
        this.maxLookupWaitTime = maxLookupWaitTime;
    }

    public void setMaxDistance(Long maxDistance) {
        this.maxDistance = maxDistance;
    }

    public Integer getMaxLookupsPerMonthPerSubscriber() {
        return maxLookupsPerMonthPerSubscriber;
    }

    public void setMaxLookupsPerMonthPerSubscriber(Integer maxLookupsPerMonthPerSubscriber) {
        this.maxLookupsPerMonthPerSubscriber = maxLookupsPerMonthPerSubscriber;
    }

    public Integer getMaxMessagesPerSubscriberPerMonth() {
        return maxMessagesPerSubscriberPerMonth;
    }

    public void setMaxMessagesPerSubscriberPerMonth(Integer maxMessagesPerSubscriberPerMonth) {
        this.maxMessagesPerSubscriberPerMonth = maxMessagesPerSubscriberPerMonth;
    }

    public Integer getMaxLookupsPerMonth() {
        return maxLookupsPerMonth;
    }

    public void setMaxLookupsPerMonth(Integer maxLookupsPerMonth) {
        this.maxLookupsPerMonth = maxLookupsPerMonth;
    }

    public Integer getMaxMessagesPerMonth() {
        return maxMessagesPerMonth;
    }

    public void setMaxMessagesPerMonth(Integer maxMessages) {
        this.maxMessagesPerMonth = maxMessages;
    }

    public ShortCode getShortCode() {
        return shortCode;
    }

    public void setShortCode(ShortCode shortCode) {
        this.shortCode = shortCode;
    }

    public String getOfferHeader() {
        return offerHeader;
    }

    public void setOfferHeader(String offerHeader) {
        this.offerHeader = offerHeader;
    }

    public String getOfferUrl() {
        return offerUrl;
    }

    public void setOfferUrl(String offerUrl) {
        this.offerUrl = offerUrl;
    }

    /**
     * We have a bare minimum so that nobody does anything stupid, 15 minutes
     *
     * @return min lookup wait time, in milliseconds
     */
    public Long getMinLookupWaitTime() {
        if (minLookupWaitTime < MIN_LOOKUP_INTERVAL) {
            minLookupWaitTime = MIN_LOOKUP_INTERVAL;
        }
        return minLookupWaitTime;
    }

    public void setMinLookupWaitTime(Long minLookupWaitTime) {
        this.minLookupWaitTime = minLookupWaitTime;
    }

    public boolean isLiveGeoLocation() {
        return liveGeoLocation;
    }

    public void setLiveGeoLocation(boolean liveGeoLocation) {
        this.liveGeoLocation = liveGeoLocation;
    }

    public String getHelpCustomMessage() {
        return helpCustomMessage;
    }

    public void setHelpCustomMessage(String helpCustomMessage) {
        this.helpCustomMessage = helpCustomMessage;
    }

    public String getStopCustomMessage() {
        return stopCustomMessage;
    }

    public void setStopCustomMessage(String stopCustomMessage) {
        this.stopCustomMessage = stopCustomMessage;
    }

    public String getGeoLocationZipCode() {
        return geoLocationZipCode;
    }

    public void setGeoLocationZipCode(String geoLocationZipCode) {
        this.geoLocationZipCode = geoLocationZipCode;
    }

    public Double getGeofenceDateSizingIntercept() {
        return geofenceDateSizingIntercept;
    }

    public void setGeofenceDateSizingIntercept(Double geofenceDateSizingIntercept) {
        this.geofenceDateSizingIntercept = geofenceDateSizingIntercept;
    }

    public Double getGeofenceDateSizingSlope() {
        return geofenceDateSizingSlope;
    }

    public void setGeofenceDateSizingSlope(Double geofenceDateSizingSlope) {
        this.geofenceDateSizingSlope = geofenceDateSizingSlope;
    }

    public String getMasterCampaignId()
    {
        return masterCampaignId;
    }

    public void setMasterCampaignId(String masterCampaignId)
    {
        this.masterCampaignId = masterCampaignId;
    }

    public int getMobileOfferBalance() {
        return mobileOfferBalance;
    }

    public void setMobileOfferBalance(int mobileOfferBalance) {
        this.mobileOfferBalance = mobileOfferBalance;
    }
    
    
    
    
}