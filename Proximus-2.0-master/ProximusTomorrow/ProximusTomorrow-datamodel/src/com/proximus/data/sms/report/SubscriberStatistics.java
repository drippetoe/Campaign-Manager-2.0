/**
 * Copyright (c) 2010-2012 Proximus Mobility LLC
 */
package com.proximus.data.sms.report;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Angela Mercer
 */
@Entity
@Table(name = "subscriber_statistics")
public class SubscriberStatistics implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Long id;
    @Column(name = "msisdn")
    private String msisdn;
    @Column(name = "event_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date eventDate;
    @Column(name = "total_messages_sent")
    private Long totalMessagesSent;
    @Column(name = "time_between_offers")
    private Double timeBetweenOffers;
    @Column(name = "distance_away_from_geo_fence")
    private Double distanceAwayFromGeoFence;

    public SubscriberStatistics() {
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

    public Long getTotalMessagesSent() {
        return totalMessagesSent;
    }

    public void setTotalMessagesSent(Long totalMessagesSent) {
        this.totalMessagesSent = totalMessagesSent;
    }

    public Double getDistanceAwayFromGeoFence() {
        return distanceAwayFromGeoFence;
    }

    public void setDistanceAwayFromGeoFence(Double distanceAwayFromGeoFence) {
        this.distanceAwayFromGeoFence = distanceAwayFromGeoFence;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public Double getTimeBetweenOffers() {
        return timeBetweenOffers;
    }

    public void setTimeBetweenOffers(Double timeBetweenOffers) {
        this.timeBetweenOffers = timeBetweenOffers;
    }
}
