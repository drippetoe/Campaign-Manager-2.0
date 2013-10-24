/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.sms.Property;

/**
 *
 * @author Gilberto Gaxiola
 */
public class PropertyStatsAgreggator {
    
    private ViewPropertySummaryStats propStats;
    private Long optInsCurrMonth = 0L;
    private Long optInsPrevMonth = 0L;
    private Long messagesSentCurrMonth = 0L;
    private Long messagesSentPrevMonth = 0L;

    public Long getMessagesSentCurrMonth() {
        return messagesSentCurrMonth;
    }

    public void setMessagesSentCurrMonth(Long messagesSentCurrMonth) {
        this.messagesSentCurrMonth = messagesSentCurrMonth;
    }

    public Long getMessagesSentPrevMonth() {
        return messagesSentPrevMonth;
    }

    public void setMessagesSentPrevMonth(Long messagesSentPrevMonth) {
        this.messagesSentPrevMonth = messagesSentPrevMonth;
    }

    public ViewPropertySummaryStats getPropStats() {
        return propStats;
    }

    public void setPropStats(ViewPropertySummaryStats propStats) {
        this.propStats = propStats;
    }

    public Long getOptInsCurrMonth() {
        return optInsCurrMonth;
    }

    public void setOptInsCurrMonth(Long optInsCurrMonth) {
        this.optInsCurrMonth = optInsCurrMonth;
    }

    public Long getOptInsPrevMonth() {
        return optInsPrevMonth;
    }

    public void setOptInsPrevMonth(Long optInsPrevMonth) {
        this.optInsPrevMonth = optInsPrevMonth;
    }

    public Long getActiveOffers() {
        return getPropStats().getActiveOffers();
    }

    public void setActiveOffers(Long activeOffers) {
        getPropStats().setActiveOffers(activeOffers);
    }

    public Long getMessagesSent() {
        return getPropStats().getMessagesSent();
    }

    public void setMessagesSent(Long messagesSent) {
        getPropStats().setMessagesSent(messagesSent);
    }

    public Long getOptIns() {
        return getPropStats().getOptIns();
    }

    public void setOptIns(Long optIns) {
        getPropStats().setOptIns(optIns);
    }

    public Long getPropertyId() {
        return getPropStats().getPropertyId();
    }

    public void setPropertyId(Long propertyId) {
        getPropStats().setPropertyId(propertyId);
    }

    public Long getRetailers() {
        return getPropStats().getRetailers();
    }

    public void setRetailers(Long retailers) {
        getPropStats().setRetailers(retailers);
    }

    public Property getProperty() {
        return getPropStats().getProperty();
    }

    public void setProperty(Property property) {
        getPropStats().setProperty(property);
    }

    public Long getRetailersWithOffers() {
        return getPropStats().getRetailersWithOffers();
    }

    public void setRetailersWithOffers(Long retailersWithOffers) {
        getPropStats().setRetailersWithOffers(retailersWithOffers);
    }
    
}
