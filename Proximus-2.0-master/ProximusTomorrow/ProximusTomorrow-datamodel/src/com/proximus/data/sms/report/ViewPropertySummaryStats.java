/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import com.proximus.data.sms.Property;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Gilberto Gaxiola
 */
@Entity
@Table(name = "view_property_summary_stats")
public class ViewPropertySummaryStats implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "property_id")
    private Long propertyId = 0L;
    @Column(name = "messages_sent")
    private Long messagesSent;
    @Column(name = "opt_ins")
    private Long optIns;
    @Column(name = "retailers")
    private Long retailers;
    @Column(name = "active_offers")
    private Long activeOffers;
    @Column(name = "retailers_with_offers")
    private Long retailersWithOffers;
    
    @Transient
    private Property property;

    public ViewPropertySummaryStats() {
        this.propertyId = 0L;
        this.activeOffers = 0L;
        this.optIns = 0L;
        this.messagesSent = 0L;
        this.retailers = 0L;
        this.retailersWithOffers = 0L;
        this.property = null;

    }

    public Long getActiveOffers() {
        return activeOffers;
    }

    public void setActiveOffers(Long activeOffers) {
        this.activeOffers = activeOffers;
    }

    public Long getMessagesSent() {
        return messagesSent;
    }

    public void setMessagesSent(Long messagesSent) {
        this.messagesSent = messagesSent;
    }

    public Long getOptIns() {
        return optIns;
    }

    public void setOptIns(Long optIns) {
        this.optIns = optIns;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getRetailers() {
        return retailers;
    }

    public void setRetailers(Long retailers) {
        this.retailers = retailers;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Long getRetailersWithOffers() {
        return retailersWithOffers;
    }

    public void setRetailersWithOffers(Long retailersWithOffers) {
        this.retailersWithOffers = retailersWithOffers;
    }
    
    
    
    @Override
    public String toString() {
        return this.getProperty().getName() + " (" + this.getProperty().getStateProvince()+")";
    } 
}
